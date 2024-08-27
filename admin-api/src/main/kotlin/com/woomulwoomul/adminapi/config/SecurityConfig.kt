package com.woomulwoomul.adminapi.config

import com.woomulwoomul.adminapi.config.auth.CustomAuthenticationEntryPoint
import com.woomulwoomul.adminapi.config.auth.OAuth2AuthenticationFailureHandler
import com.woomulwoomul.adminapi.config.auth.OAuth2AuthenticationSuccessHandler
import com.woomulwoomul.core.config.auth.CustomAuthenticationFilter
import com.woomulwoomul.core.config.auth.CustomOAuth2UserService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
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
    private val customOAuth2UserService: CustomOAuth2UserService,
    private val oAuth2AuthenticationSuccessHandler: OAuth2AuthenticationSuccessHandler,
    private val oAuth2AuthenticationFailureHandler: OAuth2AuthenticationFailureHandler,
) {

    private val WHITE_LIST = hashMapOf(
        HttpMethod.GET to arrayOf("/api/health", "/")
    )

    @Bean
    fun passwordEncoder() = BCryptPasswordEncoder()

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        return http
            .csrf{ it.disable() }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .oauth2Login{ it ->
                it.userInfoEndpoint{ it.userService(customOAuth2UserService) }
                    .successHandler(oAuth2AuthenticationSuccessHandler)
                    .failureHandler(oAuth2AuthenticationFailureHandler)
            }.authorizeHttpRequests {
                WHITE_LIST.forEach { (method, paths) ->
                    paths.forEach { path ->
                        it.requestMatchers(method, path).permitAll()
                    }
                }
                it.anyRequest().permitAll()
            }.addFilterBefore(customAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java)
            .exceptionHandling{ it.authenticationEntryPoint(customAuthenticationEntryPoint) }
            .build()
    }
}