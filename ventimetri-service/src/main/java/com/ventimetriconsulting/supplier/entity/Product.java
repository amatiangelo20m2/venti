package com.ventimetriconsulting.supplier.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.UUID;

@Entity(name = "Product")
@Table(name = "product",
        uniqueConstraints=
        @UniqueConstraint(columnNames={"product_id"}))
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
@ToString
public class Product {
    @Id
    @SequenceGenerator(
            name = "product_id",
            sequenceName = "product_id",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "product_id"
    )
    @Column(
            name = "product_id",
            updatable = false
    )
    private long productId;

    @Column(
            name = "product_code",
            nullable = false,
            unique = true,
            length = 20
    )
    private String productCode;
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "unitMeasure",
            nullable = false
    )
    private UnitMeasure unitMeasure;
    private int vatApplied;
    private double price;
    private String description;
    private String category;
    private String sku;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "supplier_id")
    @JsonIgnore
    private Supplier supplier;

    @PrePersist
    public void generateUniqueCode() {
        this.productCode = generateUniqueHexCode();
    }

    private String generateUniqueHexCode() {
        String uuid = UUID.randomUUID().toString().replace("-", "");
        return "PRO" + uuid.substring(0, 17).toUpperCase();
    }

}