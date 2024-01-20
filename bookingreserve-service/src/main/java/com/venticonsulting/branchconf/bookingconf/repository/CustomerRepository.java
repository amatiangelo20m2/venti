package com.venticonsulting.branchconf.bookingconf.repository;

import com.venticonsulting.branchconf.bookingconf.entity.booking.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> { }