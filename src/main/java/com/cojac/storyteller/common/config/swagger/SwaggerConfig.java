package com.cojac.storyteller.common.config.swagger;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Value("${server.serverAddress}")
    private String serverAddress;
    @Value("${server.port}")
    private String serverPort;

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
        new SecurityRequirement().addList(refreshTokenKey);
        SecurityScheme refreshSecurityScheme = new SecurityScheme()
                .name(refreshTokenKey)
                .type(SecurityScheme.Type.APIKEY)
                .in(SecurityScheme.In.HEADER)
                .scheme("bearer")
                .bearerFormat("JWT");

        Components components = new Components()
                .addSecuritySchemes(accessTokenKey, accessSecurityScheme)
                .addSecuritySchemes(refreshTokenKey, refreshSecurityScheme);

        Server lcoalServer = new Server()
                .url("http://localhost:8080")
                .description("Localhost 서버");

        // 배포된 서버의 IP 또는 도메인 주소 설정
        Server deployedServer = new Server()
                .url("http://" + serverAddress + ":" + serverPort)
                .description("배포된 서버");

        return new OpenAPI()
                .components(components)
                .info(apiInfo())
                .addSecurityItem(accessSecurityRequirement)
                .servers(List.of(lcoalServer, deployedServer));
    }

    @Bean
    public GroupedOpenApi apiGroup() {
        return GroupedOpenApi.builder()
                .group("StoryTeller API") // 모든 API를 포함할 그룹
                .pathsToMatch("/**") // 모든 경로를 포함
                .addOpenApiCustomizer(new LoginSwaggerConfig().customSpringSecurityLoginEndpointCustomizer())
                .addOpenApiCustomizer(new LogoutSwaggerConfig().customSpringSecurityLogoutEndpointCustomizer())
                .build();
    }

    private Info apiInfo() {
        return new Info()
                .title("StoryTeller API Test") // API의 제목
                .description("StoryTeller API 기능 테스트") // API에 대한 설명
                .version("1.0.0"); // API의 버전
    }
}
