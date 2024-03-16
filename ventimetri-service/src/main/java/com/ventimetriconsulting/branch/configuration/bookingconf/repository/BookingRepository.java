package com.ventimetriconsulting.branch.configuration.bookingconf.repository;

import com.ventimetriconsulting.branch.configuration.bookingconf.entity.booking.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("SELECT b.date, b.time, SUM(b.guest) " +
            "FROM Booking b " +
            "WHERE b.branchCode = :branchCode " +
            "AND b.date >= :today " +
            "GROUP BY b.date, b.time")
    List<Object[]> countGuestsByDateAndTime(@Param("branchCode")  String branchCode,
                                            @Param("today") LocalDate today);

    List<Booking> findByBranchCode(String branchCode);

    List<Booking> findByBranchCodeAndDateBetween(String branchCode, LocalDate startDate, LocalDate endDate);
}
