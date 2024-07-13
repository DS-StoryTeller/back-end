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
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
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

    @Transactional
    public LocalUserDTO registerUser(LocalUserDTO localUserDTO) {
        String username = localUserDTO.getUsername();
        String role = localUserDTO.getRole();
        String encryptedPassword = bCryptPasswordEncoder.encode(localUserDTO.getPassword());

        if (localUserRepository.existsByUsername(username)) {
            throw new DuplicateUsernameException(ErrorCode.DUPLICATE_USERNAME);
        }

        LocalUserEntity localUserEntity = new LocalUserEntity(encryptedPassword, username, role);
        localUserRepository.save(localUserEntity);

        return new LocalUserDTO(localUserEntity.getId(), localUserEntity.getUsername(), localUserEntity.getRole());
    }

    public UsernameDTO verifiedUsername(UsernameDTO usernameDTO) {
        String username = usernameDTO.getUsername();
        localUserRepository.findByUsername(username)
                .ifPresent(user -> {
                    throw new UsernameExistsException(ErrorCode.DUPLICATE_USERNAME);
                });
        return new UsernameDTO(username);
    }

    public UserDTO reissueToken(HttpServletRequest request, HttpServletResponse response, @RequestBody ReissueDTO reissueDTO) throws IOException {
        String refreshToken = getRefreshTokenFromRequest(request);
        validateToken(refreshToken);
        String category = jwtUtil.getCategory(refreshToken);

        if (!category.equals("refresh")) {
            throw new RequestParsingException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        String userKey = jwtUtil.getUserKey(refreshToken);
        String role = jwtUtil.getRole(refreshToken);
        String authenticationMethod = jwtUtil.getAuthenticationMethod(refreshToken);

        String refreshTokenKey = getRefreshTokenKey(authenticationMethod, reissueDTO);
        checkTokenExistsInRedis(refreshTokenKey);

        return generateAndSaveNewTokens(response, userKey, role, authenticationMethod, refreshTokenKey);
    }

    public void sendCodeToEmail(String toEmail) {
        this.checkDuplicatedEmail(toEmail);
        String title = "StoryTeller 이메일 인증 번호";
        String authCode = this.createCode();
        mailService.sendEmail(toEmail, title, authCode);

        // 이메일 인증 요청 시 인증 번호 Redis에 저장 ( key = "email_code:" + Email / value = AuthCode )
        redisService.setValues(EMAIL_CODE_PREFIX + toEmail,
                authCode, Duration.ofMillis(this.authCodeExpirationMillis));
    }

    public EmailDTO verifiedCode(String email, String authCode) {
        this.checkDuplicatedEmail(email);
        String redisAuthCode = redisService.getValues(EMAIL_CODE_PREFIX + email);
        boolean authResult = redisService.checkExistsValue(redisAuthCode) && redisAuthCode.equals(authCode);

        return new EmailDTO(email, authCode, authResult);
    }

    private void checkDuplicatedEmail(String email) {
        localUserRepository.findByEmail(email).ifPresent(user -> {
            throw new UserNotFoundException(ErrorCode.USER_NOT_FOUND);
        });
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

    private String getRefreshTokenKey(String authenticationMethod, ReissueDTO reissueDTO) {
        if (authenticationMethod.equals("local")) {
            return REFRESH_TOKEN_PREFIX + reissueDTO.getUsername();
        } else if (authenticationMethod.equals("social")) {
            return REFRESH_TOKEN_PREFIX + reissueDTO.getAccountId();
        } else {
            throw new RefreshTokenExpiredException(ErrorCode.INVALID_REFRESH_TOKEN);
        }
    }

    private void checkTokenExistsInRedis(String refreshTokenKey) {
        if (!redisService.checkExistsValue(refreshTokenKey)) {
            throw new RequestParsingException(ErrorCode.TOKEN_EXPIRED);
        }
    }

    private UserDTO generateAndSaveNewTokens(HttpServletResponse response, String userKey, String role, String authenticationMethod, String refreshTokenKey) {
        String newAccess = jwtUtil.createJwt(authenticationMethod, "access", userKey, role, ACCESS_TOKEN_EXPIRATION);
        String newRefresh = jwtUtil.createJwt(authenticationMethod, "refresh", userKey, role, REFRESH_TOKEN_EXPIRATION);

        redisService.deleteValues(refreshTokenKey);
        redisService.setValues(refreshTokenKey, newRefresh, Duration.ofMillis(REFRESH_TOKEN_EXPIRATION));

        response.setHeader("access", newAccess);
        response.setHeader("refresh", newRefresh);

        return getUserDTO(authenticationMethod, userKey);
    }

    private UserDTO getUserDTO(String authenticationMethod, String userKey) {
        if ("local".equals(authenticationMethod)) {
            LocalUserEntity localUserEntity = localUserRepository.findByUsername(userKey)
                    .orElseThrow(() -> new UserNotFoundException(ErrorCode.USER_NOT_FOUND));
            return new LocalUserDTO(localUserEntity.getId(), localUserEntity.getUsername(), localUserEntity.getRole());
        } else if ("social".equals(authenticationMethod)) {
            SocialUserEntity socialUserEntity = socialUserRepository.findByAccountId(userKey)
                    .orElseThrow(() -> new UserNotFoundException(ErrorCode.USER_NOT_FOUND));
            return new SocialUserDTO(socialUserEntity.getId(), socialUserEntity.getAccountId(), socialUserEntity.getUsername(), socialUserEntity.getRole());
        } else {
            throw new RequestParsingException(ErrorCode.INVALID_REFRESH_TOKEN);
        }
    }
}
