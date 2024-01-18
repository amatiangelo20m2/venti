package com.venticonsulting.branchconf.bookingconf.entity.booking;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

@Entity(name = "Customer")
@Table(name = "CUSTOMER",
        uniqueConstraints=
        @UniqueConstraint(columnNames={"customer_id", "email"}))
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Customer implements Serializable {


    @Id
    @SequenceGenerator(
            name = "customer_id",
            sequenceName = "customer_id",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "customer_id"
    )
    @Column(
            name = "customer_id",
            updatable = false
    )
    private long customerId;
    private String name;
    private String lastname;
    @Column(
            name = "email",
            unique = true,
            nullable = false
    )
    private String email;

    @Column(
            name = "phone",
            unique = true,
            nullable = false
    )
    private String phone;
    private LocalDate dob;
    private String registrationDate;
    private boolean treatmentPersonalData;

    @JsonIgnore
    @OneToMany(mappedBy = "customer", fetch = FetchType.LAZY)
    private List<Booking> bookings;

}