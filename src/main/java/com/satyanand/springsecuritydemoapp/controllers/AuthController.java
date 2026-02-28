package com.satyanand.springsecuritydemoapp.controllers;

import com.satyanand.springsecuritydemoapp.dto.LoginDTO;
import com.satyanand.springsecuritydemoapp.dto.LoginResponseDTO;
import com.satyanand.springsecuritydemoapp.dto.SignupDTO;
import com.satyanand.springsecuritydemoapp.dto.UserDTO;
import com.satyanand.springsecuritydemoapp.services.AuthService;
import com.satyanand.springsecuritydemoapp.services.JwtService;
import com.satyanand.springsecuritydemoapp.services.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;
    private final AuthService authService;
    private final JwtService jwtService;

    @PostMapping("/signup")
    public ResponseEntity<UserDTO> signUp(@RequestBody SignupDTO signupDTO){
        return ResponseEntity.ok(userService.signUp(signupDTO));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginDTO loginDTO, HttpServletRequest request,
                                        HttpServletResponse response){

        LoginResponseDTO token = authService.login(loginDTO);

        Cookie cookie = new Cookie("refreshToken", token.getRefreshToken());
        cookie.setHttpOnly(true);
        response.addCookie(cookie);
        return ResponseEntity.ok(token);
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponseDTO> refresh(HttpServletRequest request){
        String refreshToken = Arrays.stream(request.getCookies()).
                filter(cookie -> "refreshToken".contains(cookie.getName()))
                .findFirst()
                .map(cookie -> cookie.getValue())
                .orElseThrow(() -> new AuthenticationServiceException("refresh token not found inside the cookie"));
        LoginResponseDTO loginResponseDTO = authService.refresh(refreshToken);
        return ResponseEntity.ok(loginResponseDTO);

    }

    @PostMapping
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String header){
        if(header == null || !header.startsWith("Bearer ")){
            return ResponseEntity.badRequest().body("Invalid header");
        }

        String accessToken = header.substring(7);
        authService.logout(accessToken);
        return ResponseEntity.ok("Logged out successfully");
    }

}
