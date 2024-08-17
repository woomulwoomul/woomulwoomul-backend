package com.woomulwoomul.core.common.exception

import com.woomulwoomul.core.common.constant.ErrorField
import com.woomulwoomul.core.common.constant.ExceptionCode
import com.woomulwoomul.core.common.constant.ExceptionCode.*
import com.woomulwoomul.core.common.response.CustomException
import com.woomulwoomul.core.common.response.ExceptionResponse
import io.sentry.Sentry
import jakarta.validation.ConstraintViolationException
import jakarta.validation.constraints.*
import org.springframework.http.HttpHeaders
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
        val fieldError = ex.bindingResult.fieldErrors.firstOrNull() ?: throw CustomException(SERVER_ERROR)
        val fieldErrorCode = fieldError.code ?: ""

        val codePrefix = preFormatCode(fieldErrorCode)
        val codeSuffix = ErrorField.code[fieldErrorCode.uppercase()]
        val exceptionCode = ExceptionCode.valueOf(codePrefix + codeSuffix)

        return ResponseEntity.status(exceptionCode.httpStatus)
            .body(ExceptionResponse(exceptionCode))
    }

    @ExceptionHandler(ConstraintViolationException::class)
    protected fun handleConstraintViolationException(ex: ConstraintViolationException):
            ResponseEntity<ExceptionResponse> {
        val constraintViolation = ex.constraintViolations.firstOrNull() ?: throw CustomException(SERVER_ERROR)
        val annotation = constraintViolation.constraintDescriptor.annotation

        val codePrefix = preFormatCode(constraintViolation.propertyPath.toString())
        val suffixCode = when (annotation) {
            is NotNull -> ErrorField.code[ErrorField.NOT_NULL_CONST]
            is NotBlank -> ErrorField.code[ErrorField.NOT_BLANK_CONST]
            is Size -> ErrorField.code[ErrorField.SIZE_CONST]
            is ByteSize -> ErrorField.code[ErrorField.BYTE_SIZE_CONST]
            is Pattern -> ErrorField.code[ErrorField.PATTERN_CONST]
            is Email -> ErrorField.code[ErrorField.EMAIL_CONST]
            else -> ""
        }
        val exceptionCode = ExceptionCode.valueOf(codePrefix + suffixCode)

        return ResponseEntity.status(exceptionCode.httpStatus)
            .body(ExceptionResponse(exceptionCode))
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

    private fun preFormatCode(code: String): String {
        val lastDotIndex = code.lastIndexOf('.')
        if (lastDotIndex == -1) return code

        val lastCode = code.substring(lastDotIndex + 1)

        return lastCode.fold(StringBuilder(lastCode.length + 5)) { acc, c ->
            if (c.isUpperCase() && acc.isNotEmpty()) acc.append('_')
            acc.append(c)
        }.toString().uppercase()
    }
}