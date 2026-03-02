package com.satyanand.springsecuritydemoapp.oauth;

import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

@Component
public class GoogleUserInfoExtractor implements OAuth2UserInfoExtractor{
    @Override
    public OAuthUserInfo extract(OAuth2User user) {
        return OAuthUserInfo.builder()
                .providerUserId(user.getAttribute("sub"))
                .email(user.getAttribute("email"))
                .name(user.getAttribute("name"))
                .build();
    }
}
