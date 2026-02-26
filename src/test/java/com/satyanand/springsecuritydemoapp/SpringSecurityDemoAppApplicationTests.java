package com.satyanand.springsecuritydemoapp;

import com.satyanand.springsecuritydemoapp.entities.User;
import com.satyanand.springsecuritydemoapp.services.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SpringSecurityDemoAppApplicationTests {

    @Autowired
    private JwtService jwtService;

    @Test
    void contextLoads() {

        User user = new User(1L, "test@gmail.com", "Password", "Satyanand");

        String token = jwtService.generateAccessToken(user);
        System.out.println(token);

        Long userId = jwtService.getUserIdFromToken(token);
        System.out.println(userId);

    }



}
