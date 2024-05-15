package com.woomulwoomul.woomulwoomulbackend.common.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import org.springframework.http.ResponseEntity;

@Builder
@Schema(title = "* 예외 응답")
public record ExceptionResponse(
        @Schema(description = "코드", example = "RESPONSE_CODE")
        String code,
        @Schema(description = "메세지", example = "응답 메세지입니다.")
        String message
) {

    public static ResponseEntity<ExceptionResponse> ofResponseEntity(ExceptionCode exceptionCode) {
        return ResponseEntity.status(exceptionCode.getStatus().value())
                .body(ExceptionResponse.builder()
                        .code(exceptionCode.name())
                        .message(exceptionCode.getMessage())
                        .build());
    }
}
