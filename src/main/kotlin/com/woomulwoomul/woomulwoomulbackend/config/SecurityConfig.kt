package com.woomulwoomul.woomulwoomulbackend.config

import com.woomulwoomul.woomulwoomulbackend.api.service.user.DefaultOAuth2UserService
import com.woomulwoomul.woomulwoomulbackend.config.auth.CustomAuthenticationEntryPoint
import com.woomulwoomul.woomulwoomulbackend.config.auth.CustomAuthenticationFilter
import com.woomulwoomul.woomulwoomulbackend.config.auth.OAuth2AuthenticationFailureHandler
import com.woomulwoomul.woomulwoomulbackend.config.auth.OAuth2AuthenticationSuccessHandler
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
    private val customAuthenticationFilter: CustomAuthenticationFilter,
    private val customAuthenticationEntryPoint: CustomAuthenticationEntryPoint,
    private val defaultOAuth2UserService: DefaultOAuth2UserService,
    private val oAuth2AuthenticationSuccessHandler: OAuth2AuthenticationSuccessHandler,
    private val oAuth2AuthenticationFailureHandler: OAuth2AuthenticationFailureHandler,
) {

    @Bean
    fun passwordEncoder() = BCryptPasswordEncoder()

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        return http
            .csrf{ it.disable() }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .oauth2Login{ it ->
                it.userInfoEndpoint{ it.userService(defaultOAuth2UserService) }
                    .defaultSuccessUrl("/api/user")
                    .successHandler(oAuth2AuthenticationSuccessHandler)
                    .failureHandler(oAuth2AuthenticationFailureHandler)
            }.authorizeHttpRequests {
                it.requestMatchers("/api/login").permitAll()
                    .anyRequest().permitAll()
            }.addFilterBefore(customAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java)
            .exceptionHandling{ it.authenticationEntryPoint(customAuthenticationEntryPoint) }
            .build()
    }
}