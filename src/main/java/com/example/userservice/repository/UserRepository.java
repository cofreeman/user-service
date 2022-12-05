package com.example.userservice.repository;

import com.example.userservice.dto.UserDto;
import com.example.userservice.jpa.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity,Long> {

    Optional<UserEntity> findByUserId(String userId);

    Optional<UserEntity> findByEmail(String username);
}
