package com.woomulwoomul.woomulwoomulbackend.common.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import org.springframework.http.ResponseEntity;

@Builder
@Schema(title = "* 기본 응답")
public record DefaultResponse(
        @Schema(description = "코드", example = "RESPONSE_CODE")
        String code,
        @Schema(description = "메세지", example = "응답 메세지입니다.")
        String message
) {

    public static ResponseEntity<DefaultResponse> ofResponseEntity(SuccessCode successCode) {
        return ResponseEntity.status(successCode.getStatus().value())
                .body(DefaultResponse.builder()
                        .code(successCode.name())
                        .message(successCode.getMessage())
                        .build());
    }
}
