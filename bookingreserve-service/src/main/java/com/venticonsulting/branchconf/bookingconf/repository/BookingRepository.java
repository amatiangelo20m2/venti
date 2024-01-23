package com.venticonsulting.branchconf.bookingconf.repository;

import com.venticonsulting.branchconf.bookingconf.entity.booking.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByBranchCode(String branchCode);
    List<Booking> findByBranchCodeAndDate(String branchCode, LocalDate date);
    List<Booking> findByBranchCodeAndDateGreaterThanEqual(String branchCode, LocalDate currentDate);
    List<Booking> findByTimeRangeIdIn(List<String> timeRangeIds);

}
