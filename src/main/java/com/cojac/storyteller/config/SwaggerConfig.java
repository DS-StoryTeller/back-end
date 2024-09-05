package com.cojac.storyteller.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {

        // Access 토큰 설정
        String accessTokenKey = "access";
        SecurityRequirement accessSecurityRequirement = new SecurityRequirement().addList(accessTokenKey);
        SecurityScheme accessSecurityScheme = new SecurityScheme()
                .name(accessTokenKey)
                .type(SecurityScheme.Type.APIKEY)
                .in(SecurityScheme.In.HEADER)
                .scheme("bearer")
                .bearerFormat("JWT");

        // Refresh 토큰 설정
        String refreshTokenKey = "refresh";
        SecurityRequirement refreshSecurityRequirement = new SecurityRequirement().addList(refreshTokenKey);
        SecurityScheme refreshSecurityScheme = new SecurityScheme()
                .name(refreshTokenKey)
                .type(SecurityScheme.Type.APIKEY)
                .in(SecurityScheme.In.HEADER)
                .scheme("bearer")
                .bearerFormat("JWT");

        Components components = new Components()
                .addSecuritySchemes(accessTokenKey, accessSecurityScheme)
                .addSecuritySchemes(refreshTokenKey, refreshSecurityScheme);

        return new OpenAPI()
                .components(components)
                .info(apiInfo())
                .addSecurityItem(accessSecurityRequirement)
                .addSecurityItem(refreshSecurityRequirement); // 두 가지 보안 설정 추가
    }

    private Info apiInfo() {
        return new Info()
                .title("StoryTeller API Test") // API의 제목
                .description("StoryTeller API 기능 테스트") // API에 대한 설명
                .version("1.0.0"); // API의 버전
    }
}
