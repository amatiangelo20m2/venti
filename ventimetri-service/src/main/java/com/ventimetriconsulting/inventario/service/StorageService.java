package com.ventimetriconsulting.inventario.service;

import com.ventimetriconsulting.branch.entity.Branch;
import com.ventimetriconsulting.branch.exception.customexceptions.BranchNotFoundException;
import com.ventimetriconsulting.branch.repository.BranchRepository;
import com.ventimetriconsulting.inventario.entity.Inventario;
import com.ventimetriconsulting.inventario.entity.Storage;
import com.ventimetriconsulting.inventario.entity.dto.InventarioDTO;
import com.ventimetriconsulting.inventario.entity.dto.StorageDTO;
import com.ventimetriconsulting.inventario.repository.StorageRepository;
import com.ventimetriconsulting.supplier.dto.ProductDTO;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class StorageService {

    private final StorageRepository storageRepository;

    private final BranchRepository branchRepository;

    @Transactional
    public StorageDTO createStorage(StorageDTO storageDTO,
                                    String branchCode) {

        log.info("Crete storage {}. Associate it with branch with code {}", storageDTO, branchCode);

        Storage storage = StorageDTO.toEntity(storageDTO);

        Branch branch = branchRepository.findByBranchCode(branchCode)
                .orElseThrow(() -> new BranchNotFoundException("Branch not found with code: " + branchCode + ". Cannot associate the storage" ));

        Storage savedStorage = storageRepository.save(storage);

        branch.getStorages().add(savedStorage);

        return StorageDTO.fromEntity(savedStorage);
    }

    @Transactional
    public InventarioDTO addProduct(ProductDTO productDTO,
                                    long storageId,
                                    String userName) {

        Storage storage = storageRepository.findById(storageId)
                .orElseThrow(() -> new BranchNotFoundException("Storage not found with id: " + storageId + ". Cannot put the product"));

        Inventario inventario = Inventario
                .builder()
                .inventarioId(0)
                .product(ProductDTO.fromDTO(productDTO))
                .storage(storage)
                .deletionDate(null)
                .insertedAmount(0)
                .removedAmount(0)
                .modifiedByUser(userName)
                .deletionDate(null)
                .updateDate(null)
                .build();

        storage.getInventario().add(inventario);

        return InventarioDTO.fromEntity(inventario);
    }
}
