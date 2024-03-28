package com.ventimetriconsulting.inventario.entity;


import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity(name = "Storage")
@Table(name = "storage",
        uniqueConstraints=@UniqueConstraint(columnNames={"storage_id"}))
@AllArgsConstructor

@Setter
@Getter
@Builder
@ToString
public class Storage {

    @Id
    @SequenceGenerator(
            name = "storage_id",
            sequenceName = "storage_id",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "storage_id"
    )
    @Column(
            name = "storage_id",
            updatable = false
    )
    private long storageId;
    private String name;
    private String address;
    private String city;
    private String cap;
    private Date creationTime;

    @OneToMany(mappedBy = "storage")
    private Set<Inventario> inventario;

    public Storage() {
        this.inventario = new HashSet<>();
    }

    public Set<Inventario> getInventario() {
        if (this.inventario == null) {
            this.inventario = new HashSet<>();
        }
        return this.inventario;
    }
}
