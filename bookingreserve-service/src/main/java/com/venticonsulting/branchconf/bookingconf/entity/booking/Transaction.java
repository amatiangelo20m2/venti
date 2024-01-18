package com.venticonsulting.branchconf.bookingconf.entity.booking;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity(name = "Transaction")
@Table(name = "TRANSACTION",
        uniqueConstraints=
        @UniqueConstraint(columnNames={"transaction_id"}))
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {

    @Id
    @SequenceGenerator(
            name = "transaction_id",
            sequenceName = "transaction_id",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "transaction_id"
    )
    @Column(
            name = "transaction_id",
            updatable = false
    )
    private long transactionId;
}
