package com.woomulwoomul.clientserver.controller.develop

import com.woomulwoomul.clientserver.service.develop.DevelopService
import com.woomulwoomul.core.common.constant.SuccessCode.*
import com.woomulwoomul.core.common.response.DefaultResponse
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime

@Validated
@RestController
class DevelopController(
    private val developService: DevelopService
) {

    @GetMapping("/api/health")
    fun healthCheck(): ResponseEntity<DefaultResponse> {
        val serverName = developService.getServerName()

        return ResponseEntity.status(SERVER_OK.httpStatus)
            .body(DefaultResponse(SERVER_OK.name, serverName + SERVER_OK.message))
    }

    @GetMapping("/api/tester/{testerId}")
    fun getTesterToken(@PathVariable testerId: Long): ResponseEntity<DefaultResponse> {
        val headers = developService.getTesterToken(testerId)

        return DefaultResponse.toResponseEntity(headers, TESTER_TOKEN_GENERATED)
    }

    @PostMapping("/api/reset")
    fun reset(): ResponseEntity<DefaultResponse> {
        developService.resetAndInject(LocalDateTime.now())

        return DefaultResponse.toResponseEntity(DB_RESET)
    }
}