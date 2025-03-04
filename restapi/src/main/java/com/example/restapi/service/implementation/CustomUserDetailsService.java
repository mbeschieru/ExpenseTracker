package com.example.restapi.service.implementation;

import com.example.restapi.entity.ProfileEntity;
import com.example.restapi.repository.IProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
@Slf4j
@RequiredArgsConstructor
public class CustomUserDetailsService  implements UserDetailsService {

    private final IProfileRepository profileRepository;
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        ProfileEntity profile =  profileRepository.findByEmail(email)
                .orElseThrow( ()-> new UsernameNotFoundException("Profile not found for the email " + email));
        return new User(profile.getEmail() , profile.getPassword(), new ArrayList<>());
    }
}
