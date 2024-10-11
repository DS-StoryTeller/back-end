package com.cojac.storyteller.user.service.security;

import com.cojac.storyteller.user.entity.LocalUserEntity;
import com.cojac.storyteller.user.repository.LocalUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 통합 테스트 클래스
 *
 * 이 클래스는 여러 구성 요소(서비스, 데이터베이스 등) 간의 상호작용을 검증하기 위한 통합 테스트를 포함합니다.
 *
 * 주요 특징:
 * - 실제 데이터베이스와 리포지토리를 사용하여 테스트를 수행합니다.
 * - 외부 서비스와의 의존성을 최소화하기 위해 모의 객체를 활용합니다.
 *
 * 통합 테스트는 여러 구성 요소 간의 상호작용을 검증하므로, 일반적으로 단위 테스트보다 느릴 수 있습니다.
 *
 * 테스트 전략:
 * - 간단한 기능이나 로직에 대한 테스트는 단위 테스트를 사용하십시오.
 * - 시스템의 전체적인 동작 및 상호작용을 검증하기 위해 통합 테스트를 활용하십시오.
 *
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class LoginFilterTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private LocalUserRepository localUserRepository;

    @BeforeEach
    public void setUp() {
        LocalUserEntity localUserEntity = LocalUserEntity.builder()
                .username("username")
                .encryptedPassword(new BCryptPasswordEncoder().encode("password"))
                .email("email.com")
                .role("ROLE_USER")
                .build();

        localUserRepository.save(localUserEntity);
    }

    @Test
    @DisplayName("성공적인 인증 테스트")
    public void testSuccessfulAuthentication() throws Exception {
        mockMvc.perform(post("/login")
                        .param("username", "username")
                        .param("password", "password"))
                .andExpect(status().isOk())
                .andExpect(header().exists("access"))
                .andExpect(header().exists("refresh"));
    }

    @Test
    @DisplayName("잘못된 사용자 이름으로 인증 실패 테스트")
    public void testFailedAuthentication_withWrongUsername() throws Exception {
        mockMvc.perform(post("/login")
                        .param("username", "wrongUsername") // 잘못된 사용자 이름
                        .param("password", "password"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("잘못된 비밀번호로 인증 실패 테스트")
    public void testFailedAuthentication_withWrongPassword() throws Exception {
        mockMvc.perform(post("/login")
                        .param("username", "username")
                        .param("password", "wrongPassword")) // 잘못된 비밀번호
                .andExpect(status().isUnauthorized());
    }


}


