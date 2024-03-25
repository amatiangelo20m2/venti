package com.ventimetriconsulting.inventario.entity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ventimetriconsulting.branch.configuration.bookingconf.entity.TimeRange;
import com.ventimetriconsulting.inventario.entity.exrta.InventoryAction;
import com.ventimetriconsulting.supplier.entity.Product;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
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

//    @Lob
//    @Column(name = "inventory_action", columnDefinition = "TEXT")
//    private String inventoryActionJson;
//
//    // Convenience methods to convert between JSON string and list of InventoryAction objects
//    public List<InventoryAction> getInventoryActions() {
//        return InventoryAction.fromJsonString(inventoryActionJson);
//    }
//
//    public void setInventoryActions(List<InventoryAction> inventoryActions) {
//        this.inventoryActionJson = InventoryAction.toJsonString(inventoryActions);
//    }


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
