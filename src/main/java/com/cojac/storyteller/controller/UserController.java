package com.cojac.storyteller.controller;

import com.cojac.storyteller.code.ResponseCode;
import com.cojac.storyteller.dto.response.ResponseDTO;
import com.cojac.storyteller.dto.user.*;
import com.cojac.storyteller.service.UserService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "User Controller", description = "유저 관련 API")
public class UserController {

    private final UserService userService;

    /**
     * 자체 회원가입
     */
    @PostMapping("/register")
    @Operation(
            summary = "자체 회원가입",
            description = "자체 회원가입 API",
            responses = {
                    @ApiResponse(responseCode = "200", description = "회원가입을 성공했습니다."),
            }
    )
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
    @Operation(
            summary = "아이디 중복 확인",
            description = "아이디 중복 확인 API",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "확인할 아이디 정보",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            value = "{\"username\": \"사용자의 username\"}"
                                    )
                            }
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "아이디가 사용 검증 완료했습니다. authResult를 확인해주세요."),
            }
    )
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
    @Operation(
            summary = "토큰 재발급",
            description = "토큰 재발급 API",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "재발급 받은 아이디 정보",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "자체 로그인 사용자일 경우",
                                            value = "{\"username\": \"사용자의 username\"}"
                                    ),
                                    @ExampleObject(
                                            name = "소셜 로그인 사용자일 경우",
                                            value = "{\"accountId\": \"사용자의 accountId\"}"
                                    )
                            }
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "토큰 재발급을 성공했습니다."),
            },
            security = @SecurityRequirement(name = "refresh")
    )
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
    @Operation(
            summary = "이메일 인증 코드 요청하기",
            description = "이메일 인증 코드 요청 API",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "인증 코드 요청받을 이메일",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            value = "{\"email\": \"사용자의 email\"}"
                                    )
                            }
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "해당 이메일로 인증 코드가 전송되었습니다."),
            }
    )
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
    @Operation(
            summary = "인증 코드 확인하기",
            description = "인증 코드 확인 API",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "검즈을 위한 이메일과 인증 코드",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            value = "{\"email\": \"사용자의 email\", \"authCode\": \"인증코드\"}"
                                    )
                            }
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "인증 코드가 검증 완료했습니다. authResult를 확인해주세요."),
            }
    )
    public ResponseEntity<ResponseDTO> verificationEmailCode(@Valid @RequestBody EmailDTO emailDTO) {

        EmailDTO res = userService.verifiedCode(emailDTO.getEmail(), emailDTO.getAuthCode());
        return ResponseEntity
                .status(ResponseCode.SUCCESS_VERIFICATION_CODE.getStatus().value())
                .body(new ResponseDTO<>(ResponseCode.SUCCESS_VERIFICATION_CODE, res));
    }

    // 로그인 이후 유저 아이디 및 role 확인 방법
    @Hidden
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
