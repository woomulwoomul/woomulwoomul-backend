package com.woomulwoomul.woomulwoomulbackend.common.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import org.springframework.http.ResponseEntity;

@Builder
@Schema(title = "* 기본 단일 데이터 응답")
public record DefaultSingleResponse(
        @Schema(description = "코드", example = "RESPONSE_CODE")
        String code,
        @Schema(description = "메세지", example = "응답 메세지입니다.")
        String message,
        @Schema(description = "데이터")
        Object data
) {

    public static ResponseEntity<DefaultSingleResponse> ofResponseEntity(SuccessCode successCode, Object data) {
        return ResponseEntity.status(successCode.getStatus().value())
                .body(DefaultSingleResponse.builder()
                        .code(successCode.name())
                        .message(successCode.getMessage())
                        .data(data)
                        .build());
    }
}
