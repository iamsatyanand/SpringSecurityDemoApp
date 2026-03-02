package com.satyanand.springsecuritydemoapp.oauth;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OAuth2UserInfoExtractorFactory {

    private final GoogleUserInfoExtractor googleUserInfoExtractor;
    private final GithubUserInfoExtractor githubUserInfoExtractor;

    public OAuth2UserInfoExtractor getExtractor(String registrationId){
        return switch (registrationId){
            case "google" -> googleUserInfoExtractor;
            case "github" -> githubUserInfoExtractor;
            default -> throw new IllegalArgumentException(
                    "Unsupported provider "+ registrationId
            );
        };
    }
}
