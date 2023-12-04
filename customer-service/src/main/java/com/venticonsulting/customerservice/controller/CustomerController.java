package com.venticonsulting.customerservice.controller;

import com.venticonsulting.customerservice.entity.CustomerEntity;
import com.venticonsulting.customerservice.entity.dto.CustomerCreateEntity;
import com.venticonsulting.customerservice.entity.dto.CustomerResponseEntity;
import com.venticonsulting.customerservice.entity.dto.CustomerUpdateEntity;
import com.venticonsulting.customerservice.service.CustomerService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/user")
@CrossOrigin(origins = "*")
@AllArgsConstructor
public class CustomerController {

    private CustomerService customerService;

    @PostMapping(path = "/save")
    @ResponseStatus(HttpStatus.CREATED)
    public CustomerEntity save(@RequestBody CustomerCreateEntity userCreateEntity) { return customerService.addCustomer(userCreateEntity); }

    @GetMapping(path = "/retrieve")
    @ResponseStatus(HttpStatus.OK)
    public CustomerResponseEntity retrieveUserById(@RequestParam long id){
        return customerService.retrieveUserById(id);
    }

    @DeleteMapping(path = "/delete")
    @ResponseStatus(HttpStatus.OK)
    public void deleteUserById(@RequestParam long id){
        customerService.deleteUserById(id);
    }

    @PutMapping(path = "/update")
    @ResponseStatus(HttpStatus.OK)
    public void updateUser(@RequestBody CustomerUpdateEntity customerUpdateEntity){
        customerService.updateUser(customerUpdateEntity);
    }
}
