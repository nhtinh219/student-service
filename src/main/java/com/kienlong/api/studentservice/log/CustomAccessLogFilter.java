package com.kienlong.api.studentservice.log;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Slf4j
public class CustomAccessLogFilter extends OncePerRequestFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomAccessLogFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        if (request.getRequestURI().toLowerCase().contains("/api")) {
            long time = System.currentTimeMillis();
            try {
                filterChain.doFilter(request, response);
            } finally {
                time = System.currentTimeMillis() - time;

                String remoteIpAddress = request.getHeader("X-FORWARDED-FOR");

                if (remoteIpAddress == null || remoteIpAddress.isEmpty()){
                    remoteIpAddress = request.getRemoteAddr();
                }

                log.info("{} {} {} {} {} {}ms",
                        remoteIpAddress, request.getMethod(), request.getRequestURI(), request.getContentType(), response.getStatus(), time);
            }
        }
    }
}
