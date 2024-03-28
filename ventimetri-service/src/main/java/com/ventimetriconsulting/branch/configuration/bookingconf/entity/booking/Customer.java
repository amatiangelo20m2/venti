package com.ventimetriconsulting.branch.configuration.bookingconf.entity.booking;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Entity(name = "Customer")
@Table(name = "CUSTOMER",
        uniqueConstraints=
        @UniqueConstraint(columnNames={"customer_id", "email", "phone"}))
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
            unique = true
    )
    private String email;

    private String prefix;

    @Column(
            name = "phone",
            unique = true,
            nullable = false
    )
    private String phone;

    @Column(length = 600)
    private String imageProfile;

    private boolean isNumberVerified;

    private LocalDate dob;
    private Date registrationDate;
    private boolean treatmentPersonalData;

    @JsonIgnore
    @OneToMany(mappedBy = "customer", fetch = FetchType.LAZY)
    private List<Booking> bookings;

    @Column(
            name = "branch_code",
            length = 10
    )
    private String branchCode;
}