package com.ventimetriconsulting.inventario.entity.dto;

import com.ventimetriconsulting.inventario.entity.Inventario;
import com.ventimetriconsulting.inventario.entity.exrta.InventoryAction;
import com.ventimetriconsulting.supplier.dto.ProductDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InventarioDTO {

    private long inventarioId;
    private LocalDate insertionDate;
    private LocalDate deletionDate;
    private ProductDTO productDTO;
    private List<InventoryAction> inventoryAction;

    public static InventarioDTO fromEntity(Inventario inventario) {
        return InventarioDTO.builder()
                .insertionDate(inventario.getInsertionDate())
                .inventarioId(inventario.getInventarioId())
                .deletionDate(inventario.getDeletionDate())
                .inventoryAction(inventario.getInventoryActions())
                .productDTO(ProductDTO.toDTO(inventario.getProduct()))
                .build();
    }
}
