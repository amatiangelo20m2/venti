package com.ventimetriconsulting.inventario.entity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ventimetriconsulting.inventario.entity.exrta.InventoryAction;
import com.ventimetriconsulting.supplier.entity.Product;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

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

    @Lob
    @Column(name = "inventory_action", columnDefinition = "TEXT")
    private String inventoryActionJson;

    // Convenience methods to convert between JSON string and list of InventoryAction objects
    public List<InventoryAction> getInventoryActions() {
        return InventoryAction.fromJsonString(inventoryActionJson);
    }

    public void setInventoryActions(List<InventoryAction> inventoryActions) {
        this.inventoryActionJson = InventoryAction.toJsonString(inventoryActions);
    }


    @Override
    public String toString() {
        return "Inventario{" +
                "inventarioId=" + inventarioId +
                ", storage=" + storage +
                ", product=" + product +
                ", insertionDate=" + insertionDate +
                ", deletionDate=" + deletionDate +
                ", inventoryActionJson='" + inventoryActionJson + '\'' +
                '}';
    }
}
