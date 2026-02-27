package com.satyanand.springsecuritydemoapp.filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
@Component
public class LoggingFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(LoggingFilter.class);
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        long startTime = System.currentTimeMillis();

        // BEFORE
        log.info("Incoming request : {} {}",
                request.getMethod(),
                request.getRequestURI());

        filterChain.doFilter(request, response);

        // AFTER
        long duration = System.currentTimeMillis() - startTime;

        log.info("Outgoing Response: status : {} | Time taken : {} ms",
                response.getStatus(),
                duration);



    }
}
