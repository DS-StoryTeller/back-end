package com.cojac.storyteller.config;

import com.cojac.storyteller.config.swagger.LoginSwaggerConfig;
import com.cojac.storyteller.config.swagger.LogoutSwaggerConfig;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.examples.Example;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

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
                .addSecurityItem(accessSecurityRequirement);
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
