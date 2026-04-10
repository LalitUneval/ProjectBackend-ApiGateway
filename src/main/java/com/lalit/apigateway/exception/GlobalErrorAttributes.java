package com.lalit.apigateway.exception;

import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.webflux.error.DefaultErrorAttributes;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;

import java.time.LocalDateTime;
import java.util.Map;

@Component
public class GlobalErrorAttributes extends DefaultErrorAttributes {

    @Override
    public Map<String, Object> getErrorAttributes(ServerRequest request, ErrorAttributeOptions options) {
        Map<String, Object> errorAttributes = super.getErrorAttributes(request, options);
        Throwable error = getError(request);

        // This part replaces your "if (ex instanceof ResponseStatusException)" logic
        if (error instanceof org.springframework.web.server.ResponseStatusException ex) {
            errorAttributes.put("status", ex.getStatusCode().value());
            errorAttributes.put("message", ex.getReason());
        } else if (error != null) {
            errorAttributes.put("message", error.getMessage());
        }

        errorAttributes.put("timestamp", LocalDateTime.now().toString());
        errorAttributes.put("developer", "Lalit");
        return errorAttributes;
    }
}