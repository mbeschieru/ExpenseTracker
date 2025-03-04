package com.example.restapi.service;

import com.example.restapi.dto.ProfileDTO;
import com.example.restapi.entity.ProfileEntity;

public interface IProfileService {
    ProfileDTO createProfile(ProfileDTO profileDTO);
}
