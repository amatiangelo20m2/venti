package com.ventimetriconsulting.supplier.dto;

import com.ventimetriconsulting.supplier.entity.Product;
import com.ventimetriconsulting.supplier.entity.UnitMeasure;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductDTO {

    private long productId;
    private String name;
    private String productCode;
    private UnitMeasure unitMeasure;
    private String description;
    private int vatApplied;
    private double price;
    private String category;
    private String sku;

    public static Product fromDTO(ProductDTO productDTO) {
        return Product.builder()
                .productId(0)
                .name(productDTO.getName())
                .unitMeasure(productDTO.getUnitMeasure())
                .vatApplied(productDTO.getVatApplied())
                .price(productDTO.getPrice())
                .productCode(productDTO.getProductCode())
                .description(productDTO.getDescription())
                .category(productDTO.getCategory())
                .sku(productDTO.getSku())
                .build();
    }

    public static ProductDTO toDTO(Product product) {
        return ProductDTO.builder()
                .productId(product.getProductId())
                .name(product.getName())
                .unitMeasure(product.getUnitMeasure())
                .vatApplied(product.getVatApplied())
                .price(product.getPrice())
                .productCode(product.getProductCode())
                .description(product.getDescription())
                .category(product.getCategory())
                .sku(product.getSku())
                .build();
    }

    public static List<Product> fromDTOList(List<ProductDTO> productDTOList) {
        return productDTOList.stream()
                    .map(ProductDTO::fromDTO)
                    .collect(Collectors.toList());
    }

    public static List<ProductDTO> toDTOList(List<Product> productList) {
        return productList.stream()
                .map(ProductDTO::toDTO)
                .collect(Collectors.toList());
    }
}
