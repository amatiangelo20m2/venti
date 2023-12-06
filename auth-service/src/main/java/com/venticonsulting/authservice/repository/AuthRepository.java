package com.venticonsulting.authservice.repository;

import com.venticonsulting.authservice.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuthRepository extends JpaRepository<UserEntity, Long> {

//    @Query("SELECT user FROM UserEntity user WHERE user.email =?1")
    Optional<UserEntity> findByEmail(String email);

    void deleteByEmail(String email);
}
