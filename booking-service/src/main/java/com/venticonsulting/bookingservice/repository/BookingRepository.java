package com.venticonsulting.bookingservice.repository;

import com.venticonsulting.bookingservice.entity.Customer;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookingRepository extends MongoRepository<Customer, Long> {

    @Query("{ 'phone' : ?0 }")
    Optional<Customer> findByPhone(String phone);
}
