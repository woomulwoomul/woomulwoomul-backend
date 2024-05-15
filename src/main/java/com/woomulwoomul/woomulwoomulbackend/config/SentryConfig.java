package com.woomulwoomul.woomulwoomulbackend.config;

import com.woomulwoomul.woomulwoomulbackend.common.log.InterceptorLogging;
import io.sentry.Hint;
import io.sentry.SentryEvent;
import io.sentry.SentryOptions;
import io.sentry.protocol.SentryId;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

@Component
public class SentryConfig implements SentryOptions.BeforeSendCallback {

    @Value("${api.name}")
    private String serverName;

    @Override
    public SentryEvent execute(SentryEvent event, @Nullable Hint hint) {
        event.setServerName(serverName);
        event.setEventId(new SentryId(InterceptorLogging.getRequestId()));
        return event;
    }
}
