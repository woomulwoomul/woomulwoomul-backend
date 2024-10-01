package com.woomulwoomul.adminapi.controller.develop

import com.woomulwoomul.adminapi.service.develop.DevelopService
import com.woomulwoomul.core.common.constant.SuccessCode.SERVER_OK
import com.woomulwoomul.core.common.response.DefaultResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

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
}