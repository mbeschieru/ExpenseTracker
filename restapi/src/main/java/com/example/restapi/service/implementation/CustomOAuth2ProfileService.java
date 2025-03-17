package com.example.restapi.service.implementation;

import com.example.restapi.entity.ProfileEntity;
import com.example.restapi.repository.IProfileRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional

public class CustomOAuth2ProfileService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    private final DefaultOAuth2UserService defaultOAuth2UserService = new DefaultOAuth2UserService();
    private final IProfileRepository profileRepository;
    private final GitHubEmailFetcher gitHubEmailFetcher;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = defaultOAuth2UserService.loadUser(userRequest);

        Map<String, Object> originalAtributes = oAuth2User.getAttributes();
        Map<String,Object> updatedAtributes = new HashMap<>(originalAtributes);


        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        String userNameAttributeKey;

        String email;

        String name;

        String profileId;

        if ("google".equals(registrationId)) {
            email = (String) originalAtributes.get("email");
            name = (String) originalAtributes.get("name");
            profileId = (String) originalAtributes.get("sub");
            userNameAttributeKey = "sub";
        } else if ("github".equals(registrationId)) {
            email = (String) originalAtributes.get("email");
            if (email == null) {
              String token = userRequest.getAccessToken().getTokenValue();
              email = gitHubEmailFetcher.fetchPrimaryEmail(token);
            }
            name = originalAtributes.get("name") != null ? (String) originalAtributes.get("name") : (String) originalAtributes.get("login");
            profileId = String.valueOf(originalAtributes.get("id"));
            userNameAttributeKey = "id";
        } else {
            throw new OAuth2AuthenticationException("Unknown OAuth provider");
        }

        final String finalEmail = email;
        final String finalName = name;
        final String finalProfileId = profileId;

        updatedAtributes.put("email",finalEmail);


        profileRepository.findByEmail(finalEmail).orElseGet(() -> {
            ProfileEntity newProfile = new ProfileEntity();
            newProfile.setEmail(finalEmail);
            newProfile.setName(finalName);
            newProfile.setProfileId(finalProfileId);
            newProfile.setPassword("[OAuth2_Account_NoPassword]");
            return profileRepository.save(newProfile);
        });



        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("USER")),
                updatedAtributes,
                userNameAttributeKey
        );
    }


}
