package com.ventimetriconsulting.inventario.entity;


import jakarta.persistence.*;
import lombok.*;

@Entity(name = "Storage")
@Table(name = "storage",
        uniqueConstraints=@UniqueConstraint(columnNames={"storage_id"}))
@AllArgsConstructor
@NoArgsConstructor
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
    private String creationDate;
    private String address;
    private String city;
    private String cap;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inventario_id", referencedColumnName = "inventario_id")
    private Inventario inventario;


}
