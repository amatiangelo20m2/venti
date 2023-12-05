package com.venticonsulting.userservice.service;

import com.venticonsulting.userservice.entity.ProfileStatus;
import com.venticonsulting.userservice.entity.UserEntity;
import com.venticonsulting.userservice.entity.dto.UpdateUserEntity;
import com.venticonsulting.userservice.entity.dto.UserCreateEntity;
import com.venticonsulting.userservice.entity.dto.UserResponseEntity;
import com.venticonsulting.userservice.exception.UserNotFoundException;
import com.venticonsulting.userservice.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
@AllArgsConstructor
public class UserService {

    private UserRepository userRepository;

    @Transactional
    public UserResponseEntity addUser(UserCreateEntity userCreateEntity) {
        log.info("Save user: " + userCreateEntity.toString());
        UserEntity userEntityBuild = UserEntity
                .builder()
                .name(userCreateEntity.getName())
                .lastname(userCreateEntity.getLastname())
                .phone(userCreateEntity.getPhone())
                .email(userCreateEntity.getEmail())
                .profileStatus(ProfileStatus.ONLINE)
                .avatar(userCreateEntity.getAvatar())
                .id(0).build();

        UserEntity userEntity = userRepository.save(userEntityBuild);

        return UserResponseEntity
                .builder()
                .email(userEntity.getEmail())
                .lastname(userEntity.getLastname())
                .name(userEntity.getName())
                .phone(userEntity.getPhone())
                .avatar(userEntity.getAvatar())
                .profileStatus(userEntity.getProfileStatus())
                .jwt("XXXXXXXXXXXXXXXXXXXX")
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

    private static UserEntity getUserEntity(UpdateUserEntity updateUserEntity, Optional<UserEntity> existingUserOpt) {
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
                    .profileStatus(userOpt.get().getProfileStatus())
                    .avatar(userOpt.get().getAvatar())
                    .jwt("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX")
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
}
