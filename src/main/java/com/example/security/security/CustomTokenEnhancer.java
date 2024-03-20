package com.example.security.security;

import com.example.security.entity.UserEntity;
import com.example.security.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CustomTokenEnhancer implements TokenEnhancer {
    private final UserService userService;

    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken oAuth2AccessToken, OAuth2Authentication oAuth2Authentication) {
        User user = (User) oAuth2Authentication.getPrincipal();

        Optional<UserEntity> userEntity = userService.getUserByUsername(user.getUsername());

        if (userEntity.isEmpty()) {
            return oAuth2AccessToken;
        }

        Map<String, Object> additionalInfo = new HashMap<>();
        additionalInfo.put("id", userEntity.get().getId());

        ((DefaultOAuth2AccessToken) oAuth2AccessToken).setAdditionalInformation(additionalInfo);

        return oAuth2AccessToken;
    }
}
