package com.woomulwoomul.adminapi.service.develop

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class DevelopService(
    @Value("\${api.name}") private val serverName: String,
) {

    /**
     * 서버명 조회
     * @return 서버명
     */
    fun getServerName(): String {
        return serverName
    }
}