package com.venticonsulting.customerservice.repository;

import com.venticonsulting.customerservice.entity.CustomerEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends MongoRepository<CustomerEntity, Long> {

    @Query("{ 'phone' : ?0 }")
    Optional<CustomerEntity> findByPhone(String phone);
}
