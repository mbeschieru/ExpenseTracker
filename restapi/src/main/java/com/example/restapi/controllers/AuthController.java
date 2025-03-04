package com.example.restapi.controllers;

import com.example.restapi.dto.ProfileDTO;
import com.example.restapi.io.AuthRequest;
import com.example.restapi.io.AuthResponse;
import com.example.restapi.io.ProfileRequest;
import com.example.restapi.io.ProfileResponse;
import com.example.restapi.service.IProfileService;
import com.example.restapi.service.implementation.CustomUserDetailsService;
import com.example.restapi.service.implementation.TokenBlackListService;
import com.example.restapi.util.JwtTokenUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
public class AuthController {
    private final IProfileService profileService;
    private final ModelMapper modelMapper;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;
    private final CustomUserDetailsService customUserDetailsService;
    private final TokenBlackListService tokenBlackListService;

    @PostMapping("/register")
    ProfileResponse createProfile(@Valid @RequestBody ProfileRequest profileRequest) {
        ProfileDTO profileDTO = mapToProfileDTO(profileRequest);
        profileDTO = profileService.createProfile(profileDTO);
        return mapToProfileResponse(profileDTO);
    }

    @PostMapping("/login")
    public AuthResponse authenticateUser(@RequestBody AuthRequest authRequest) throws Exception {
        final UserDetails userDetails = customUserDetailsService.loadUserByUsername(authRequest.getEmail());
        authenticate(authRequest);
        final String token = jwtTokenUtil.generateToken(userDetails);
        return new AuthResponse(token,authRequest.getEmail());
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PostMapping("/logout")
        private void logout (HttpServletRequest request) {
            String token = extractJwtTokenFromRequest(request);
            if (token != null) {
                tokenBlackListService.addTokenToBlacklist(token);
            }
        }
    private void authenticate(AuthRequest authRequest) throws Exception {
        try{
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getEmail(),authRequest.getPassword()));
        }catch(DisabledException ex) {
            throw new Exception("Profile disabled");
        }catch(BadCredentialsException ex) {
            throw new Exception("Bad credentials");
        }
    }
    private ProfileResponse mapToProfileResponse(ProfileDTO profileDTO) {
        return modelMapper.map(profileDTO, ProfileResponse.class);
    }

    private ProfileDTO mapToProfileDTO(ProfileRequest profileRequest) {
        return modelMapper.map(profileRequest, ProfileDTO.class);
    }

    private String extractJwtTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
