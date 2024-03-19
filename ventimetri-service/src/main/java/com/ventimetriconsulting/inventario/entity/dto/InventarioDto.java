package com.ventimetriconsulting.inventario.entity.dto;

import com.ventimetriconsulting.supplier.entity.UnitMeasure;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InventarioDto {
    private long inventarioId;
    private long productCode;
    private String productName;
    private UnitMeasure unitMeasure;
}
