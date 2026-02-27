package com.satyanand.springsecuritydemoapp.services;

import com.satyanand.springsecuritydemoapp.dto.SignupDTO;
import com.satyanand.springsecuritydemoapp.dto.UserDTO;
import com.satyanand.springsecuritydemoapp.entities.User;
import com.satyanand.springsecuritydemoapp.exceptions.ResourceNotFoundException;
import com.satyanand.springsecuritydemoapp.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) {
        return userRepository.findByEmail(username).orElseThrow(() -> new BadCredentialsException("User not found with email: " + username));
    }

    public User getUserById(Long id){
        return userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + id));
    }

    public UserDTO signUp(SignupDTO signupDTO)  {
        signupDTO.setEmail(signupDTO.getEmail().toLowerCase());
        if(userRepository.existsByEmail(signupDTO.getEmail())){
            throw new IllegalStateException("Email already registered");
        }

        User toBeCreatedUser = modelMapper.map(signupDTO, User.class);
        toBeCreatedUser.setPassword(passwordEncoder.encode(toBeCreatedUser.getPassword()));

        return modelMapper.map(userRepository.save(toBeCreatedUser), UserDTO.class);
    }
}
