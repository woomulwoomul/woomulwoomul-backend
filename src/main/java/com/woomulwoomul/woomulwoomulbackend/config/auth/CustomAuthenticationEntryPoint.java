package com.woomulwoomul.woomulwoomulbackend.config.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.woomulwoomul.woomulwoomulbackend.common.response.ExceptionCode;
import com.woomulwoomul.woomulwoomulbackend.common.response.ExceptionResponse;
import com.woomulwoomul.woomulwoomulbackend.exception.CustomException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.OutputStream;

import static com.woomulwoomul.woomulwoomulbackend.common.response.ExceptionCode.SERVER_ERROR;
import static com.woomulwoomul.woomulwoomulbackend.common.response.ExceptionCode.TOKEN_UNAUTHENTICATED;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
        response.setStatus(UNAUTHORIZED.value());
        response.setContentType(APPLICATION_JSON_VALUE);

        try {
            String responseBody = objectMapper.writeValueAsString(ExceptionResponse.ofResponseEntity(TOKEN_UNAUTHENTICATED));

            OutputStream outputStream = response.getOutputStream();
            outputStream.write(responseBody.getBytes());
            outputStream.flush();
        } catch (IOException e) {
            throw new CustomException(SERVER_ERROR, e.getCause());
        }
    }
}
