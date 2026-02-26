package com.satyanand.springsecuritydemoapp.dto;

import lombok.*;

@Getter
@Setter
@RequiredArgsConstructor
@NoArgsConstructor
@Builder
public class LoginDTO {

    private String email;
    private String password;
}
