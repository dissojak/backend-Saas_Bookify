package com.bookify.backendbookify_saas.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.Map;

@Component
public class RequestMappingPrinter {

    private static final Logger log = LoggerFactory.getLogger(RequestMappingPrinter.class);

    private final RequestMappingHandlerMapping mapping;

    public RequestMappingPrinter(@Qualifier("requestMappingHandlerMapping") RequestMappingHandlerMapping mapping) {
        this.mapping = mapping;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void printMappings() {
        log.info("=== Registered request mappings ===");
        for (Map.Entry<RequestMappingInfo, org.springframework.web.method.HandlerMethod> e : mapping.getHandlerMethods().entrySet()) {
            RequestMappingInfo info = e.getKey();

            // Guard against RequestMappingInfo variants that may have null conditions (some infrastructure handlers)
            var patternsCond = info.getPatternsCondition();
            var methodsCond = info.getMethodsCondition();

            String patterns = patternsCond != null ? patternsCond.getPatterns().toString() : "[]";
            String methods = methodsCond != null ? methodsCond.getMethods().toString() : "[]";

            String handler = e.getValue() != null ? e.getValue().toString() : "<no-handler>";
            log.info("mapping: {} {} -> {}", methods, patterns, handler);
        }
        log.info("=== End mappings ===");
    }
}
