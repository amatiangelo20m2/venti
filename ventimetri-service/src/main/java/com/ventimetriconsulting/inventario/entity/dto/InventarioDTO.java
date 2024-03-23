package com.ventimetriconsulting.inventario.entity.dto;

import com.ventimetriconsulting.inventario.entity.Inventario;
import com.ventimetriconsulting.supplier.dto.ProductDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InventarioDTO {

    private long inventarioId;

    private LocalDate insertionDate;
    private LocalDate updateDate;
    private LocalDate deletionDate;

    private int insertedAmount;
    private int removedAmount;
    private String modifiedByUser;

    private ProductDTO productDTO;

    public static InventarioDTO fromEntity(Inventario inventario) {
        return InventarioDTO.builder()
                .inventarioId(inventario.getInventarioId())
                .deletionDate(inventario.getDeletionDate())
                .updateDate(inventario.getUpdateDate())
                .insertedAmount(inventario.getInsertedAmount())
                .modifiedByUser(inventario.getModifiedByUser())
                .removedAmount(inventario.getRemovedAmount())
                .productDTO(ProductDTO.toDTO(inventario.getProduct()))
                .build();
    }
}
