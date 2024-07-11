package com.woomulwoomul.woomulwoomulbackend.api.controller.develop

import com.woomulwoomul.woomulwoomulbackend.api.service.develop.DevelopService
import com.woomulwoomul.woomulwoomulbackend.common.constant.SuccessCode.DB_RESET
import com.woomulwoomul.woomulwoomulbackend.common.constant.SuccessCode.SERVER_OK
import com.woomulwoomul.woomulwoomulbackend.common.response.DefaultResponse
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

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

    @PostMapping("/api/reset")
    fun reset(): ResponseEntity<DefaultResponse> {
        developService.resetAndInject()

        return ResponseEntity.status(DB_RESET.httpStatus)
            .body(DefaultResponse(DB_RESET.name, DB_RESET.message))
    }
}