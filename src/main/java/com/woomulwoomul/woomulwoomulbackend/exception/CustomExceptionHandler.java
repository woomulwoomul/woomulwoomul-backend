package com.woomulwoomul.woomulwoomulbackend.exception;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.woomulwoomul.woomulwoomulbackend.common.response.ExceptionResponse;
import io.sentry.Sentry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.nio.file.AccessDeniedException;

import static com.woomulwoomul.woomulwoomulbackend.common.response.ExceptionCode.*;

@Slf4j
@RestControllerAdvice
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(CustomException.class)
    protected ResponseEntity<ExceptionResponse> handleCustomException(CustomException ex) {
        return ExceptionResponse.ofResponseEntity(ex.getExceptionCode());
    }

    /**
     * 400 Bad request
     */

    /**
     * 401 Unauthorized
     */
    @ExceptionHandler(JWTVerificationException.class)
    protected ResponseEntity<ExceptionResponse> handleJwtVerificationException(JWTVerificationException ex) {
        return ExceptionResponse.ofResponseEntity(TOKEN_UNAUTHORIZED);
    }

    @ExceptionHandler(AccessDeniedException.class)
    protected ResponseEntity<ExceptionResponse> handleAccessDeniedException(AccessDeniedException ex) {
        return ExceptionResponse.ofResponseEntity(TOKEN_UNAUTHORIZED);
    }

    /**
     * 405 Method Not Allowed
     */
    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex,
                                                                         HttpHeaders headers,
                                                                         HttpStatusCode status,
                                                                         WebRequest request) {
        return ResponseEntity.status(METHOD_DISABLED.getStatus().value())
                .body(new ExceptionResponse(METHOD_DISABLED.name(),
                        METHOD_DISABLED.getMessage()));
    }

    /**
     * 500 Internal server error
     */
    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ExceptionResponse> handleException(Exception ex) {
        Sentry.captureException(ex);
        return ExceptionResponse.ofResponseEntity(SERVER_ERROR);
    }

    @ExceptionHandler(InterruptedException.class)
    protected ResponseEntity<ExceptionResponse> handleInterruptedException(InterruptedException ex) {
        Sentry.captureException(ex);
        return ExceptionResponse.ofResponseEntity(SERVER_ERROR);
    }
}
