package com.woomulwoomul.woomulwoomulbackend.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import org.springdoc.core.customizers.GlobalOpenApiCustomizer;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.woomulwoomul.woomulwoomulbackend.common.response.ExceptionCode.ONGOING_INSPECTION;
import static com.woomulwoomul.woomulwoomulbackend.common.response.ExceptionCode.SERVER_ERROR;

@Configuration
@SecurityScheme(type = SecuritySchemeType.APIKEY, name = "AUTHORIZATION", in = SecuritySchemeIn.HEADER)
@OpenAPIDefinition(security = {
        @SecurityRequirement(name = "Access-Token"),
        @SecurityRequirement(name = "Refresh-Token")})
public class SwaggerConfig {

    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("v1")
                .pathsToMatch("/v1/**")
                .build();
    }

    @Bean
    public OpenAPI springShopOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("우물우물 Dev API")
                        .version("V1.0.0"));
    }

    @Bean
    public GlobalOpenApiCustomizer customerGlobalHeaderOpenApiCustomiser() {
        return openApi -> {
            openApi.getPaths().values().forEach(pathItem -> pathItem.readOperations()
                    .forEach(operation -> {
                        ApiResponses apiResponses = operation.getResponses();
                        apiResponses.addApiResponse("500", createApiResponse(SERVER_ERROR.name(), null));
                        apiResponses.addApiResponse("503", createApiResponse(ONGOING_INSPECTION.name(), null));
                    }));
        };
    }

    private ApiResponse createApiResponse(String message, Content content){
        return new ApiResponse()
                .description(message)
                .content(content);
    }
}
