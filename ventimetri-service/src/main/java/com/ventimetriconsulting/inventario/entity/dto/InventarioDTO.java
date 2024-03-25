package com.ventimetriconsulting.inventario.entity.dto;

import com.ventimetriconsulting.inventario.entity.Inventario;
import com.ventimetriconsulting.inventario.entity.exrta.InventoryAction;
import com.ventimetriconsulting.supplier.dto.ProductDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InventarioDTO {

    private long inventarioId;
    private LocalDate insertionDate;
    private LocalDate deletionDate;
    private ProductDTO productDTO;
    private Set<InventoryAction> inventoryAction;

    public static InventarioDTO fromEntity(Inventario inventario) {
        return InventarioDTO.builder()
                .insertionDate(inventario.getInsertionDate())
                .inventarioId(inventario.getInventarioId())
                .deletionDate(inventario.getDeletionDate())
                .inventoryAction(inventario.getInventoryActions())
                .productDTO(ProductDTO.toDTO(inventario.getProduct()))
                .build();
    }

    public static Set<InventarioDTO> fromEntities(Set<Inventario> inventarios) {
        Set<InventarioDTO> inventarioDTOs = new TreeSet<>((dto1, dto2) -> {
            String name1 = dto1.getProductDTO().getName();
            String name2 = dto2.getProductDTO().getName();
            return name1.compareTo(name2);
        });

        for (Inventario inventario : inventarios) {
            inventarioDTOs.add(fromEntity(inventario));
        }
        return inventarioDTOs;
    }

    public static Inventario toEntity(InventarioDTO inventarioDTO) {
        Inventario inventario = new Inventario();
        inventario.setInventarioId(inventarioDTO.getInventarioId());
        inventario.setInsertionDate(inventarioDTO.getInsertionDate());
        inventario.setDeletionDate(inventarioDTO.getDeletionDate());
        inventario.setInventoryActions(inventarioDTO.getInventoryAction());
        inventario.setProduct(ProductDTO.fromDTO(inventarioDTO.getProductDTO()));
        return inventario;
    }

    public static Set<Inventario> toEntities(Set<InventarioDTO> inventarioDTOs) {
        Set<Inventario> inventarios = new HashSet<>();
        for (InventarioDTO inventarioDTO : inventarioDTOs) {
            inventarios.add(toEntity(inventarioDTO));
        }
        return inventarios;
    }

}
