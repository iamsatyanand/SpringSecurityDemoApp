package com.satyanand.springsecuritydemoapp.handler;

import com.satyanand.springsecuritydemoapp.dto.LoginResponseDTO;
import com.satyanand.springsecuritydemoapp.entities.enums.AuthProvider;
import com.satyanand.springsecuritydemoapp.oauth.OAuth2UserInfoExtractor;
import com.satyanand.springsecuritydemoapp.oauth.OAuth2UserInfoExtractorFactory;
import com.satyanand.springsecuritydemoapp.oauth.OAuthUserInfo;
import com.satyanand.springsecuritydemoapp.services.AuthService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final OAuth2UserInfoExtractorFactory factory;
    private final AuthService authService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) authentication;

        String registrationId = token.getAuthorizedClientRegistrationId();

        OAuth2User oAuth2User = token.getPrincipal();

        OAuth2UserInfoExtractor extractor = factory.getExtractor(registrationId);

        OAuthUserInfo info = extractor.extract(oAuth2User);


        LoginResponseDTO loginResponse =
                authService.socialLogin(
                        AuthProvider.valueOf(registrationId.toUpperCase()),
                        info.getProviderUserId(),
                        info.getEmail(),
                        info.getName(),
                        response
                );

        response.setContentType("application/json");
        response.getWriter().write(
                new ObjectMapper().writeValueAsString(loginResponse)
        );
    }
}
