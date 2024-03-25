package com.venticonsulting.authservice.service;

import com.venticonsulting.authservice.entity.JwtEntity;
import com.venticonsulting.authservice.entity.ProfileStatus;
import com.venticonsulting.authservice.entity.SignInMethod;
import com.venticonsulting.authservice.entity.UserEntity;
import com.venticonsulting.authservice.entity.dto.*;
import com.venticonsulting.authservice.exception.customexceptions.BadCredentialsException;
import com.venticonsulting.authservice.exception.customexceptions.UserAlreadyExistException;
import com.venticonsulting.authservice.exception.customexceptions.UserNotFoundException;
import com.venticonsulting.authservice.repository.AuthRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
@AllArgsConstructor
public class AuthService {

    private final AuthRepository userRepository;

    private PasswordEncoder passwordEncoder;

    private JwtService jwtService;

    @Transactional
    public AuthResponseEntity signUp(Credentials credentials) {
        log.info("Save user: " + credentials.getEmail());

        if(userRepository.findByEmail(credentials.getEmail()).isPresent()){
            log.error("Exception saving user. Mail " + credentials.getEmail() + " is already been used.");
            throw new UserAlreadyExistException("Mail " + credentials.getEmail() + " is already been used");
        }
        UserEntity userEntityBuild = UserEntity
                .builder()
                .name(credentials.getName())
                .phone(null)
                .password(passwordEncoder.encode(credentials.getPassword()))
                .email(credentials.getEmail())
                .profileStatus(ProfileStatus.ONLINE)
                .signInMethod(SignInMethod.PASSWORD)
                .avatar("")
                .id(0)
                .build();

        UserEntity userEntity = userRepository.save(userEntityBuild);

        return AuthResponseEntity.builder()
                .user(UserResponseEntity
                        .builder()
                        .email(userEntity.getEmail())
                        .name(userEntity.getName())
                        .phone(userEntity.getPhone())
                        .avatar(userEntity.getAvatar())
                        .userCode(userEntity.getUserCode())
                        .status(userEntity.getProfileStatus())
                        .build())

                .accessToken(jwtService.generateToken(credentials.getEmail(), userEntity.getUserCode()))
                .build();
    }

    @Transactional
    public AuthResponseEntity signInWithGoogle(Credentials credentials) {
        log.info("Sign in user by google: " + credentials.getEmail());
        Optional<UserEntity> userByEmail = userRepository.findByEmail(credentials.getEmail());
        if(userByEmail.isPresent()){
            return AuthResponseEntity.builder()
                    .user(UserResponseEntity
                            .builder()
                            .email(userByEmail.get().getEmail())
                            .name(userByEmail.get().getName())
                            .phone(userByEmail.get().getPhone())
                            .avatar(userByEmail.get().getAvatar())
                            .userCode(userByEmail.get().getUserCode())
                            .status(userByEmail.get().getProfileStatus())
                            .build())

                    .accessToken(jwtService.generateToken(credentials.getEmail(), userByEmail.get().getUserCode()))
                    .build();
        }else{
            UserEntity userEntityBuild = UserEntity
                    .builder()
                    .name(credentials.getName())
                    .phone(null)
                    .password(passwordEncoder.encode(credentials.getPassword()))
                    .email(credentials.getEmail())
                    .profileStatus(ProfileStatus.ONLINE)
                    .signInMethod(SignInMethod.GOOGLE)
                    .avatar("")
                    .id(0)
                    .build();

            UserEntity userEntity = userRepository.save(userEntityBuild);
            return AuthResponseEntity.builder()
                    .user(UserResponseEntity
                            .builder()
                            .email(userEntity.getEmail())
                            .name(userEntity.getName())
                            .phone(userEntity.getPhone())
                            .avatar(userEntity.getAvatar())
                            .userCode(userEntity.getUserCode())
                            .status(userEntity.getProfileStatus())
                            .build())

                    .accessToken(jwtService.generateToken(credentials.getEmail(),
                            userEntity.getUserCode()))
                    .build();
        }





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
                    .name(userOpt.get().getName())
                    .phone(userOpt.get().getPhone())
                    .status(userOpt.get().getProfileStatus())
                    .avatar(userOpt.get().getAvatar())
                    .userCode(userOpt.get().getUserCode())
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
            if (passwordEncoder.matches(credentials.getPassword(), existingUserOpt.get().getPassword())) {
                return AuthResponseEntity.builder()
                        .user(UserResponseEntity
                                .builder()
                                .email(existingUserOpt.get().getEmail())
                                .name(existingUserOpt.get().getName())
                                .phone(existingUserOpt.get().getPhone())
                                .avatar(existingUserOpt.get().getAvatar())
                                .status(existingUserOpt.get().getProfileStatus())
                                .userCode(existingUserOpt.get().getUserCode())
                                .build())
                        .accessToken(jwtService.generateToken(credentials.getEmail(), existingUserOpt.get().getUserCode()))
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


    public AuthResponseEntity signInWithAccessToken(JwtEntity accessToken) {
        log.info("Token: " + accessToken.getAccessToken());

        Optional<UserEntity> existingUserOpt = userRepository.findByEmail(jwtService.extractUsername(accessToken.getAccessToken()));

        if(existingUserOpt.isPresent()){
            return AuthResponseEntity.builder()
                    .user(UserResponseEntity
                            .builder()
                            .email(existingUserOpt.get().getEmail())
                            .name(existingUserOpt.get().getName())
                            .phone(existingUserOpt.get().getPhone())
                            .avatar(existingUserOpt.get().getAvatar())
                            .status(existingUserOpt.get().getProfileStatus())
                            .userCode(existingUserOpt.get().getUserCode())
                            .build())
                    .accessToken(jwtService.generateToken(existingUserOpt.get().getEmail(), existingUserOpt.get().getUserCode()))
                    .build();
        }else{
            log.error("Utente non trovato con la seguente mail [" + jwtService.extractUsername(accessToken.getAccessToken()) + "] dopo autenticatione con jwt");
            throw new UserNotFoundException("Utente non trovato con la seguente mail [" + jwtService.extractUsername(accessToken.getAccessToken()) + "] dopo autenticatione con jwt");
        }

    }
}
