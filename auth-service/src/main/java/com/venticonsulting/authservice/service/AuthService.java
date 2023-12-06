package com.venticonsulting.authservice.service;

import com.venticonsulting.authservice.entity.ProfileStatus;
import com.venticonsulting.authservice.entity.UserEntity;
import com.venticonsulting.authservice.entity.dto.*;
import com.venticonsulting.authservice.exception.BadCredentialsException;
import com.venticonsulting.authservice.exception.UserAlreadyExistException;
import com.venticonsulting.authservice.exception.UserNotFoundException;
import com.venticonsulting.authservice.repository.AuthRepository;
import com.venticonsulting.authservice.service.jwt.JwtTokenUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
@AllArgsConstructor
public class AuthService {

    private final AuthRepository userRepository;

    @Transactional
    public AuthResponseEntity signUp(Credentials credentials) {
        log.info("Save user: " + credentials.getEmail());

        if(userRepository.findByEmail(credentials.getEmail()).isPresent()){
            log.error("Exception saving user. Mail " + credentials.getEmail() + " is already been used.");
            throw new UserAlreadyExistException("Mail " + credentials.getEmail() + " is already been used");
        }
        UserEntity userEntityBuild = UserEntity
                .builder()
                .name("")
                .lastname("")
                .phone("")
                .password(UserEntity.encryptPassword(credentials.getPassword()))
                .email(credentials.getEmail())
                .profileStatus(ProfileStatus.ONLINE)
                .avatar("")
                .id(0)
                .build();

        UserEntity userEntity = userRepository.save(userEntityBuild);

        return AuthResponseEntity.builder()
                .user(UserResponseEntity
                        .builder()
                        .email(userEntity.getEmail())
                        .lastname(userEntity.getLastname())
                        .name(userEntity.getName())
                        .phone(userEntity.getPhone())
                        .avatar(userEntity.getAvatar())
                        .status(userEntity.getProfileStatus())
                        .build())
                .accessToken(generateNewJWTToken(credentials.getEmail()))
                .build();
    }

    @Transactional
    public void updateUser(UpdateUserEntity updateUserEntity) {
        log.info("Update user with id {} : {}", updateUserEntity.getUserId(), updateUserEntity);

        Optional<UserEntity> existingUserOpt = userRepository.findById(updateUserEntity.getUserId());

        if (existingUserOpt.isPresent()) {
            UserEntity existingUser = getUserEntity(updateUserEntity, existingUserOpt);
            userRepository.save(existingUser);
        } else {
            throw new UserNotFoundException("User not found with the following id: " + updateUserEntity.getUserId());
        }
    }

    private static UserEntity getUserEntity(UpdateUserEntity updateUserEntity,
                                            Optional<UserEntity> existingUserOpt) {
        UserEntity existingUser = existingUserOpt.get();

        if (updateUserEntity.getName() != null) {
            existingUser.setName(updateUserEntity.getName());
        }

        if (updateUserEntity.getLastname() != null) {
            existingUser.setLastname(updateUserEntity.getLastname());
        }

        if (updateUserEntity.getPhone() != null) {
            existingUser.setPhone(updateUserEntity.getPhone());
        }

        if (updateUserEntity.getEmail() != null) {
            existingUser.setEmail(updateUserEntity.getEmail());
        }
        return existingUser;
    }

    public UserResponseEntity retrieveUserByEmail(String email) {
        log.info("Retrieve user by id : {}", email);

        Optional<UserEntity> userOpt = userRepository.findByEmail(email);
        if(userOpt.isPresent()){
            return UserResponseEntity
                    .builder()
                    .email(userOpt.get().getEmail())
                    .lastname(userOpt.get().getLastname())
                    .name(userOpt.get().getName())
                    .phone(userOpt.get().getPhone())
                    .status(userOpt.get().getProfileStatus())
                    .avatar(userOpt.get().getAvatar())
                    .build();
        }else{
            log.error("User not found with the following email [{}] ", email);
            throw new UserNotFoundException("User not found with the following email: " + email);
        }
    }

    public void deleteUserByEmail(String email) {
        log.info("Delete user by email : {}", email);
        if(userRepository.findByEmail(email).isPresent()){
            userRepository.deleteByEmail(email);
        }else{
            log.error("User not found with the following email [{}] ", email);
            throw new UserNotFoundException("User not found with the following email: " + email);
        }
    }

    public AuthResponseEntity signIn(Credentials credentials) {

        log.info("Sign in user with email : " + credentials.getEmail());

        Optional<UserEntity> existingUserOpt = userRepository.findByEmail(credentials.getEmail());
        if (existingUserOpt.isPresent()) {
            if (existingUserOpt.get().checkPassword(credentials.getPassword())) {
                return AuthResponseEntity.builder()
                        .user(UserResponseEntity
                                .builder()
                                .email(existingUserOpt.get().getEmail())
                                .lastname(existingUserOpt.get().getLastname())
                                .name(existingUserOpt.get().getName())
                                .phone(existingUserOpt.get().getPhone())
                                .avatar(existingUserOpt.get().getAvatar())
                                .status(existingUserOpt.get().getProfileStatus())
                                .build())
                        .accessToken(generateNewJWTToken(credentials.getEmail()))
                        .build();

            }else{
                log.error("Password errata per mail [" + credentials.getEmail() + "]");
                throw new BadCredentialsException("Password errata per mail [" + credentials.getEmail() + "]");
            }
        }else{
            log.error("Utente non trovato con la seguente mail [" + credentials.getEmail() + "]");
            throw new UserNotFoundException("Utente non trovato con la seguente mail [" + credentials.getEmail() + "]");
        }
    }
    private String generateNewJWTToken(String user) {
        return JwtTokenUtils.generateToken(user, 3600000);
    }

    public AuthResponseEntity signInWithAccessToken(String accessToken) {
        log.info("Token: " + accessToken);

        Jws<Claims> claimsJws = JwtTokenUtils.parseToken(accessToken);
        Claims claims = claimsJws.getBody();


        for (String key : claims.keySet()) {
            Object value = claims.get(key);
            log.info("Claim key: " + value);
        }
        String username = claims.get("username", String.class);
        String role = claims.get("role", String.class);

        Optional<UserEntity> existingUserOpt = userRepository.findByEmail(username);

        if(existingUserOpt.isPresent()){
            return AuthResponseEntity.builder()
                    .user(UserResponseEntity
                            .builder()
                            .email(existingUserOpt.get().getEmail())
                            .lastname(existingUserOpt.get().getLastname())
                            .name(existingUserOpt.get().getName())
                            .phone(existingUserOpt.get().getPhone())
                            .avatar(existingUserOpt.get().getAvatar())
                            .status(existingUserOpt.get().getProfileStatus())
                            .build())
                    .accessToken(generateNewJWTToken(existingUserOpt.get().getEmail()))
                    .build();
        }else{
            log.error("Utente non trovato con la seguente mail [" + username + "] dopo autenticatione con jwt");
            throw new UserNotFoundException("Utente non trovato con la seguente mail [" + username + "] dopo autenticatione con jwt");
        }

    }
}
