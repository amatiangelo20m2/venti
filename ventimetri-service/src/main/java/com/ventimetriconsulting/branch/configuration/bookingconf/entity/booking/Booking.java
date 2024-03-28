package com.ventimetriconsulting.branch.configuration.bookingconf.entity.booking;

import com.ventimetriconsulting.branch.configuration.bookingconf.entity.booking.dto.BookingStatus;
import com.ventimetriconsulting.branch.entity.Branch;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "booking",
        uniqueConstraints=
        @UniqueConstraint(columnNames={"booking_id"}))
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Booking {

    @Id
    @SequenceGenerator(
            name = "booking_id",
            sequenceName = "booking_id",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "booking_id"
    )
    @Column(
            name = "booking_id",
            updatable = false
    )
    private Long bookingId;

    @Column(
            name = "branch_code",
            length = 10
    )
    private String branchCode;

    @Column(
            name = "booking_code",
            length = 10
    )
    private String bookingcode;

    @Column(name = "booking_date")
    private LocalDate date;

    @Column(name = "booking_time")
    private LocalTime time;

    @Column
    private int guest;

    @Column
    private int child;

    @Column
    private int allowedDogs;

    @Column
    private String requests;

    @Column
    private String formCodeFrom;

    @Column(name = "insert_booking_time")
    private Date insertBookingTime;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Column(name = "booking_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private BookingStatus bookingStatus;

    @Column(name = "is_arrived", nullable = false)
    private boolean isArrived = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id", nullable = true) // Assuming a booking might not always have a branch assigned
    private Branch branch;

    @PrePersist
    public void generateUniqueCode() {
        this.bookingcode = generateUniqueHexCode();
    }

    private String generateUniqueHexCode() {
        String uuid = UUID.randomUUID().toString().replace("-", "");
        return uuid.substring(0, 10).toUpperCase();
    }
}
