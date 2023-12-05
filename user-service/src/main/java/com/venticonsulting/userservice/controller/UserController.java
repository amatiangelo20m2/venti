package com.venticonsulting.userservice.controller;

import com.venticonsulting.userservice.entity.dto.UpdateUserEntity;
import com.venticonsulting.userservice.entity.dto.UserCreateEntity;
import com.venticonsulting.userservice.entity.dto.UserResponseEntity;
import com.venticonsulting.userservice.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/user")
@CrossOrigin(origins = "*")
@AllArgsConstructor
public class UserController {

    private UserService userService;

    @PostMapping(path = "/save")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponseEntity save(@RequestBody UserCreateEntity userCreateEntity) { return userService.addUser(userCreateEntity); }

    @GetMapping(path = "/retrieve")
    @ResponseStatus(HttpStatus.OK)
    public UserResponseEntity retrieveUserByEmail(@RequestParam String email){
        return userService.retrieveUserByEmail(email);
    }

    @DeleteMapping(path = "/delete")
    @ResponseStatus(HttpStatus.OK)
    public void deleteUserById(@RequestParam String email){
        userService.deleteUserByEmail(email);
    }

    @PutMapping(path = "/update")
    @ResponseStatus(HttpStatus.OK)
    public void updateUser(@RequestBody UpdateUserEntity userEntity){
        userService.updateUser(userEntity);
    }


}
