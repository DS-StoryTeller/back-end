package com.cojac.storyteller.docs;

import com.cojac.storyteller.dto.response.ResponseDTO;
import com.cojac.storyteller.dto.user.CreateUserRequestDTO;
import com.cojac.storyteller.dto.user.EmailDTO;
import com.cojac.storyteller.dto.user.ReissueDTO;
import com.cojac.storyteller.dto.user.UsernameDTO;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.IOException;

@Tag(name = "User Controller", description = "유저 관련 API")
public interface UserControllerDocs {

    /**
     * 자체 회원가입
     */
    @Operation(
            summary = "자체 회원가입",
            description = "자체 회원가입 API",
            responses = {
                    @ApiResponse(responseCode = "200", description = "회원가입을 성공했습니다."),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
                    @ApiResponse(responseCode = "409", description = "중복된 유저 아이디입니다."),
            }
    )
    ResponseEntity<ResponseDTO> registerUser(@ParameterObject @Valid CreateUserRequestDTO createUserRequestDTO);

    /**
     * 아이디 중복 확인
     */
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
                    @ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
            }
    )
    ResponseEntity<ResponseDTO> verifiedUsername(@Valid @RequestBody UsernameDTO usernameDTO);

    /**
     * 토큰 재발급
     */
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
                    @ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
                    @ApiResponse(responseCode = "401", description = "유효하지 않은 Refresh 토큰입니다."),
                    @ApiResponse(responseCode = "401", description = "토큰이 만료되었습니다."),
            },
            security = @SecurityRequirement(name = "refresh")
    )
    ResponseEntity<ResponseDTO> reissueAccessToken(HttpServletRequest request,
                                                   HttpServletResponse response,
                                                   @Valid @RequestBody ReissueDTO reissueDTO) throws IOException;

    /**
     * 이메일 인증 코드 요청하기
     */
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
                    @ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
                    @ApiResponse(responseCode = "500", description = "내부 서버 오류입니다."),

            }
    )
    ResponseEntity<ResponseDTO> sendEmailVerification(@Valid @RequestBody EmailDTO emailDTO);

    /**
     * 인증 코드 확인하기
     */
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
                    @ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
                    @ApiResponse(responseCode = "409", description = "이미 가입된 이메일입니다."),
            }
    )
    ResponseEntity<ResponseDTO> verificationEmailCode(@Valid @RequestBody EmailDTO emailDTO);

    @Hidden
    ResponseEntity test();

}