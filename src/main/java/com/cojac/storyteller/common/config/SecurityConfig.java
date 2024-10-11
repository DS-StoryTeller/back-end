package com.cojac.storyteller.common.config;

import com.cojac.storyteller.user.repository.LocalUserRepository;
import com.cojac.storyteller.user.repository.SocialUserRepository;
import com.cojac.storyteller.common.redis.RedisService;
import com.cojac.storyteller.user.jwt.*;
import com.cojac.storyteller.user.controller.security.CustomAuthenticationEntryPoint;
import com.cojac.storyteller.user.jwt.JWTFilter;
import com.cojac.storyteller.user.service.security.LoginFilter;
import com.cojac.storyteller.user.service.security.LogoutFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Collections;
import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    @Value("${server.serverAddress}")
    private String serverAddress;
    @Value("${server.port}")
    private String serverPort;

    private final AuthenticationConfiguration authenticationConfiguration;
    private final JWTUtil jwtUtil;
    private final RedisService redisService;
    private final ObjectMapper objectMapper;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    private final LocalUserRepository localUserRepository;
    private final SocialUserRepository socialUserRepository;

    @Value("${management.endpoints.web.base-path}")
    private String endpoints;

    // AuthenticationManager Bean 등록
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        // CORS 설정
        http
                .cors((corsCustomizer -> corsCustomizer.configurationSource(new CorsConfigurationSource() {

                    @Override
                    public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {

                        CorsConfiguration configuration = new CorsConfiguration();

                        configuration.setAllowedOrigins(List.of("http://localhost:3000", "http://" + serverAddress + ":" + serverPort));
                        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                        configuration.setAllowCredentials(true);
                        configuration.setAllowedHeaders(Collections.singletonList("*"));
                        configuration.setMaxAge(3600L);

                        configuration.setExposedHeaders(Collections.singletonList("Set-Cookie"));
                        configuration.setExposedHeaders(Collections.singletonList("Authorization"));

                        return configuration;
                    }
                })));

        // csrf disable
        http
                .csrf((auth) -> auth.disable());

        // Form 로그인 방식 disable
        http
                .formLogin((auth) -> auth.disable());

        // http basic 인증 방식 disable
        http
                .httpBasic((auth) -> auth.disable());

        // 경로별 인가 작업
        http
                .authorizeHttpRequests((auth) -> auth
                        .requestMatchers("/login", "/", "/register").permitAll()
                        .requestMatchers("/username/verifications", "/emails/verification-requests", "/emails/verifications").permitAll()
                        .requestMatchers("/kakao-login", "/google-login").permitAll()
                        .requestMatchers("/reissue").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/storyteller-api/**").permitAll()
                        .requestMatchers(endpoints+"/**").permitAll()
                        .requestMatchers("/healthCheck", "/env").permitAll()
                        .anyRequest().authenticated());

        // 인증/인가와 관련된 예외 처리
        http
                .exceptionHandling((exceptionHandling) -> exceptionHandling
                        .authenticationEntryPoint(customAuthenticationEntryPoint)
                );

        // 로그아웃 필터 등록
        http
                .addFilterBefore(new LogoutFilter(jwtUtil, redisService, objectMapper, localUserRepository, socialUserRepository), org.springframework.security.web.authentication.logout.LogoutFilter.class);

        // JWTFilter 등록
        http
                .addFilterBefore(new JWTFilter(jwtUtil), LoginFilter.class);

        // 커스텀 UsernamePasswordAuthenticationFilter 추가
        http
                .addFilterAt(new LoginFilter(authenticationManager(authenticationConfiguration), jwtUtil, redisService), UsernamePasswordAuthenticationFilter.class);


        // 세션 설정
        http
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));


        return http.build();

    }

}