package com.ventimetriconsulting.inventario.entity;


import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
    private List<Inventario> inventario;

    public Storage() {
        this.inventario = new ArrayList<>();
    }

    public List<Inventario> getInventario() {
        if (this.inventario == null) {
            this.inventario = new ArrayList<>();
        }
        return this.inventario;
    }
}
