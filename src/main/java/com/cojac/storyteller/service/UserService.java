package com.cojac.storyteller.service;

import com.cojac.storyteller.code.ErrorCode;
import com.cojac.storyteller.domain.LocalUserEntity;
import com.cojac.storyteller.domain.SocialUserEntity;
import com.cojac.storyteller.dto.user.*;
import com.cojac.storyteller.exception.*;
import com.cojac.storyteller.jwt.JWTUtil;
import com.cojac.storyteller.repository.LocalUserRepository;
import com.cojac.storyteller.repository.SocialUserRepository;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class UserService {

    private static final String REFRESH_TOKEN_PREFIX = "refresh_token:";
    private static final String EMAIL_CODE_PREFIX = "email_code:";
    private static final long ACCESS_TOKEN_EXPIRATION = 86400000L; // 24 hours
    private static final long REFRESH_TOKEN_EXPIRATION = 1209600000L; // 14 days

    private final LocalUserRepository localUserRepository;
    private final SocialUserRepository socialUserRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JWTUtil jwtUtil;
    private final RedisService redisService;
    private final MailService mailService;
    @Value("${spring.mail.auth-code-expiration-millis}")
    private long authCodeExpirationMillis;

    /**
     * 카카오 소셜 로그인
     */
    @Transactional
    public SocialUserDTO kakaoLogin(KakaoLoginRequestDTO kakaoLoginRequestDTO, HttpServletResponse response) {

        String accountId = "kakao " + kakaoLoginRequestDTO.getId();
        String username = kakaoLoginRequestDTO.getUsername();
        String email = kakaoLoginRequestDTO.getEmail();
        String role = kakaoLoginRequestDTO.getRole();

        SocialUserEntity socialUserEntity = findOrCreateSocialUser(accountId, username, email, role);
        socialUserRepository.save(socialUserEntity);

        //토큰 생성
        String accessToken = jwtUtil.createJwt("social", "access", accountId, role, ACCESS_TOKEN_EXPIRATION);
        String refreshToken = jwtUtil.createJwt("social", "refresh", accountId, role, REFRESH_TOKEN_EXPIRATION);

        // Redis에 refresh 토큰 저장
        String refreshTokenKey = REFRESH_TOKEN_PREFIX + username;
        redisService.setValues(refreshTokenKey, refreshToken, Duration.ofMillis(REFRESH_TOKEN_EXPIRATION));

        // 응답 헤더에 JWT 설정
        response.setHeader("access", accessToken);
        response.setHeader("refresh", refreshToken);

        SocialUserDTO socialUserDTO = SocialUserDTO.mapToSocialUserDTO(socialUserEntity);
        return socialUserDTO;
    }

    private SocialUserEntity findOrCreateSocialUser(String accountId, String username, String email, String role) {
        return socialUserRepository.findByAccountId(accountId)
                .map(existingUser -> {
                    existingUser.updateUsername(username);
                    existingUser.updateEmail(email);
                    return existingUser;
                })
                .orElseGet(() -> new SocialUserEntity(accountId, username, email, role));
    }

    /**
     * 회원 등록하기
     */
    @Transactional
    public LocalUserDTO registerUser(LocalUserDTO localUserDTO) {
        String username = localUserDTO.getUsername();
        String role = localUserDTO.getRole();
        String email = localUserDTO.getEmail();
        String encryptedPassword = bCryptPasswordEncoder.encode(localUserDTO.getPassword());

        if (localUserRepository.existsByUsername(username)) {
            throw new DuplicateUsernameException(ErrorCode.DUPLICATE_USERNAME);
        }

        LocalUserEntity localUserEntity = LocalUserEntity.builder()
                .username(username)
                .encryptedPassword(encryptedPassword)
                .email(email)
                .role(role)
                .build();
        localUserRepository.save(localUserEntity);

        return LocalUserDTO.builder()
                .id(localUserEntity.getId())
                .username(username)
                .email(email)
                .role(role)
                .build();
    }

    /**
     * 유저 아이디 중복 검증
     */
    public UsernameDTO verifiedUsername(UsernameDTO usernameDTO) {
        String username = usernameDTO.getUsername();
        boolean authResult = localUserRepository.findByUsername(username)
                .map(user -> false)
                .orElse(true);
        return new UsernameDTO(username, authResult);
    }

    /**
     * 토큰 재발급
     */
    public UserDTO reissueToken(HttpServletRequest request, HttpServletResponse response, ReissueDTO reissueDTO) throws IOException {
        String refreshToken = getRefreshTokenFromRequest(request);
        validateToken(refreshToken);
        validateCategory(refreshToken);

        String authenticationMethod = jwtUtil.getAuthenticationMethod(refreshToken);
        if (authenticationMethod.equals("local")) {
            // 자체 로그인 사용자 검증
            return authenticateLocalUser(response, reissueDTO, refreshToken);
        } else if (authenticationMethod.equals("social")) {
            // 소셜 사용자 검증
            return authenticateSocialUser(response, reissueDTO, refreshToken);
        }
        else {
            throw new RequestParsingException(ErrorCode.INVALID_REFRESH_TOKEN);
        }
    }

    /**
     * 인증 코드 검증을 위한 이메일 전송
     */
    public void sendCodeToEmail(String toEmail) {
        this.checkDuplicatedEmail(toEmail);
        String title = "StoryTeller 이메일 인증 번호";
        String authCode = this.createCode();
        mailService.sendEmail(toEmail, title, authCode);

        // 이메일 인증 요청 시 인증 번호 Redis에 저장 ( key = "email_code:" + Email / value = AuthCode )
        redisService.setValues(EMAIL_CODE_PREFIX + toEmail,
                authCode, Duration.ofMillis(authCodeExpirationMillis));
    }

    /**
     * 인증 코드 검증하기
     */
    public EmailDTO verifiedCode(String email, String authCode) {
        this.checkDuplicatedEmail(email);
        String redisAuthCode = redisService.getValues(EMAIL_CODE_PREFIX + email);
        boolean authResult = redisService.checkExistsValue(redisAuthCode) && redisAuthCode.equals(authCode);

        return new EmailDTO(email, authCode, authResult);
    }

    private String getRefreshTokenFromRequest(HttpServletRequest request) {
        String refreshToken = request.getHeader("refresh");
        if (refreshToken == null) {
            throw new RequestParsingException(ErrorCode.TOKEN_MISSING);
        }
        return refreshToken;
    }

    private void validateToken(String refreshToken) {
        try {
            jwtUtil.isExpired(refreshToken);
        } catch (ExpiredJwtException e) {
            throw new AccessTokenExpiredException(ErrorCode.TOKEN_EXPIRED);
        }
    }

    private void validateCategory(String refreshToken) {
        String category = jwtUtil.getCategory(refreshToken);
        if (!category.equals("refresh")) {
            throw new RequestParsingException(ErrorCode.INVALID_REFRESH_TOKEN);
        }
    }

    private UserDTO authenticateLocalUser(HttpServletResponse response, ReissueDTO reissueDTO, String refreshToken) {

        // redis 에 저장되어 있는지 확인
        String refreshTokenKey = hasValueInRedis(reissueDTO.getUsername());

        String username = jwtUtil.getUserKey(refreshToken);
        String role = jwtUtil.getRole(refreshToken);

        // Access token 생성
        String newAccess = jwtUtil.createJwt("local", "access", username, role, ACCESS_TOKEN_EXPIRATION);
        String newRefresh = jwtUtil.createJwt("local", "refresh", username, role, REFRESH_TOKEN_EXPIRATION);

        //Refresh 토큰 저장 DB에 기존의 Refresh 토큰 삭제 후 새 Refresh 토큰 저장
        redisService.deleteValues(refreshTokenKey);
        redisService.setValues(refreshTokenKey, newRefresh);

        //response
        response.setHeader("access", newAccess);
        response.setHeader("refresh", newRefresh);

        LocalUserEntity userEntity = localUserRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(ErrorCode.USER_NOT_FOUND));

        UserDTO userDTO = LocalUserDTO.builder()
                .id(userEntity.getId())
                .username(username)
                .email("email")
                .role(role)
                .build();

        return userDTO;
    }

    private UserDTO authenticateSocialUser(HttpServletResponse response, ReissueDTO reissueDTO, String refreshToken) {

        // redis 에 저장되어 있는지 확인
        String refreshTokenKey = hasValueInRedis(reissueDTO.getAccountId());

        String accountId = jwtUtil.getUserKey(refreshToken);
        String role = jwtUtil.getRole(refreshToken);

        // Access token 생성
        String newAccess = jwtUtil.createJwt("social", "access", accountId, role, ACCESS_TOKEN_EXPIRATION);
        String newRefresh = jwtUtil.createJwt("social", "refresh", accountId, role, REFRESH_TOKEN_EXPIRATION);

        //Refresh 토큰 저장 DB에 기존의 Refresh 토큰 삭제 후 새 Refresh 토큰 저장
        redisService.deleteValues(refreshTokenKey);
        redisService.setValues(refreshTokenKey, newRefresh);

        //response
        response.setHeader("access", newAccess);
        response.setHeader("refresh", newRefresh);

        SocialUserEntity socialUserEntity = socialUserRepository.findByAccountId(accountId)
                .orElseThrow(() -> new UserNotFoundException(ErrorCode.USER_NOT_FOUND));

        return SocialUserDTO.mapToSocialUserDTO(socialUserEntity);
    }

    private String hasValueInRedis(String userKey) {
        String refreshTokenKey = REFRESH_TOKEN_PREFIX + userKey;
        if (!redisService.checkExistsValue(refreshTokenKey)) {
            throw new RequestParsingException(ErrorCode.TOKEN_EXPIRED);
        }
        return refreshTokenKey;
    }

    private void checkDuplicatedEmail(String email) {
        if (localUserRepository.existsByEmail(email) || socialUserRepository.existsByEmail(email)) {
            throw new DuplicateEmailException(ErrorCode.DUPLICATE_EMAIL);
        }
    }

    private String createCode() {
        int len = 6;
        try {
            Random random = SecureRandom.getInstanceStrong();
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < len; i++) {
                builder.append(random.nextInt(10));
            }
            return builder.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new BusinessLogicException(ErrorCode.NO_SUCH_ALGORITHM);
        }
    }
}
