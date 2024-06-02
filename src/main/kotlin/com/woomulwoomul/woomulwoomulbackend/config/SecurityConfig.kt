package com.woomulwoomul.woomulwoomulbackend.config

import com.woomulwoomul.woomulwoomulbackend.api.service.user.DefaultOAuth2UserService
import com.woomulwoomul.woomulwoomulbackend.config.auth.CustomAuthenticationEntryPoint
import com.woomulwoomul.woomulwoomulbackend.config.auth.CustomAuthenticationFilter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@EnableWebSecurity
class SecurityConfig(
    val customAuthenticationFilter: CustomAuthenticationFilter,
    val customAuthenticationEntryPoint: CustomAuthenticationEntryPoint,
    val defaultOAuth2UserService: DefaultOAuth2UserService,
) {

    @Bean
    fun passwordEncoder() = BCryptPasswordEncoder()

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        return http
            .csrf{ it.disable() }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .oauth2Login{ it -> it.userInfoEndpoint{ it.userService(defaultOAuth2UserService) }.defaultSuccessUrl("/api/user") }
            .authorizeHttpRequests {
                it.requestMatchers("/api/login").permitAll()
                    .anyRequest().permitAll()
            }.addFilterBefore(customAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java)
            .exceptionHandling{ it.authenticationEntryPoint(customAuthenticationEntryPoint) }
            .build()
    }
}