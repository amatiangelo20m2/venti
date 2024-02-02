package com.venticonsulting.branchconf.bookingconf.repository;

import com.venticonsulting.branchconf.bookingconf.entity.booking.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findByEmail(String email);

    Optional<Customer> findByPhone(String phone);

    Optional<Customer> findByPhoneOrEmail(String phone, String email);

    @Query("SELECT customer FROM Customer customer WHERE customer.prefix = ?1 AND customer.phone = ?2")
    Optional<Customer> findByPrefixAndPhone(String prefix, String phone);
}