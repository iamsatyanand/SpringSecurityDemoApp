package com.satyanand.springsecuritydemoapp.controllers;

import com.satyanand.springsecuritydemoapp.dto.LoginDTO;
import com.satyanand.springsecuritydemoapp.dto.LoginResponseDTO;
import com.satyanand.springsecuritydemoapp.dto.SignupDTO;
import com.satyanand.springsecuritydemoapp.dto.UserDTO;
import com.satyanand.springsecuritydemoapp.services.AuthService;
import com.satyanand.springsecuritydemoapp.services.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;
    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<UserDTO> signUp(@RequestBody SignupDTO signupDTO){
        return ResponseEntity.ok(userService.signUp(signupDTO));
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginDTO loginDTO, HttpServletRequest request,
                                        HttpServletResponse response){

        String token = authService.login(loginDTO);

        Cookie cookie = new Cookie("token", token);
        cookie.setHttpOnly(true);
        response.addCookie(cookie);
        return ResponseEntity.ok(token);
    }

}
