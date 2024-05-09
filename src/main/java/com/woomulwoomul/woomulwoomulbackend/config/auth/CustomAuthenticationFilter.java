package com.woomulwoomul.woomulwoomulbackend.config.auth;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static com.woomulwoomul.woomulwoomulbackend.common.constant.CustomHttpHeaders.AUTHORIZATION;
import static com.woomulwoomul.woomulwoomulbackend.common.constant.CustomHttpHeaders.REFRESH_TOKEN;
import static com.woomulwoomul.woomulwoomulbackend.config.auth.JwtType.ACCESS;
import static com.woomulwoomul.woomulwoomulbackend.config.auth.JwtType.REFRESH;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String accessToken = request.getHeader(AUTHORIZATION);
        String refreshToken = request.getHeader(REFRESH_TOKEN);

        if (accessToken != null && !request.getRequestURI().contains("token")) {
            jwtProvider.verifyToken(accessToken, ACCESS);
        } else if (refreshToken != null) {
            jwtProvider.verifyToken(refreshToken, REFRESH);
        }

        filterChain.doFilter(request, response);
    }
}
