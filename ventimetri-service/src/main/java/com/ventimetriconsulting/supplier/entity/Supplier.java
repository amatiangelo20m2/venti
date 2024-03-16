package com.ventimetriconsulting.supplier.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.*;

@Entity(name = "Supplier")
@Table(name = "SUPPLIER",
        uniqueConstraints=
        @UniqueConstraint(columnNames={"name", "supplier_code"}))
@AllArgsConstructor
@Data
@Builder
public class Supplier {

    @Id
    @SequenceGenerator(
            name = "supplier_id",
            sequenceName = "supplier_id",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "supplier_id"
    )
    @Column(
            name = "supplier_id",
            updatable = false
    )
    private long supplierId;
    @Column(
            name = "name",
            nullable = false
    )
    private String name;
    private String vatNumber;
    private String address;
    private String city;
    private String cap;
    @Column(
            name = "supplier_code",
            nullable = false,
            unique = true,
            length = 10
    )
    private String supplierCode;
    @Column(
            name = "phone",
            nullable = false
    )
    private String phoneNumber;
    private String email;
    private String pec;
    private String cf;
    private String country;

    private long createdByUserId;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "supplier", cascade = CascadeType.ALL)
    private List<Product> products;

    @PrePersist
    public void generateUniqueCode() {
        this.supplierCode = generateUniqueHexCode();
    }

    private String generateUniqueHexCode() {
        String uuid = UUID.randomUUID().toString().replace("-", "");
        return "S" + uuid.substring(0, 9).toUpperCase();
    }

    public Supplier(){
        this.products = new ArrayList<>();
    }

    public List<Product> getProducts() {
        if (this.products == null) {
            this.products = new ArrayList<>();
        }
        return this.products;
    }

}
