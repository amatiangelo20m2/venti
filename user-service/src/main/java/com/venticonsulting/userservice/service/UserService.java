package com.venticonsulting.userservice.service;

import com.venticonsulting.userservice.entity.UserEntity;
import com.venticonsulting.userservice.entity.dto.UpdateUserEntity;
import com.venticonsulting.userservice.entity.dto.UserCreateEntity;
import com.venticonsulting.userservice.entity.dto.UserResponseEntity;
import com.venticonsulting.userservice.exception.UserNotFoundException;
import com.venticonsulting.userservice.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.sql.Update;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
@AllArgsConstructor
public class UserService {

    private UserRepository userRepository;

    @Transactional
    public long addUser(UserCreateEntity userCreateEntity) {
        log.info("Save user: " + userCreateEntity.toString());
        UserEntity userEntityBuild = UserEntity
                .builder()
                .name(userCreateEntity.getName())
                .lastname(userCreateEntity.getLastname())
                .phone(userCreateEntity.getPhone())
                .email(userCreateEntity.getEmail())
                .userId(0).build();

        UserEntity userEntity = userRepository.save(userEntityBuild);
        return userEntity.getUserId();
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

    public UserResponseEntity retrieveUserById(long id) {
        log.info("Retrieve user by id : {}", id);

        Optional<UserEntity> userOpt = userRepository.findById(id);
        if(userOpt.isPresent()){
            return UserResponseEntity
                    .builder()
                    .email(userOpt.get().getEmail())
                    .lastname(userOpt.get().getLastname())
                    .name(userOpt.get().getName())
                    .phone(userOpt.get().getPhone())
                    .build();
        }else{
            throw new UserNotFoundException("User not found with the following id: " + id);
        }
    }

    public void deleteUserById(long id) {
        log.info("Delete user by id : {}", id);
        if(userRepository.findById(id).isPresent()){
            userRepository.deleteById(id);
        }else{
            throw new UserNotFoundException("User not found with the following id: " + id);
        }
    }
}
