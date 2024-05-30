package com.woomulwoomul.woomulwoomulbackend.common.exception

import com.woomulwoomul.woomulwoomulbackend.common.constant.ExceptionCode.*
import com.woomulwoomul.woomulwoomulbackend.common.response.CustomException
import com.woomulwoomul.woomulwoomulbackend.common.response.ExceptionResponse
import io.sentry.Sentry
import jakarta.validation.ConstraintViolationException
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.http.ResponseEntity
import org.springframework.security.access.AccessDeniedException
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.WebRequest
import org.springframework.web.multipart.MaxUploadSizeExceededException
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@RestControllerAdvice
class CustomExceptionHandler : ResponseEntityExceptionHandler() {

    @ExceptionHandler(CustomException::class)
    protected fun handleCustomException(ex: CustomException): ResponseEntity<ExceptionResponse> {
        return ExceptionResponse.toResponseEntity(ex)
    }

    /**
     * 400 Bad Request
     */
    override fun handleMethodArgumentNotValid(
        ex: MethodArgumentNotValidException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest
    ): ResponseEntity<Any>? {
        var responseCode = ex.fieldError?.field?.uppercase()
        val responseMessage = ex.fieldError?.defaultMessage

        responseCode?.let { responseCode -> formatIfInnerDto(responseCode) }.also { responseCode = it }
        responseCode += responseMessage?.let { formatResponseCode(it) }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(responseMessage?.let {
                responseCode?.let {
                        it1 -> ExceptionResponse(it1, it) }
            })
    }

    @ExceptionHandler(ConstraintViolationException::class)
    protected fun handleConstraintViolationException(ex: ConstraintViolationException):
            ResponseEntity<ExceptionResponse> {
        var responseCode: String
        val responseMessage: String

        if (ex.constraintViolations.stream().findFirst().isPresent) {
            val constraintViolation = ex.constraintViolations.stream()
                .findFirst()
                .get()
            val propertyPathSize = constraintViolation.propertyPath.toString()
                .split("\\.")
                .size

            responseCode = constraintViolation.propertyPath.toString()
                .split("\\.")[propertyPathSize - 1]
            responseCode = responseCode.replace("[^\\p{Alnum}]+", "_")
                .replace("(\\p{Lower})(\\p{Upper})", "$1_$2")
                .uppercase()
            responseCode = formatIfInnerDto(responseCode)
            responseMessage = constraintViolation.messageTemplate
            responseCode += formatResponseCode(responseMessage)
            responseCode = responseCode.split(".")[responseCode.split(".").size - 1]
        } else {
            throw CustomException(SERVER_ERROR)
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ExceptionResponse(responseCode, responseMessage))
    }

    /**
     * 401 Unauthorized
     */
    @ExceptionHandler(AccessDeniedException::class)
    protected fun handleAccessDeniedException(ex: AccessDeniedException): ResponseEntity<ExceptionResponse> {
        return ExceptionResponse.toResponseEntity(TOKEN_UNAUTHENTICATED)
    }

    /**
     * 405 Method Not Allowed
     */
    override fun handleHttpRequestMethodNotSupported(
        ex: HttpRequestMethodNotSupportedException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest
    ): ResponseEntity<Any>? {
        return ResponseEntity.status(METHOD_DISABLED.httpStatus)
            .body(ExceptionResponse(METHOD_DISABLED))
    }

    /**
     * 413 Payload Too Large
     */
    override fun handleMaxUploadSizeExceededException(
        ex: MaxUploadSizeExceededException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest
    ): ResponseEntity<Any>? {
        return ResponseEntity.status(FILE_SIZE_EXCEED.httpStatus)
            .body(ExceptionResponse(FILE_SIZE_EXCEED))
    }

    /**
     * 500 Internal Server Error
     */
    @ExceptionHandler(Exception::class)
    protected fun handleException(ex: Exception): ResponseEntity<ExceptionResponse> {
        Sentry.captureException(ex)
        return ExceptionResponse.toResponseEntity(SERVER_ERROR)
    }

    /**
     * 500 Internal Server Error
     */
    @ExceptionHandler(InterruptedException::class)
    protected fun handleInterruptedException(ex: InterruptedException): ResponseEntity<ExceptionResponse> {
        Sentry.captureException(ex)
        return ExceptionResponse.toResponseEntity(SERVER_ERROR)
    }

    private fun formatIfInnerDto(responseCode: String): String {
        if (!responseCode.matches(".*\\d+.*".toRegex()))
            return responseCode

        for (i in responseCode.length - 1 downTo 0) {
            val c = responseCode[i]
            if (c.isDigit())
                return responseCode.substring(i + 2)
        }

        return responseCode
    }

    private fun formatResponseCode(responseMessage: String): String {
        var responseCode = ""

        if (responseMessage.contains("필수 입력입니다.") || responseMessage.contains("첨부해 주세요")) {
            responseCode = "_FIELD_REQUIRED" // @NotBlank, @NotNull
        } else if (responseMessage.contains("~")) {
            responseCode = "_LENGTH_INVALID" // @Size
        } else if (responseMessage.contains("형식") || responseMessage.contains("조합")) {
            responseCode = "_FORMAT_INVALID" // @Pattern, @Email - format
        } else if (responseMessage.contains("중 하나여야 됩니다.")) {
            if (responseCode.contains("TYPE")) responseCode.removeSurrounding("TYPE")
            responseCode = "_TYPE_INVALID" // @Pattern - type
        } else if (responseMessage.contains("까지의 수만")) {
            responseCode = "_RANGE_INVALID" // @Range
        } else if (responseMessage.contains("0 또는 양수")) {
            responseCode = "_POSITIVE_OR_ZERO_ONLY" // @PositiveOrZero
        } else if (responseMessage.contains("양수")) {
            responseCode = "_POSITIVE_ONLY" // @Positive
        } else if (responseMessage.contains("요청")) {
            responseCode = "REQUEST_INVALID"
        }

        return responseCode
    }
}