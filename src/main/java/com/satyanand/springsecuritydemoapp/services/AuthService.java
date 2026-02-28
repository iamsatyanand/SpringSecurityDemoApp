package com.satyanand.springsecuritydemoapp.services;

import com.satyanand.springsecuritydemoapp.dto.LoginDTO;
import com.satyanand.springsecuritydemoapp.dto.LoginResponseDTO;
import com.satyanand.springsecuritydemoapp.entities.Session;
import com.satyanand.springsecuritydemoapp.entities.User;
import com.satyanand.springsecuritydemoapp.exceptions.ResourceNotFoundException;
import com.satyanand.springsecuritydemoapp.repositories.SessionRepository;
import com.satyanand.springsecuritydemoapp.repositories.UserRepository;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.boot.web.server.Cookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final SessionRepository sessionRepository;
    private final UserService userService;


    @Transactional
    public LoginResponseDTO login(LoginDTO loginDTO) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDTO.getEmail(), loginDTO.getPassword())
        );

        User user = (User) authentication.getPrincipal();

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        List<Session> sessions = sessionRepository.findByUserOrderByLastUsedAtAsc(user);
        if(sessions.size() >= 2){
            sessionRepository.delete(sessions.get(0));
        }


        Session session = Session.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .createdAt(LocalDateTime.now())
                .lastUsedAt(LocalDateTime.now())
                .user(user)
                .build();

        sessionRepository.save(session);

        return LoginResponseDTO.builder()
                .id(user.getId())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    // no rotation of refresh token
    @Transactional
    public LoginResponseDTO refresh(String refreshToken) {
        if(!jwtService.isValidToken(refreshToken)){
            throw new JwtException("Invalid token");
        }

        Session session = sessionRepository.findByRefreshToken( refreshToken)
                .orElseThrow(() -> new JwtException("Session expired"));

        User user = session.getUser();

        session.setLastUsedAt(LocalDateTime.now());

        String accessToken = jwtService.generateAccessToken(user);

        session.setAccessToken(accessToken);

        sessionRepository.save(session);

        return LoginResponseDTO.builder()
                .id(user.getId())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    // rotational refresh token which wil keep session alive if used with sliding session by always updating the expiration
    @Transactional
    public LoginResponseDTO refreshRotation(String refreshToken) {

        if (!jwtService.isValidToken(refreshToken)) {
            throw new JwtException("Invalid or expired refresh token");
        }

        Session session = sessionRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new JwtException("Session expired"));

        User user = session.getUser();

        String newAccessToken = jwtService.generateAccessToken(user);
        String newRefreshToken = jwtService.generateRefreshToken(user);

        session.setAccessToken(newAccessToken);
        session.setRefreshToken(newRefreshToken);
        session.setLastUsedAt(LocalDateTime.now());

        sessionRepository.save(session);

        return LoginResponseDTO.builder()
                .id(user.getId())
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .build();
    }
}
