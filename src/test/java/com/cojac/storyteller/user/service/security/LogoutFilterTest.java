package com.cojac.storyteller.user.service.security;

import com.cojac.storyteller.user.entity.LocalUserEntity;
import com.cojac.storyteller.user.repository.LocalUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class LogoutFilterTest {

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
    @DisplayName("성공적인 로그아웃 테스트")
    public void testSuccessfulLogout() throws Exception {
        MockHttpServletResponse loginResponse = mockMvc.perform(post("/login")
                        .param("username", "username")
                        .param("password", "password"))
                .andExpect(status().isOk())
                .andExpect(header().exists("access"))
                .andExpect(header().exists("refresh"))
                .andReturn()
                .getResponse();

        String refreshToken = loginResponse.getHeader("refresh");

        mockMvc.perform(post("/logout")
                        .header("refresh", refreshToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"username\"}"))
                .andExpect(status().isOk());

    }

    @Test
    @DisplayName("로그아웃 시 Access 토큰으로 테스트")
    public void testLogoutWithAccessToken() throws Exception {
        MockHttpServletResponse loginResponse = mockMvc.perform(post("/login")
                        .param("username", "username")
                        .param("password", "password"))
                .andExpect(status().isOk())
                .andExpect(header().exists("access"))
                .andExpect(header().exists("refresh"))
                .andReturn()
                .getResponse();

        String accessToken = loginResponse.getHeader("access");

        mockMvc.perform(post("/logout")
                        .header("refresh", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"username\"}"))
                .andExpect(status().isUnauthorized());

    }

    @Test
    @DisplayName("로그아웃 시 토큰 누락 테스트")
    public void testLogoutWithoutToken() throws Exception {
        mockMvc.perform(post("/logout"))
                .andExpect(status(). isUnauthorized());
    }

}
