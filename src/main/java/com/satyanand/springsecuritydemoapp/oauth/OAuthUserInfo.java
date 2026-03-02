package com.satyanand.springsecuritydemoapp.oauth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class OAuthUserInfo {

    private String providerUserId;
    private String email;
    private String name;
}
