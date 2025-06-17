package com.codefarm.usersignup.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@Component
public class TraceIdFilter extends OncePerRequestFilter {

    private static final String TRACE_ID_HEADER = "X-Trace-Id";
    private static final String TRACE_ID_MDC_KEY = "traceId";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String traceId = getOrCreateTraceId(request);
            MDC.put(TRACE_ID_MDC_KEY, traceId);
            response.addHeader(TRACE_ID_HEADER, traceId);
            
            log.info("Request started - Method: {}, URI: {}", request.getMethod(), request.getRequestURI());
            
            filterChain.doFilter(request, response);
            
            log.info("Request completed - Method: {}, URI: {}, Status: {}", 
                    request.getMethod(), 
                    request.getRequestURI(), 
                    response.getStatus());
        } finally {
            MDC.remove(TRACE_ID_MDC_KEY);
        }
    }

    private String getOrCreateTraceId(HttpServletRequest request) {
        String traceId = request.getHeader(TRACE_ID_HEADER);
        if (traceId == null || traceId.isEmpty()) {
            traceId = generateTraceId();
        }
        return traceId;
    }

    private String generateTraceId() {
        return UUID.randomUUID().toString().replace("-", "");
    }
} 