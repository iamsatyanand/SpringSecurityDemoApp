package com.satyanand.springsecuritydemoapp.services;

import com.satyanand.springsecuritydemoapp.dto.LoginDTO;
import com.satyanand.springsecuritydemoapp.dto.LoginResponseDTO;
import com.satyanand.springsecuritydemoapp.entities.User;
import com.satyanand.springsecuritydemoapp.exceptions.ResourceNotFoundException;
import com.satyanand.springsecuritydemoapp.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.boot.web.server.Cookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;


    public String login(LoginDTO loginDTO) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDTO.getEmail(), loginDTO.getPassword())
        );

        User user = (User) authentication.getPrincipal();

        return jwtService.generateAccessToken(user);
    }
}
