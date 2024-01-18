package com.venticonsulting.waapi.entity.booking;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;

@Entity
@Table(name = "booking",
        uniqueConstraints=
        @UniqueConstraint(columnNames={"booking_id"}))
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@ToString
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
    private Long booking_id;

    @Column(
            name = "branch_code",
            unique = true,
            length = 10
    )
    private String branchCode;

    @Column(name = "booking_date")
    private LocalDate date;

    @Column(name = "booking_time")
    private LocalTime time;

    @Column(name = "insert_booking_time")
    private Date insertBookingTime;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;
}
