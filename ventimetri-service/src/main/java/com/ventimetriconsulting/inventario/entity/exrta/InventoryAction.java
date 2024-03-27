package com.ventimetriconsulting.inventario.entity.exrta;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Date;

@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InventoryAction {

    @Column(name = "insertion_date", nullable = false)
    private LocalDate insertionDate;

    @Column(name = "inserted_amount", nullable = false)
    private long amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type")
    private TransactionType transactionType;

    @Column(name = "modified_by_user", nullable = false)
    private String modifiedByUser;
}
