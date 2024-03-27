package com.ventimetriconsulting.inventario.entity;

import com.ventimetriconsulting.inventario.entity.exrta.InventoryAction;
import com.ventimetriconsulting.supplier.entity.Product;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.Set;

@Entity(name = "Inventario")
@Table(name = "inventario",
        uniqueConstraints=@UniqueConstraint(columnNames={"inventario_id"}))
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
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
    private LocalDate deletionDate;

    @ElementCollection
    @CollectionTable(
            name = "inventory_actions",
            joinColumns = @JoinColumn(name = "inventario_id")
    )
    @OrderColumn(name = "position")
    private Set<InventoryAction> inventoryActions;

    @Override
    public String toString() {
        return "Inventario{" +
                "inventarioId=" + inventarioId +
                ", product=" + product +
                ", insertionDate=" + insertionDate +
                ", deletionDate=" + deletionDate +
                ", inventoryActionJson='" + inventoryActions + '\'' +
                '}';
    }
}
