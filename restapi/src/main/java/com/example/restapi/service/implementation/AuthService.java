package com.example.restapi.service.implementation;

import com.example.restapi.entity.ProfileEntity;
import com.example.restapi.repository.IProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final IProfileRepository profileRepository;

    public ProfileEntity getLoggedInProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final String email = authentication.getName();
        return profileRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Profile not found for the email + " + email));
    }
}
