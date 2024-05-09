package com.woomulwoomul.woomulwoomulbackend.config;

import com.woomulwoomul.woomulwoomulbackend.common.log.InterceptorLogging;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new InterceptorLogging());
    }
}
