package com.venticonsulting.bookingservice.service;

import com.venticonsulting.bookingservice.entity.Customer;
import com.venticonsulting.bookingservice.entity.dto.CustomerCreateEntity;
import com.venticonsulting.bookingservice.entity.dto.CustomerResponseEntity;
import com.venticonsulting.bookingservice.entity.dto.CustomerUpdateEntity;
import com.venticonsulting.bookingservice.exception.CustomerAlreadyPresentException;
import com.venticonsulting.bookingservice.exception.CustomerNotFoundException;
import com.venticonsulting.bookingservice.repository.BookingRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Slf4j
@AllArgsConstructor
public class BookingService {

    private BookingRepository bookingRepository;

    @Transactional
    public Customer addCustomer(CustomerCreateEntity userCreateEntity) {
        log.info("Save Customer: " + userCreateEntity.toString());
        Customer customerEntity = Customer
                .builder()
                .name(userCreateEntity.getName())
                .phone(userCreateEntity.getPhone())
                .email(userCreateEntity.getEmail())
                .dataTreatment(userCreateEntity.isDataTreatment())
                .build();

        if (bookingRepository.findByPhone(userCreateEntity.getPhone()).isPresent()) {
            throw new CustomerAlreadyPresentException("Phone number already in use : " + userCreateEntity.getPhone());
        } else {
            return bookingRepository.save(customerEntity);
        }
    }

    @Transactional
    public void updateUser(CustomerUpdateEntity customerUpdateEntity) {
        log.info("Update customer with id {} : {}", customerUpdateEntity.getUserId(), customerUpdateEntity);

        Optional<Customer> existingUserOpt = bookingRepository.findById(customerUpdateEntity.getUserId());

        if (existingUserOpt.isPresent()) {
            Customer existingUser = getUserEntity(customerUpdateEntity, existingUserOpt);
            bookingRepository.save(existingUser);
        } else {
            throw new CustomerNotFoundException("Customer not found with the following id: " + customerUpdateEntity.getUserId());
        }
    }

    private static Customer getUserEntity(CustomerUpdateEntity customerUpdateEntity, Optional<Customer> existingUserOpt) {
        Customer existingUser = existingUserOpt.get();

        if (customerUpdateEntity.getName() != null) {
            existingUser.setName(customerUpdateEntity.getName());
        }

        if (customerUpdateEntity.getPhone() != null) {
            existingUser.setPhone(customerUpdateEntity.getPhone());
        }

        if (customerUpdateEntity.getEmail() != null) {
            existingUser.setEmail(customerUpdateEntity.getEmail());
        }
        return existingUser;
    }

    public CustomerResponseEntity retrieveUserById(long id) {
        log.info("Retrieve customer by id : {}", id);

        Optional<Customer> userOpt = bookingRepository.findById(id);
        if(userOpt.isPresent()){
            return CustomerResponseEntity
                    .builder()
                    .email(userOpt.get().getEmail())
                    .name(userOpt.get().getName())
                    .phone(userOpt.get().getPhone())
                    .dataTreatment(userOpt.get().isDataTreatment())
                    .build();
        }else{
            throw new CustomerNotFoundException("Customer not found with the following id: " + id);
        }
    }

    public void deleteUserById(long id) {
        log.info("Delete user by id : {}", id);
        if(bookingRepository.findById(id).isPresent()){
            bookingRepository.deleteById(id);
        }else{
            throw new CustomerNotFoundException("Customer not found with the following id: " + id);
        }
    }
}
