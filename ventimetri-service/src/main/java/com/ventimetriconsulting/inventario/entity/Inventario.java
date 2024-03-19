package com.ventimetriconsulting.inventario.entity;

import com.ventimetriconsulting.supplier.entity.Product;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity(name = "Invantario")
@Table(name = "inventario",
        uniqueConstraints=@UniqueConstraint(columnNames={"inventario_id"}))
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
@ToString
public class Inventario {

    @Id
    @SequenceGenerator(
            name = "inventario_id",
            sequenceName = "inventario_id",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "inventario_id"
    )
    @Column(
            name = "inventario_id",
            updatable = false
    )
    private long inventarioId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "consumed_quantity", nullable = false)
    private int consumedQuantity;

    @Column(name = "date", nullable = false)
    private LocalDate date;
}
