package com.cojac.storyteller.user.controller;

import com.cojac.storyteller.response.code.ResponseCode;
import com.cojac.storyteller.common.swagger.UserControllerDocs;
import com.cojac.storyteller.response.dto.ResponseDTO;
import com.cojac.storyteller.user.dto.oauth.GoogleLoginRequestDTO;
import com.cojac.storyteller.user.dto.oauth.KakaoLoginRequestDTO;
import com.cojac.storyteller.user.service.UserService;
import com.cojac.storyteller.user.dto.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

@RestController
@RequiredArgsConstructor
public class UserController implements UserControllerDocs {

    private final UserService userService;

    /**
     * 카카오 소셜 로그인
     */
    @PostMapping("/kakao-login")
    public ResponseEntity<ResponseDTO> kakaoLogin(@RequestBody @Valid KakaoLoginRequestDTO kakaoLoginRequestDTO, HttpServletResponse response) {
        SocialUserDTO res = userService.kakaoLogin(kakaoLoginRequestDTO, response);
        return ResponseEntity
                .status(ResponseCode.SUCCESS_KAKAO_LOGIN.getStatus().value())
                .body(new ResponseDTO<>(ResponseCode.SUCCESS_KAKAO_LOGIN, res));
    }

    /**
     * 구글 소셜 로그인
     */
    @PostMapping("/google-login")
    public ResponseEntity<ResponseDTO> googleLogin(@RequestBody @Valid GoogleLoginRequestDTO googleLoginRequestDTO, HttpServletResponse response) throws Exception {
        SocialUserDTO res = userService.googleLogin(googleLoginRequestDTO, response);
        return ResponseEntity
                .status(ResponseCode.SUCCESS_GOOGLE_LOGIN.getStatus().value())
                .body(new ResponseDTO<>(ResponseCode.SUCCESS_GOOGLE_LOGIN, res));
    }

    /**
     * 자체 회원가입
     */
    @PostMapping("/register")
    public ResponseEntity<ResponseDTO> registerUser(@ParameterObject @Valid CreateUserRequestDTO createUserRequestDTO) {
        LocalUserDTO res = userService.registerUser(createUserRequestDTO);
        return ResponseEntity
                .status(ResponseCode.SUCCESS_REGISTER.getStatus().value())
                .body(new ResponseDTO<>(ResponseCode.SUCCESS_REGISTER, res));
    }

    /**
     * 아이디 중복 확인
     */
    @PostMapping("/username/verifications")
    public ResponseEntity<ResponseDTO> verifiedUsername(@Valid @RequestBody UsernameDTO usernameDTO) {

        UsernameDTO res = userService.verifiedUsername(usernameDTO);
        return ResponseEntity
                .status(ResponseCode.SUCCESS_VERIFICATION_USERNAME.getStatus().value())
                .body(new ResponseDTO<>(ResponseCode.SUCCESS_VERIFICATION_USERNAME, res));
    }

    /**
     * 토큰 재발급
     */
    @PostMapping("/reissue")
    public ResponseEntity<ResponseDTO> reissueAccessToken(HttpServletRequest request,
                                               HttpServletResponse response,
                                               @Valid @RequestBody ReissueDTO reissueDTO) throws IOException {
        UserDTO res = userService.reissueToken(request, response, reissueDTO);
        return ResponseEntity
                .status(ResponseCode.SUCCESS_REISSUE.getStatus().value())
                .body(new ResponseDTO<>(ResponseCode.SUCCESS_REISSUE, res));
    }

    /**
     * 이메일 인증 코드 요청하기
     */
    @PostMapping("/emails/verification-requests")
    public ResponseEntity<ResponseDTO> sendEmailVerification(@Valid @RequestBody EmailDTO emailDTO) {

        userService.sendCodeToEmail(emailDTO.getEmail());
        return ResponseEntity
                .status(ResponseCode.SUCCESS_VERIFICATION_REQUEST.getStatus().value())
                .body(new ResponseDTO<>(ResponseCode.SUCCESS_VERIFICATION_REQUEST, null));
    }

    /**
     * 인증 코드 확인하기
     */
    @PostMapping("/emails/verifications")
    public ResponseEntity<ResponseDTO> verificationEmailCode(@Valid @RequestBody EmailDTO emailDTO) {

        EmailDTO res = userService.verifiedCode(emailDTO.getEmail(), emailDTO.getAuthCode());
        return ResponseEntity
                .status(ResponseCode.SUCCESS_VERIFICATION_CODE.getStatus().value())
                .body(new ResponseDTO<>(ResponseCode.SUCCESS_VERIFICATION_CODE, res));
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
