package com.ventimetriconsulting.inventario.entity.dto;

import com.ventimetriconsulting.inventario.entity.Storage;
import com.ventimetriconsulting.supplier.dto.SupplierDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StorageDTO {

    private long storageId;
    private String name;
    private String address;
    private String city;
    private String cap;
    private Date creationTime;
    private Set<InventarioDTO> inventarioDTOS;

    public static StorageDTO fromEntity(Storage storage) {
        return StorageDTO.builder()
                .storageId(storage.getStorageId())
                .name(storage.getName())
                .address(storage.getAddress())
                .city(storage.getCity())
                .cap(storage.getCap())
                .creationTime(storage.getCreationTime())
                .inventarioDTOS(InventarioDTO.fromEntities(storage.getInventario()))
                .build();
    }

    public static Storage toEntity(StorageDTO storageDTO) {
        return Storage.builder()
                .storageId(storageDTO.getStorageId())
                .name(storageDTO.getName())
                .address(storageDTO.getAddress())
                .city(storageDTO.getCity())
                .cap(storageDTO.getCap())
                .creationTime(storageDTO.getCreationTime())
                .inventario(InventarioDTO.toEntities(storageDTO.getInventarioDTOS()))
                .build();
    }

    public static List<StorageDTO> toDTOList(Set<Storage> storages) {

        return storages.stream()
                .map(StorageDTO::fromEntity)
                .collect(Collectors.toList());
    }
}
