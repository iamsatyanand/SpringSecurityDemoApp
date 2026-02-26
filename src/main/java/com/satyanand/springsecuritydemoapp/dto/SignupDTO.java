package com.satyanand.springsecuritydemoapp.dto;

import lombok.*;

@Getter
@Setter
@RequiredArgsConstructor
@NoArgsConstructor
@Builder
public class SignupDTO {

    private String name;
    private String email;
    private String password;
}
