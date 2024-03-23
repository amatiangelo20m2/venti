package com.ventimetriconsulting.inventario.entity;

import com.ventimetriconsulting.supplier.entity.Product;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

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


    @ManyToOne
    @JoinColumn(name = "storage_id")
    private Storage storage;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    private LocalDate insertionDate;
    private LocalDate updateDate;
    private LocalDate deletionDate;

    private int insertedAmount;
    private int removedAmount;
    private String modifiedByUser;
}
