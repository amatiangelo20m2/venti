package com.venticonsulting.bookingservice.controller;

import com.venticonsulting.bookingservice.entity.Customer;
import com.venticonsulting.bookingservice.entity.dto.CustomerCreateEntity;
import com.venticonsulting.bookingservice.entity.dto.CustomerResponseEntity;
import com.venticonsulting.bookingservice.entity.dto.CustomerUpdateEntity;
import com.venticonsulting.bookingservice.service.BookingService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/booking")
@AllArgsConstructor
public class BookingController {

    private BookingService customerService;
    @PostMapping(path = "/customer/save")
    @ResponseStatus(HttpStatus.CREATED)
    public Customer save(@RequestBody CustomerCreateEntity userCreateEntity) { return customerService.addCustomer(userCreateEntity); }

    @GetMapping(path = "/customer/retrieve")
    @ResponseStatus(HttpStatus.OK)
    public CustomerResponseEntity retrieveUserById(@RequestParam long id){
        return customerService.retrieveUserById(id);
    }
    @DeleteMapping(path = "/customer/delete")
    @ResponseStatus(HttpStatus.OK)
    public void deleteUserById(@RequestParam long id){
        customerService.deleteUserById(id);
    }

    @PutMapping(path = "/customer/update")
    @ResponseStatus(HttpStatus.OK)
    public void updateUser(@RequestBody CustomerUpdateEntity customerUpdateEntity){
        customerService.updateUser(customerUpdateEntity);
    }
}
