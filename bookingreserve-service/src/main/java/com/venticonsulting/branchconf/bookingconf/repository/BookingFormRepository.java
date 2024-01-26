package com.venticonsulting.branchconf.bookingconf.repository;

import com.venticonsulting.branchconf.bookingconf.entity.configuration.BookingForm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingFormRepository extends JpaRepository<BookingForm, Long> {

}
