package com.satyanand.springsecuritydemoapp.services;

import com.satyanand.springsecuritydemoapp.dto.LoginDTO;
import com.satyanand.springsecuritydemoapp.dto.LoginResponseDTO;
import com.satyanand.springsecuritydemoapp.entities.Session;
import com.satyanand.springsecuritydemoapp.entities.User;
import com.satyanand.springsecuritydemoapp.entities.UserAuthProvider;
import com.satyanand.springsecuritydemoapp.entities.enums.AuthProvider;
import com.satyanand.springsecuritydemoapp.exceptions.ResourceNotFoundException;
import com.satyanand.springsecuritydemoapp.repositories.SessionRepository;
import com.satyanand.springsecuritydemoapp.repositories.UserAuthProviderRepository;
import com.satyanand.springsecuritydemoapp.repositories.UserRepository;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final SessionRepository sessionRepository;
    private final UserAuthProviderRepository userAuthProviderRepository;


//    @Transactional
//    public LoginResponseDTO login(LoginDTO loginDTO, HttpServletResponse response) {
//        Authentication authentication = authenticationManager.authenticate(
//                new UsernamePasswordAuthenticationToken(loginDTO.getEmail(), loginDTO.getPassword())
//        );
//
//        User user = (User) authentication.getPrincipal();
//
//        return createLoginSession(user, response);
//    }

    @Transactional
    public LoginResponseDTO socialLogin(
            AuthProvider provider,
            String providerUserId,
            String email,
            String name,
            HttpServletResponse response) {

        User user = resolveUser(provider, providerUserId, email, name);

        return createLoginSession(user, response);
    }

    private User resolveUser(
            AuthProvider provider,
            String providerUserId,
            String email,
            String name) {

        Optional<UserAuthProvider> existing =
                userAuthProviderRepository
                        .findByProviderAndProviderUserId(provider, providerUserId);

        if (existing.isPresent()) {
            return existing.get().getUser();
        }

        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null) {
            user = new User();
            user.setEmail(email);
            user.setName(name);
            userRepository.save(user);
        }

        UserAuthProvider link = UserAuthProvider.builder()
                .provider(provider)
                .providerUserId(providerUserId)
                .user(user)
                .build();

        userAuthProviderRepository.save(link);

        return user;
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

    @Transactional
    public LoginResponseDTO createLoginSession(User user, HttpServletResponse response){
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        List<Session> sessions =
                sessionRepository.findByUserOrderByLastUsedAtAsc(user);

        // MAX 2 SESSION LOGIC
        if (sessions.size() >= 2) {
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

        // Set refresh token in HttpOnly cookie
        Cookie cookie = new Cookie("refreshToken", refreshToken);
        cookie.setHttpOnly(true);
//        cookie.setSecure(true);
        cookie.setPath("/auth/refresh");
        cookie.setMaxAge(7 * 24 * 60 * 60);
        response.addCookie(cookie);

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

    @Transactional
    public void logout(String accessToken){
        Session session = sessionRepository.findByAccessToken(accessToken).orElseThrow(
                () -> new JwtException("Session expired")
        );

        sessionRepository.delete(session);
    }

    @Transactional
    public void logoutAll(Long userId) {
        sessionRepository.deleteByUserId(userId);
    }
}
