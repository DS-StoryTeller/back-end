package com.cojac.storyteller.common.config.swagger;

import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Configuration
public class LoginSwaggerConfig {

    @Bean
    public OpenApiCustomizer customSpringSecurityLoginEndpointCustomizer() {
        return openAPI -> {
            // 로그인 엔드포인트 정의
            ApiResponses responses = new ApiResponses()
                    .addApiResponse("200", new ApiResponse().description("로그인을 성공했습니다."));

            // 쿼리 파라미터 정의
            List<Parameter> parameters = Arrays.asList(
                    new io.swagger.v3.oas.models.parameters.Parameter()
                            .name("username")
                            .in("query") // 쿼리 파라미터로 설정
                            .required(true)
                            .schema(new Schema<String>().type("string")),
                    new io.swagger.v3.oas.models.parameters.Parameter()
                            .name("password")
                            .in("query") // 쿼리 파라미터로 설정
                            .required(true)
                            .schema(new Schema<String>().type("string"))
            );

            PathItem pathItem = new PathItem()
                    .post(new io.swagger.v3.oas.models.Operation()
                            .summary("자체 로그인")
                            .description("자체 로그인")
                            .addParametersItem(parameters.get(0))
                            .addParametersItem(parameters.get(1))
                            .responses(responses)
                            .tags(Collections.singletonList("User Controller")) // 태그 추가
                    );

            openAPI.path("/login", pathItem);
        };
    }
}
