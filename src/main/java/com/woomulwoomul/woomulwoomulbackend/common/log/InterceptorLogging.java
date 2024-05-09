package com.woomulwoomul.woomulwoomulbackend.common.log;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
import org.springframework.util.StopWatch;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.util.UUID;

@Slf4j
@Getter
public class InterceptorLogging implements HandlerInterceptor {

    private static final ThreadLocal<StopWatch> stopWatchThreadLocal = new ThreadLocal<>();
    private static final ThreadLocal<String> uuidThreadLocal = new ThreadLocal<>();

    public boolean preHandle(HttpServletRequest request,
                             @Nullable HttpServletResponse response,
                             @Nullable Object handler) {
        String uuid = UUID.randomUUID().toString();
        String method = request.getMethod();
        String uri = request.getRequestURI();

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        log.info("========== [{} | START] {} {} ==========", uuid, method, uri);

        uuidThreadLocal.set(uuid);
        stopWatchThreadLocal.set(stopWatch);

        return true;
    }

    public void postHandle(HttpServletRequest request,
                           @Nullable HttpServletResponse response,
                           @Nullable Object handler,
                           ModelAndView modelAndView) {
        StopWatch stopWatch = stopWatchThreadLocal.get();
        stopWatch.stop();

        long time = stopWatch.getTotalTimeMillis();
        String uuid = uuidThreadLocal.get();
        String method = request.getMethod();
        String uri = request.getRequestURI();

        log.info("========== [{} | FINISH] {} {} | time={}ms ==========", uuid, method, uri, time);

        uuidThreadLocal.remove();
        stopWatchThreadLocal.remove();
    }

    public static String getRequestId() {
        return uuidThreadLocal.get();
    }
}
