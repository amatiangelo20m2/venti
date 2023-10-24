package com.venticonsulting.userservice.service;

import com.venticonsulting.userservice.entity.UserEntity;
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
}
