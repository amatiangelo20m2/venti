package com.ventimetriconsulting.branch.entity;

import com.ventimetriconsulting.branch.configuration.bookingconf.entity.BranchConfiguration;
import com.ventimetriconsulting.branch.configuration.bookingconf.entity.booking.Booking;
import com.ventimetriconsulting.branch.entity.dto.BranchType;
import com.ventimetriconsulting.inventario.entity.Storage;
import com.ventimetriconsulting.supplier.entity.Supplier;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Cascade;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Entity(name = "Branch")
@Table(name = "BRANCH",
        uniqueConstraints=
        @UniqueConstraint(columnNames={"branch_id", "name", "branch_code"}))
@AllArgsConstructor
@Data
@Builder
@ToString
public class Branch {

    @Id
    @SequenceGenerator(
            name = "branch_id",
            sequenceName = "branch_id",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "branch_id"
    )
    @Column(
            name = "branch_id",
            updatable = false
    )
    private long branchId;

    @Column(
            name = "branch_code",
            unique = true,
            length = 10
    )
    private String branchCode;
    private String name;
    private String email;
    private String address;
    private String city;
    private String cap;

    @Column(
            name = "phone")
    private String phoneNumber;

    private String vat;

    @Enumerated
    private BranchType type;

    @Column(name = "logo_image", columnDefinition = "bytea")
    private byte[] logoImage;

    @OneToOne(mappedBy = "branch",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY,
            optional = true)
    private BranchConfiguration branchConfiguration;

    @OneToMany(mappedBy = "branch", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Booking> bookings;

    @ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinTable(
            name = "branch_supplier",
            joinColumns = @JoinColumn(name = "branch_id"),
            inverseJoinColumns = @JoinColumn(name = "supplier_id"))
    private Set<Supplier> suppliers;

    @ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinTable(
            name = "branch_storage",
            joinColumns = @JoinColumn(name = "branch_id"),
            inverseJoinColumns = @JoinColumn(name = "storage_id"))
    private Set<Storage> storages;

    @PrePersist
    public void generateUniqueCode() {
        this.branchCode = generateUniqueHexCode();
    }

    private String generateUniqueHexCode() {
        String uuid = UUID.randomUUID().toString().replace("-", "");
        return "B" + uuid.substring(0, 9).toUpperCase();
    }

    public Branch() {
        this.suppliers = new HashSet<>();
    }

    // Getter for suppliers
    public Set<Supplier> getSuppliers() {
        if (this.suppliers == null) {
            this.suppliers = new HashSet<>();
        }
        return this.suppliers;
    }

    public Set<Storage> getStorages() {
        if (this.storages == null) {
            this.storages = new HashSet<>();
        }
        return this.storages;
    }

}
