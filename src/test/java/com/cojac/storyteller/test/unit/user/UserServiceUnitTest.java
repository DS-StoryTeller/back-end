package com.cojac.storyteller.test.unit.user;

import com.cojac.storyteller.common.mail.MailService;
import com.cojac.storyteller.common.redis.RedisService;
import com.cojac.storyteller.user.dto.*;
import com.cojac.storyteller.user.entity.LocalUserEntity;
import com.cojac.storyteller.user.entity.SocialUserEntity;
import com.cojac.storyteller.user.exception.DuplicateEmailException;
import com.cojac.storyteller.user.exception.DuplicateUsernameException;
import com.cojac.storyteller.user.exception.RequestParsingException;
import com.cojac.storyteller.user.jwt.JWTUtil;
import com.cojac.storyteller.user.repository.LocalUserRepository;
import com.cojac.storyteller.user.repository.SocialUserRepository;
import com.cojac.storyteller.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static com.cojac.storyteller.user.jwt.CustomLogoutFilter.REFRESH_TOKEN_PREFIX;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 단위 테스트 클래스
 *
 * 이 클래스는 개별 단위(주로 서비스 또는 비즈니스 로직) 기능을 검증하기 위한 단위 테스트를 포함합니다.
 *
 * 주요 특징:
 * - 모의 객체(mock objects)를 사용하여 외부 의존성을 제거하고,테스트 대상 객체의 로직에만 집중합니다.
 * - 테스트의 독립성을 보장하여, 각 테스트가 서로에게 영향을 미치지 않도록 합니다.
 *
 * 테스트 전략:
 * - 간단한 기능이나 로직에 대한 테스트는 단위 테스트를 사용하십시오.
 * - 시스템의 전체적인 동작 및 상호작용을 검증하기 위해 통합 테스트를 활용하십시오.
 *
 * 참고: 단위 테스트는 실행 속도가 빠르며,
 *       전체 시스템의 동작보다는 개별 단위의 동작을 검증하는 데 중점을 둡니다.
 */
public class UserServiceUnitTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private LocalUserRepository localUserRepository;
    @Mock
    private SocialUserRepository socialUserRepository;
    @Mock
    private JWTUtil jwtUtil;
    @Mock
    private RedisService redisService;
    @Mock
    private MailService mailService;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * 카카오 소셜 로그인
     */
    @Test
    void kakaoLogin_ShouldReturnSocialUserDTO_WhenValidKakaoLoginRequest() throws Exception {
        // Arrange
        KakaoLoginRequestDTO kakaoLoginRequestDTO = new KakaoLoginRequestDTO("12345", "nickname", "email@example.com", "USER");
        HttpServletResponse response = mock(HttpServletResponse.class);

        SocialUserEntity mockUser = new SocialUserEntity("kakao_12345", "nickname", "email@example.com", "USER");

        when(socialUserRepository.findByAccountId("kakao_12345")).thenReturn(Optional.empty());
        when(socialUserRepository.save(any(SocialUserEntity.class))).thenReturn(mockUser);
        when(jwtUtil.createJwt(any(), any(), any(), any(), any())).thenReturn("accessToken", "refreshToken");

        // Act
        SocialUserDTO result = userService.kakaoLogin(kakaoLoginRequestDTO, response);

        // Assert
        assertNotNull(result);
        assertEquals(mockUser.getAccountId(), result.getAccountId());
        verify(response).setHeader("access", "accessToken");
        verify(response).setHeader("refresh", "refreshToken");
    }

    /**
     * 회원 등록하기
     */
    @Test
    void registerUser_ShouldReturnLocalUserDTO_WhenValidCreateUserRequest() {
        CreateUserRequestDTO createUserRequestDTO = new CreateUserRequestDTO();
        createUserRequestDTO.setUsername("testUser");
        createUserRequestDTO.setPassword("password123");
        createUserRequestDTO.setEmail("test@example.com");
        createUserRequestDTO.setRole("USER");

        when(localUserRepository.existsByUsername("testUser")).thenReturn(false);
        when(bCryptPasswordEncoder.encode("password123")).thenReturn("hashedPassword");
        when(localUserRepository.save(any(LocalUserEntity.class))).thenReturn(new LocalUserEntity());

        LocalUserDTO result = userService.registerUser(createUserRequestDTO);

        assertNotNull(result);
        assertEquals("testUser", result.getUsername());
        verify(localUserRepository).save(any(LocalUserEntity.class));
    }

    @Test
    void registerUser_ShouldThrowDuplicateUsernameException_WhenUsernameExists() {
        // given
        CreateUserRequestDTO createUserRequestDTO = new CreateUserRequestDTO();
        createUserRequestDTO.setUsername("existingUser");
        createUserRequestDTO.setPassword("password123");
        createUserRequestDTO.setEmail("email@example.com");
        createUserRequestDTO.setRole("USER");

        when(localUserRepository.existsByUsername(createUserRequestDTO.getUsername())).thenReturn(true);

        // when & then
        assertThrows(DuplicateUsernameException.class, () -> {
            userService.registerUser(createUserRequestDTO);
        });

        verify(localUserRepository).existsByUsername(createUserRequestDTO.getUsername());
    }


    /**
     * 유저 아이디 중복 검증
     */
    @Test
    void verifiedUsername_ShouldReturnAuthResultTrue_WhenUsernameIsAvailable() {
        // given
        String username = "testUser";
        UsernameDTO usernameDTO = UsernameDTO.builder().username(username).build();
        when(localUserRepository.findByUsername(username)).thenReturn(Optional.empty());

        // when
        UsernameDTO result = userService.verifiedUsername(usernameDTO);

        // then
        assertTrue(result.isAuthResult());
    }

    @Test
    void verifiedUsername_ShouldReturnAuthResultFalse_WhenUsernameIsTaken() {
        // given
        String username = "existingUser";
        UsernameDTO usernameDTO = UsernameDTO.builder().username(username).build();
        when(localUserRepository.findByUsername(username)).thenReturn(Optional.of(new LocalUserEntity()));

        // when
        UsernameDTO result = userService.verifiedUsername(usernameDTO);

        // then
        assertFalse(result.isAuthResult());
    }

    /**
     * 토큰 재발급
     */
    @Test
    void reissueToken_ShouldReturnUserDTO_WhenRefreshTokenIsValidAndLocalUser() throws Exception {
        // given
        String refreshToken = "validRefreshToken";
        String username = "username";
        ReissueDTO reissueDTO = new ReissueDTO(username, "someAccountId");
        LocalUserEntity mockUser = LocalUserEntity.builder()
                .username(username)
                .build();

        when(request.getHeader("refresh")).thenReturn(refreshToken);
        when(jwtUtil.getAuthenticationMethod(refreshToken)).thenReturn("local");
        when(jwtUtil.getCategory(refreshToken)).thenReturn("refresh");
        when(localUserRepository.findByUsername(username)).thenReturn(Optional.of(mockUser));
        String refreshTokenKey = REFRESH_TOKEN_PREFIX + username;
        when(redisService.checkExistsValue(refreshTokenKey)).thenReturn(true);
        when(jwtUtil.getUserKey(refreshToken)).thenReturn(username);
        when(jwtUtil.createJwt(any(), any(), any(), any(), any()))
                .thenReturn("newAccessToken", "newRefreshToken");

        // when
        UserDTO result = userService.reissueToken(request, response, reissueDTO);

        // then
        assertNotNull(result);
        verify(response).setHeader("access", "newAccessToken");
        verify(response).setHeader("refresh", "newRefreshToken");
    }

    @Test
    void reissueToken_ShouldReturnUserDTO_WhenRefreshTokenIsValidAndSocialUser() throws Exception {
        // given
        String refreshToken = "validRefreshToken";
        String accountId = "accountId";
        ReissueDTO reissueDTO = new ReissueDTO("username", accountId);
        SocialUserEntity mockUser = SocialUserEntity.builder()
                .accountId(accountId)
                .build();

        when(request.getHeader("refresh")).thenReturn(refreshToken);
        when(jwtUtil.getAuthenticationMethod(refreshToken)).thenReturn("social");
        when(jwtUtil.getCategory(refreshToken)).thenReturn("refresh");
        when(socialUserRepository.findByAccountId(accountId)).thenReturn(Optional.of(mockUser));
        String refreshTokenKey = REFRESH_TOKEN_PREFIX + accountId;
        when(redisService.checkExistsValue(refreshTokenKey)).thenReturn(true);
        when(jwtUtil.getUserKey(refreshToken)).thenReturn(accountId);
        when(jwtUtil.createJwt(any(), any(), any(), any(), any()))
                .thenReturn("newAccessToken", "newRefreshToken");

        // when
        UserDTO result = userService.reissueToken(request, response, reissueDTO);

        // then
        assertNotNull(result);
        verify(response).setHeader("access", "newAccessToken");
        verify(response).setHeader("refresh", "newRefreshToken");
    }

    @Test
    void reissueToken_ShouldThrowException_WhenRefreshTokenIsInvalid() {
        // given
        String refreshToken = null;
        when(request.getHeader("refresh")).thenReturn(refreshToken);

        // when
        Executable executable = () -> userService.reissueToken(request, response, new ReissueDTO());

        // then
        assertThrows(RequestParsingException.class, executable);
    }

    /**
     * 인증 코드 검증을 위한 이메일 전송
     */
    @Test
    void sendCodeToEmail_ShouldSendEmail_WhenEmailIsNotDuplicated() {
        // given
        String email = "test@example.com";
        when(localUserRepository.existsByEmail(email)).thenReturn(false);
        when(socialUserRepository.existsByEmail(email)).thenReturn(false);

        // when
        userService.sendCodeToEmail(email);

        // then
        verify(mailService).sendEmail(any(), any(), any());
    }

    @Test
    void sendCodeToEmail_ShouldThrowException_WhenEmailIsDuplicated() {
        // given
        String email = "duplicate@example.com";
        when(localUserRepository.existsByEmail(email)).thenReturn(true);
        when(socialUserRepository.existsByEmail(email)).thenReturn(false);

        // when
        Executable executable = () -> userService.sendCodeToEmail(email);

        // then
        assertThrows(DuplicateEmailException.class, executable);
    }

    /**
     * 인증 코드 검증하기
     */
    @Test
    void verifiedCode_ShouldReturnAuthResultTrue_WhenCodeIsValid() {
        // given
        String email = "test@example.com";
        String authCode = "123456";
        when(redisService.getValues("email_code:" + email)).thenReturn(authCode);
        when(redisService.checkExistsValue(authCode)).thenReturn(true);

        // when
        EmailDTO result = userService.verifiedCode(email, authCode);

        // then
        assertTrue(result.isAuthResult());
    }

    @Test
    void verifiedCode_ShouldReturnAuthResultFalse_WhenCodeIsInvalid() {
        // given
        String email = "test@example.com";
        String authCode = "123456";
        when(redisService.getValues("email_code:" + email)).thenReturn("wrongCode");
        when(redisService.checkExistsValue(any())).thenReturn(true);

        // when
        EmailDTO result = userService.verifiedCode(email, authCode);

        // then
        assertFalse(result.isAuthResult());
    }
}
