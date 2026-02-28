package com.satyanand.springsecuritydemoapp.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginResponseDTO {

    private Long id;
    private String accessToken;
    private String refreshToken;
}
