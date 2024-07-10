package com.cojac.storyteller.controller;

import com.cojac.storyteller.code.ErrorCode;
import com.cojac.storyteller.code.ResponseCode;
import com.cojac.storyteller.dto.response.ResponseDTO;
import com.cojac.storyteller.dto.user.UserDTO;
import com.cojac.storyteller.dto.user.UsernameDTO;
import com.cojac.storyteller.exception.AccessTokenExpiredException;
import com.cojac.storyteller.exception.RequestParsingException;
import com.cojac.storyteller.jwt.JWTUtil;
import com.cojac.storyteller.service.RedisService;
import com.cojac.storyteller.service.UserService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.util.Collection;
import java.util.Iterator;

@RestController
@RequiredArgsConstructor
public class UserController {

    public static final String REFRESH_TOKEN_PREFIX = "refresh_token:";
    private static final long ACCESS_TOKEN_EXPIRATION = 86400000L; // 24 hours
    private static final long REFRESH_TOKEN_EXPIRATION = 1209600000L; // 14 days

    private final UserService userService;
    private final JWTUtil jwtUtil;
    private final RedisService redisService;

    /**
     * 자체 회원가입
     */
    @PostMapping("/register")
    public ResponseEntity<ResponseDTO> registerUser(UserDTO userDTO) {
        UserDTO res = userService.registerUser(userDTO);
        return ResponseEntity
                .status(ResponseCode.SUCCESS_REGISTER.getStatus().value())
                .body(new ResponseDTO<>(ResponseCode.SUCCESS_REGISTER, res));
    }

    /**
     * 아이디 중복 확인
     */
    @PostMapping("/check-username")
    public ResponseEntity<ResponseDTO> checkUsername(@Valid @RequestBody UsernameDTO usernameDTO) {

        UsernameDTO res = userService.checkUsername(usernameDTO);
        return ResponseEntity
                .status(ResponseCode.SUCCESS_CHECK_USERNAME.getStatus().value())
                .body(new ResponseDTO<>(ResponseCode.SUCCESS_CHECK_USERNAME, res));
    }

    /**
     * 토큰 재발급
     */
    @PostMapping("/reissue")
    public ResponseEntity<ResponseDTO> reissueAccessToken(HttpServletRequest request,
                                               HttpServletResponse response,
                                               @Valid @RequestBody UsernameDTO usernameDTO) {
        // 헤더에서 refresh키에 담긴 토큰을 꺼냄
        String refreshToken = request.getHeader("refresh");

        if (refreshToken == null) {
            throw new RequestParsingException(ErrorCode.TOKEN_MISSING);
        }

        // 유효 기간 확인
        try {
            jwtUtil.isExpired(refreshToken);
        } catch (ExpiredJwtException e) {
            throw new AccessTokenExpiredException(ErrorCode.TOKEN_EXPIRED);
        }

        // 토큰이 refresh 토큰인지 확인
        String category = jwtUtil.getCategory(refreshToken);
        if (!category.equals("refresh")) {
            throw new RequestParsingException(ErrorCode.INVALID_ACCESS_TOKEN);
        }

        // Redis 키값
        String refreshTokenKey = REFRESH_TOKEN_PREFIX + usernameDTO.getUsername();

        // Redis에 저장되어 있는지 확인
        String values = redisService.getValues(refreshTokenKey);
        if (values == null || values.isEmpty()) {
            throw new RequestParsingException(ErrorCode.TOKEN_EXPIRED);
        }

        String username = jwtUtil.getUsername(refreshToken);
        String role = jwtUtil.getRole(refreshToken);

        // token 새롭게 생성
        String newAccess = jwtUtil.createJwt("self", "access", username, role, ACCESS_TOKEN_EXPIRATION);
        String newRefresh = jwtUtil.createJwt("self", "refresh", username, role, REFRESH_TOKEN_EXPIRATION);

        //Refresh 토큰 저장 Redis에 기존의 Refresh 토큰 삭제 후 새 Refresh 토큰 저장
        redisService.deleteValues(refreshTokenKey);
        redisService.setValues(refreshTokenKey, refreshToken, Duration.ofMillis(REFRESH_TOKEN_EXPIRATION));

        //response
        response.setHeader("access", newAccess);
        UserDTO userDTO = new UserDTO();
        userDTO.setRefreshToken(newRefresh);

        return ResponseEntity
                .status(ResponseCode.SUCCESS_REISSUE.getStatus().value())
                .body(new ResponseDTO<>(ResponseCode.SUCCESS_REISSUE, userDTO));
    }

    // 로그인 이후 유저 아이디 및 role 확인 방법
    @GetMapping("test")
    public ResponseEntity test() {
        // 사용자 아이디(username)
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        // 사용자 role
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iter = authorities.iterator();
        GrantedAuthority auth = iter.next();
        String role = auth.getAuthority();

        String res = String.format("{username: %s, role: %s}", username, role);

        return ResponseEntity
                .status(ResponseCode.SUCCESS_TEST.getStatus().value())
                .body(new ResponseDTO<>(ResponseCode.SUCCESS_TEST, res));
    }
}
