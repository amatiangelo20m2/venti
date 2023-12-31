package com.venticonsulting.userservice.controller;

import com.venticonsulting.userservice.entity.dto.UpdateUserEntity;
import com.venticonsulting.userservice.entity.dto.UserCreateEntity;
import com.venticonsulting.userservice.entity.dto.UserResponseEntity;
import com.venticonsulting.userservice.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
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
    public long save(@RequestBody UserCreateEntity userCreateEntity) { return userService.addUser(userCreateEntity); }

    @GetMapping(path = "/retrieve")
    @ResponseStatus(HttpStatus.OK)
    public UserResponseEntity retrieveUserById(@RequestParam long id){
        return userService.retrieveUserById(id);
    }

    @DeleteMapping(path = "/delete")
    @ResponseStatus(HttpStatus.OK)
    public void deleteUserById(@RequestParam long id){
        userService.deleteUserById(id);
    }

    @PutMapping(path = "/update")
    @ResponseStatus(HttpStatus.OK)
    public void updateUser(@RequestBody UpdateUserEntity userEntity){
        userService.updateUser(userEntity);
    }
}
