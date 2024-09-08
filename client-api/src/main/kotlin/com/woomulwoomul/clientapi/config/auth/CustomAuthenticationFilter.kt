package com.woomulwoomul.clientapi.config.auth

import com.woomulwoomul.core.common.constant.CustomHttpHeaders
import com.woomulwoomul.core.config.auth.JwtProvider
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class CustomAuthenticationFilter(
    private val jwtProvider: JwtProvider,
) : OncePerRequestFilter() {

    override fun doFilterInternal(request: HttpServletRequest,
                                  response: HttpServletResponse,
                                  filterChain: FilterChain
    ) {
        val accessToken = request.getHeader(HttpHeaders.AUTHORIZATION)
        val refreshToken = request.getHeader(CustomHttpHeaders.REFRESH_TOKEN)
        val requestUri = request.requestURI

        if (accessToken != null && !requestUri.contains("token")) {
            jwtProvider.verifyToken(accessToken)
        } else if (refreshToken != null && requestUri.contains("token")) {
            jwtProvider.verifyToken(refreshToken)
        }

        filterChain.doFilter(request, response)
    }
}