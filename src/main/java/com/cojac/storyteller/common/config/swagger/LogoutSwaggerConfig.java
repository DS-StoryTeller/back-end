package com.cojac.storyteller.common.config.swagger;

import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.examples.Example;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Configuration
public class LogoutSwaggerConfig {

    @Bean
    public OpenApiCustomizer customSpringSecurityLogoutEndpointCustomizer() {
        return openAPI -> {
            // 로그아웃 엔드포인트 정의
            ApiResponses responses = new ApiResponses()
                    .addApiResponse("200", new ApiResponse().description("로그아웃을 성공했습니다."));

            // 헤더 파라미터 정의
            List<Parameter> headers = Arrays.asList(
                    new io.swagger.v3.oas.models.parameters.Parameter()
                            .name("refresh")
                            .in("header") // 헤더로 설정
                            .required(true)
                            .schema(new Schema<String>().type("string"))
            );

            // 요청 본문 JSON 스키마 정의
            Schema<?> loginSchema = new Schema<>()
                    .addProperty("username", new Schema<String>().type("string"))
                    .addProperty("accountId", new Schema<String>().type("string"));

            // 예시 값 설정
            Example localExample = new Example()
                    .value("{\"username\": \"사용자 이름\"}");

            Example socialExample = new Example()
                    .value("{\"accountId\": \"google_132468798\"}");

            Content requestBodyContent = new Content()
                    .addMediaType("application/json", new MediaType()
                            .schema(loginSchema)
                            .examples(Map.of(
                                    "소셜 사용자일 경우", socialExample,
                                    "자체 사용자일 경우", localExample

                            ))
                    );

            RequestBody requestBody = new RequestBody()
                    .content(requestBodyContent)
                    .required(true);

            // 로그아웃 엔드포인트 정의
            PathItem pathItem = new PathItem()
                    .post(new io.swagger.v3.oas.models.Operation()
                            .summary("로그아웃")
                            .description("로그아웃")
                            .addParametersItem(headers.get(0)) // 헤더 추가
                            .requestBody(requestBody) // 요청 본문 추가
                            .responses(responses)
                            .tags(Collections.singletonList("User Controller")) // 태그 추가
                    );

            openAPI.path("/logout", pathItem);
        };
    }
}
