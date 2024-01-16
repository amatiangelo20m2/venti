package com.venticonsulting.waapi.entity;

import jakarta.persistence.*;
import lombok.*;

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



    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;
}
