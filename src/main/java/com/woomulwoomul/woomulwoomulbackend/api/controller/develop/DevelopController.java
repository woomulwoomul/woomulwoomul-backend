package com.woomulwoomul.woomulwoomulbackend.api.controller.develop;

import com.woomulwoomul.woomulwoomulbackend.api.service.develop.DevelopService;
import com.woomulwoomul.woomulwoomulbackend.common.response.DefaultResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.woomulwoomul.woomulwoomulbackend.common.response.SuccessCode.SERVER_OK;

@Tag(name = "개발 API")
@Validated
@RestController
@RequiredArgsConstructor
public class DevelopController {

    private final DevelopService developService;

    @Operation(summary = "헬스 체크",
            description = "<응답 코드>\n" +
                    "- 200 = SERVER_OK\n" +
                    "- 500 = SERVER_ERROR\n" +
                    "- 503 = ONGOING_INSPECTION",
            responses = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = Object.class)))
    })
    @GetMapping("/api/health")
    public ResponseEntity<DefaultResponse> healthCheck() {
        String serverName = developService.getServerName();

        return ResponseEntity.status(SERVER_OK.getStatus())
                .body(DefaultResponse.builder()
                        .code(SERVER_OK.name())
                        .message(serverName.concat(" ").concat(SERVER_OK.getMessage()))
                        .build());
    }
}
