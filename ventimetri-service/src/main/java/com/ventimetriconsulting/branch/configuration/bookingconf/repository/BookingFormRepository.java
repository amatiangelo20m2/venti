package com.ventimetriconsulting.branch.configuration.bookingconf.repository;

import com.ventimetriconsulting.branch.configuration.bookingconf.entity.BookingForm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingFormRepository extends JpaRepository<BookingForm, Long> {

}
