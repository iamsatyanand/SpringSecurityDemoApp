package com.satyanand.springsecuritydemoapp.services;

import com.satyanand.springsecuritydemoapp.dto.LoginDTO;
import com.satyanand.springsecuritydemoapp.dto.LoginResponseDTO;
import com.satyanand.springsecuritydemoapp.entities.Session;
import com.satyanand.springsecuritydemoapp.entities.User;
import com.satyanand.springsecuritydemoapp.exceptions.ResourceNotFoundException;
import com.satyanand.springsecuritydemoapp.repositories.SessionRepository;
import com.satyanand.springsecuritydemoapp.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.boot.web.server.Cookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final SessionRepository sessionRepository;


    public String login(LoginDTO loginDTO) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDTO.getEmail(), loginDTO.getPassword())
        );

        User user = (User) authentication.getPrincipal();

        String token = jwtService.generateAccessToken(user);

        sessionRepository.deleteByUserId(user.getId());

        Session session = Session.builder()
                .token(token)
                .createdAt(LocalDateTime.now())
                .user(user)
                .build();

        sessionRepository.save(session);

        return token;
    }
}
