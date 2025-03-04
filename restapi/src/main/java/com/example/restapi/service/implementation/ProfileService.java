package com.example.restapi.service.implementation;

import com.example.restapi.dto.ProfileDTO;
import com.example.restapi.entity.ProfileEntity;
import com.example.restapi.exceptions.ItemExistsException;
import com.example.restapi.repository.IProfileRepository;
import com.example.restapi.service.IProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProfileService implements IProfileService {

    private final IProfileRepository profileRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder encoder;
    @Override
    public ProfileDTO createProfile(ProfileDTO profileDTO) {
        if (existsByEmail(profileDTO.getEmail())){
            throw new ItemExistsException("Email already in use " + profileDTO.getEmail());
        }
        profileDTO.setPassword(encoder.encode(profileDTO.getPassword()));
        ProfileEntity profileEntity = mapToProfileEntity(profileDTO);
        profileEntity.setProfileId(UUID.randomUUID().toString());
        profileEntity = profileRepository.save(profileEntity);
        return mapToProfileDTO(profileEntity);
    }

    private Boolean existsByEmail(String email) {
        return profileRepository.findByEmail(email).isPresent();
    }

    private ProfileDTO mapToProfileDTO(ProfileEntity profileEntity) {
        return modelMapper.map(profileEntity,ProfileDTO.class);
    }
    private ProfileEntity mapToProfileEntity(ProfileDTO profileDTO) {
        return modelMapper.map(profileDTO, ProfileEntity.class);
    }
}
