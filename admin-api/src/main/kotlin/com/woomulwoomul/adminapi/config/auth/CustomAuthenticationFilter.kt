package com.woomulwoomul.adminapi.config.auth

import arrow.core.getOrElse
import com.woomulwoomul.core.common.constant.CustomCookies
import com.woomulwoomul.core.common.constant.ExceptionCode
import com.woomulwoomul.core.common.response.CustomException
import com.woomulwoomul.core.config.auth.JwtProvider
import jakarta.servlet.FilterChain
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
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
        val cookies = request.cookies ?: arrayOf()

        val accessToken = getCookieValue(cookies, CustomCookies.ACCESS_TOKEN)
        val refreshToken = getCookieValue(cookies, CustomCookies.REFRESH_TOKEN)

        if (accessToken != null && !jwtProvider.isValidToken(accessToken)) {
            if (refreshToken != null && jwtProvider.isValidToken(refreshToken)) {
                val userId = jwtProvider.decodeToken(refreshToken).subject()
                    .getOrElse { throw CustomException(ExceptionCode.TOKEN_UNAUTHORIZED) }
                    .toLong()

                jwtProvider.createTokenCookies(userId)
                    .forEach { response.addCookie(it) }
            }
        }

        filterChain.doFilter(request, response)
    }

    private fun getCookieValue(cookies: Array<Cookie>, key: String): String? {
        return cookies.firstOrNull { it.name == key }?.value
    }
}