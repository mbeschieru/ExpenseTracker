package com.example.restapi.repository;

import com.example.restapi.entity.ProfileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IProfileRepository extends JpaRepository<ProfileEntity,Long> {
    Optional<ProfileEntity> findByEmail(String email);
}
