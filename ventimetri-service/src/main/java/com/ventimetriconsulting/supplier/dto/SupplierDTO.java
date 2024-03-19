package com.ventimetriconsulting.supplier.dto;

import com.ventimetriconsulting.supplier.entity.Product;
import com.ventimetriconsulting.supplier.entity.Supplier;
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
public class SupplierDTO {

    private long supplierId;
    private String name;
    private String vatNumber;
    private String address;
    private String city;
    private String cap;
    private String phoneNumber;
    private String email;
    private String pec;
    private String cf;
    private String country;
    private long createdByUserId;
    private List<ProductDTO> productDTOList;


    public static Supplier fromDTO(SupplierDTO supplierDTO) {
        return Supplier.builder()
                .supplierId(0)
                .name(supplierDTO.getName())
                .vatNumber(supplierDTO.getVatNumber())
                .address(supplierDTO.getAddress())
                .city(supplierDTO.getCity())
                .cap(supplierDTO.getCap())
                .phoneNumber(supplierDTO.getPhoneNumber())
                .email(supplierDTO.getEmail())
                .pec(supplierDTO.getPec())
                .cf(supplierDTO.getCf())
                .country(supplierDTO.getCountry())
                .createdByUserId(supplierDTO.getCreatedByUserId())
                .build();
    }

    public static SupplierDTO fromEntity(Supplier supplier) {
        SupplierDTO dto = new SupplierDTO();
        dto.setSupplierId(supplier.getSupplierId());
        dto.setName(supplier.getName());
        dto.setVatNumber(supplier.getVatNumber());
        dto.setAddress(supplier.getAddress());
        dto.setCity(supplier.getCity());
        dto.setCap(supplier.getCap());
        dto.setPhoneNumber(supplier.getPhoneNumber());
        dto.setEmail(supplier.getEmail());
        dto.setPec(supplier.getPec());
        dto.setCf(supplier.getCf());
        dto.setCountry(supplier.getCountry());
        dto.setCreatedByUserId(supplier.getCreatedByUserId());
        dto.setProductDTOList(ProductDTO.toDTOList(supplier.getProducts()));
        return dto;
    }

    public static List<Supplier> fromDTOList(Set<SupplierDTO> supplierDTOS) {
        return supplierDTOS.stream()
                .map(SupplierDTO::fromDTO)
                .collect(Collectors.toList());
    }

    public static List<SupplierDTO> toDTOList(Set<Supplier> supplierSet) {
        return supplierSet.stream()
                .map(SupplierDTO::fromEntity)
                .collect(Collectors.toList());
    }

}
