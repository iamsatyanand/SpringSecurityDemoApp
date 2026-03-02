package com.satyanand.springsecuritydemoapp.oauth;

import org.springframework.security.oauth2.core.user.OAuth2User;

public interface OAuth2UserInfoExtractor {

    OAuthUserInfo extract(OAuth2User user);
}
