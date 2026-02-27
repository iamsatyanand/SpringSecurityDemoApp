package com.satyanand.springsecuritydemoapp.filters;

import com.satyanand.springsecuritydemoapp.entities.User;
import com.satyanand.springsecuritydemoapp.services.JwtService;
import com.satyanand.springsecuritydemoapp.services.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String requestTokenHeader = request.getHeader("Authorization");
        if(requestTokenHeader == null || !requestTokenHeader.startsWith("Bearer")){
            filterChain.doFilter(request, response);
            return;
        }

        String token = requestTokenHeader.split("Bearer ")[1];

        Long userId = jwtService.getUserIdFromToken(token);

        User user = userService.getUserById(userId);
        if(user != null){
            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                    new UsernamePasswordAuthenticationToken(user, null, null);
            SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
        }

        filterChain.doFilter(request, response);

    }
}
