package com.woomulwoomul.woomulwoomulbackend.config

import com.woomulwoomul.woomulwoomulbackend.common.log.InterceptorLogging
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebMvcConfig : WebMvcConfigurer {

    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(InterceptorLogging())
        super.addInterceptors(registry)
    }
}