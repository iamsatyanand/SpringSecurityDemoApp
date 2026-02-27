package com.satyanand.springsecuritydemoapp.config;

import com.satyanand.springsecuritydemoapp.filters.JwtAuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity){

        httpSecurity
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/posts", "/auth/**").permitAll()
//                        .requestMatchers("/posts/**").hasAnyRole("ADMIN")
                        .anyRequest().authenticated())
                .csrf(csrfconfig -> csrfconfig.disable())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
//                .formLogin(Customizer.withDefaults());

        return httpSecurity.build();
    }

//    @Bean
//    public UserDetailsService inMemoryUserDetailsService() {
//        UserDetails normalUser = User
//                .withUsername("Satya")
//                .password(passwordEncoder().encode("Password"))
//                .roles("USER")
//                .build();
//
//        UserDetails adminUser = User
//                .withUsername("SatyaAdmin")
//                .password(passwordEncoder().encode("pass123"))
//                .roles("ADMIN")
//                .build();
//
//        return new InMemoryUserDetailsManager(normalUser, adminUser);
//
//    }



}
