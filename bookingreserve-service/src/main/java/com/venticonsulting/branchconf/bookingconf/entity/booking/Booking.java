package com.venticonsulting.branchconf.bookingconf.entity.booking;

import com.venticonsulting.branchconf.bookingconf.entity.booking.dto.BookingStatus;
import jakarta.persistence.*;
import lombok.*;

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

    @PrePersist
    public void generateUniqueCode() {
        this.bookingcode = generateUniqueHexCode();
    }

    private String generateUniqueHexCode() {
        String uuid = UUID.randomUUID().toString().replace("-", "");
        return uuid.substring(0, 10).toUpperCase();
    }
}
