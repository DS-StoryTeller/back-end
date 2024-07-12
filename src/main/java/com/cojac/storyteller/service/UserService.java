package com.cojac.storyteller.service;

import com.cojac.storyteller.code.ErrorCode;
import com.cojac.storyteller.domain.LocalUserEntity;
import com.cojac.storyteller.domain.SocialUserEntity;
import com.cojac.storyteller.dto.user.ReissueDTO;
import com.cojac.storyteller.dto.user.LocalUserDTO;
import com.cojac.storyteller.dto.user.UserDTO;
import com.cojac.storyteller.dto.user.UsernameDTO;
import com.cojac.storyteller.dto.user.oauth2.SocialUserDTO;
import com.cojac.storyteller.exception.*;
import com.cojac.storyteller.jwt.JWTUtil;
import com.cojac.storyteller.repository.LocalUserRepository;
import com.cojac.storyteller.repository.SocialUserRepository;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.IOException;
import java.time.Duration;

@Service
@RequiredArgsConstructor
public class UserService {

    private static final String REFRESH_TOKEN_PREFIX = "refresh_token:";
    private static final long ACCESS_TOKEN_EXPIRATION = 86400000L; // 24 hours
    private static final long REFRESH_TOKEN_EXPIRATION = 1209600000L; // 14 days

    private final LocalUserRepository localUserRepository;
    private final SocialUserRepository socialUserRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JWTUtil jwtUtil;
    private final RedisService redisService;

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

    public UsernameDTO checkUsername(UsernameDTO usernameDTO) {
        String username = usernameDTO.getUsername();
        localUserRepository.findByUsername(username)
                .ifPresent(user -> {
                    throw new UsernameExistsException(ErrorCode.DUPLICATE_USERNAME);
                });
        return new UsernameDTO(username);
    }

    public UserDTO reissueToken(HttpServletRequest request, HttpServletResponse response, @Valid @RequestBody ReissueDTO reissueDTO) throws IOException {
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
