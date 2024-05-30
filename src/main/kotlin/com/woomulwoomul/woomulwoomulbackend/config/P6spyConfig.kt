package com.woomulwoomul.woomulwoomulbackend.config

import com.p6spy.engine.spy.P6SpyOptions
import com.woomulwoomul.woomulwoomulbackend.common.log.P6spyLogging
import jakarta.annotation.PostConstruct
import org.springframework.context.annotation.Configuration

@Configuration
class P6spyConfig {

    @PostConstruct
    fun logFormat() {
        P6SpyOptions.getActiveInstance().logMessageFormat = P6spyLogging::class.java.name
    }
}