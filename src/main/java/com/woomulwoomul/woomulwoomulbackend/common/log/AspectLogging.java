package com.woomulwoomul.woomulwoomulbackend.common.log;

import com.woomulwoomul.woomulwoomulbackend.exception.CustomException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

@Slf4j
@Aspect
@Component
public class AspectLogging {
    @Pointcut("execution(public * com.woomulwoomul.woomulwoomulbackend.domain..*(..))" +
            "|| execution(public * com.gabojait.gabojaitspring.api..*(..))")
    private void global() {}

    @Pointcut("execution(protected * com.woomulwoomul.woomulwoomulbackend.exception.CustomExceptionHandler..*(..))")
    private void exception() {}

    @Before("global()")
    public void beforeGlobal(JoinPoint jp) {
        final MethodSignature signature = (MethodSignature) jp.getSignature();
        final String className = signature.getDeclaringType().getSimpleName();
        final Method method = signature.getMethod();
        final Parameter[] parameterNames = method.getParameters();
        final Object[] arguments = jp.getArgs();
        final int paramLength = Math.min(parameterNames.length, arguments.length);
        final String uuid = InterceptorLogging.getRequestId() == null ? "SYSTEM" : InterceptorLogging.getRequestId();

        StringBuilder param = new StringBuilder();
        for (int i = 0; i < paramLength; i++) {
            param.append(parameterNames[i].getName()).append("=");

            if (arguments[i] != null) param.append(arguments[i]);
            else param.append("null");

            if (i != parameterNames.length - 1) param.append(", ");
        }

        log.info("[{} | BEFORE] {} | {} ({})", uuid, className, method.getName(), param);
    }

    @AfterReturning(value = "global()", returning = "result")
    public void afterGlobal(JoinPoint jp, Object result) {
        final MethodSignature signature = (MethodSignature) jp.getSignature();
        final String className = signature.getDeclaringType().getSimpleName();
        final String methodName = signature.getMethod().getName();
        final String uuid = InterceptorLogging.getRequestId() == null ? "SYSTEM" : InterceptorLogging.getRequestId();

        log.info("[{} | AFTER] {} | {} | return={}", uuid, className, methodName, result);
    }

    @AfterThrowing(value = "exception()", throwing = "ex")
    public void afterThrowingGlobal(JoinPoint jp, CustomException ex) {
        final MethodSignature signature = (MethodSignature) jp.getSignature();
        final String className = signature.getDeclaringType().getSimpleName();
        final String methodName = signature.getMethod().getName();
        final String errorName = ex.getExceptionCode().name();
        final String uuid = InterceptorLogging.getRequestId() != null ? InterceptorLogging.getRequestId() : "SYSTEM";

        log.error("========== [{} | ERROR] {} | {} | code={} ==========", uuid, className, methodName, errorName);

        if (ex.getExceptionCode().getStatus().equals(HttpStatus.INTERNAL_SERVER_ERROR))
            if (ex.getThrowable() != null)
                log.error("========== [{} | DESCRIPTION] ==========", uuid, ex.getThrowable());
    }
}
