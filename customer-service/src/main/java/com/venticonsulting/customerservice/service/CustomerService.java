package com.venticonsulting.customerservice.service;

import com.venticonsulting.customerservice.entity.CustomerEntity;
import com.venticonsulting.customerservice.entity.dto.CustomerCreateEntity;
import com.venticonsulting.customerservice.entity.dto.CustomerResponseEntity;
import com.venticonsulting.customerservice.entity.dto.CustomerUpdateEntity;
import com.venticonsulting.customerservice.exception.CustomerAlreadyPresentException;
import com.venticonsulting.customerservice.exception.CustomerNotFoundException;
import com.venticonsulting.customerservice.repository.CustomerRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Slf4j
@AllArgsConstructor
public class CustomerService {

    private CustomerRepository customerRepository;

    @Transactional
    public CustomerEntity addCustomer(CustomerCreateEntity userCreateEntity) {
        log.info("Save Customer: " + userCreateEntity.toString());
        CustomerEntity customerEntity = CustomerEntity
                .builder()
                .name(userCreateEntity.getName())
                .lastname(userCreateEntity.getLastname())
                .phone(userCreateEntity.getPhone())
                .email(userCreateEntity.getEmail())
                .dataTreatment(userCreateEntity.isDataTreatment())
                .build();

        if (customerRepository.findByPhone(userCreateEntity.getPhone()).isPresent()) {
            throw new CustomerAlreadyPresentException("Phone number already in use : " + userCreateEntity.getPhone());
        } else {
            return customerRepository.save(customerEntity);
        }
    }

    @Transactional
    public void updateUser(CustomerUpdateEntity customerUpdateEntity) {
        log.info("Update customer with id {} : {}", customerUpdateEntity.getUserId(), customerUpdateEntity);

        Optional<CustomerEntity> existingUserOpt = customerRepository.findById(customerUpdateEntity.getUserId());

        if (existingUserOpt.isPresent()) {
            CustomerEntity existingUser = getUserEntity(customerUpdateEntity, existingUserOpt);
            customerRepository.save(existingUser);
        } else {
            throw new CustomerNotFoundException("Customer not found with the following id: " + customerUpdateEntity.getUserId());
        }
    }

    private static CustomerEntity getUserEntity(CustomerUpdateEntity customerUpdateEntity, Optional<CustomerEntity> existingUserOpt) {
        CustomerEntity existingUser = existingUserOpt.get();

        if (customerUpdateEntity.getName() != null) {
            existingUser.setName(customerUpdateEntity.getName());
        }

        if (customerUpdateEntity.getLastname() != null) {
            existingUser.setLastname(customerUpdateEntity.getLastname());
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

        Optional<CustomerEntity> userOpt = customerRepository.findById(id);
        if(userOpt.isPresent()){
            return CustomerResponseEntity
                    .builder()
                    .email(userOpt.get().getEmail())
                    .lastname(userOpt.get().getLastname())
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
        if(customerRepository.findById(id).isPresent()){
            customerRepository.deleteById(id);
        }else{
            throw new CustomerNotFoundException("Customer not found with the following id: " + id);
        }
    }
}
