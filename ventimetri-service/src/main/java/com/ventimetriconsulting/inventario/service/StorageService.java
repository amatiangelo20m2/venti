package com.ventimetriconsulting.inventario.service;

import com.ventimetriconsulting.branch.entity.Branch;
import com.ventimetriconsulting.branch.exception.customexceptions.BranchNotFoundException;
import com.ventimetriconsulting.branch.repository.BranchRepository;
import com.ventimetriconsulting.inventario.entity.Inventario;
import com.ventimetriconsulting.inventario.entity.Storage;
import com.ventimetriconsulting.inventario.entity.dto.InventarioDTO;
import com.ventimetriconsulting.inventario.entity.dto.StorageDTO;
import com.ventimetriconsulting.inventario.entity.exrta.InventoryAction;
import com.ventimetriconsulting.inventario.repository.InventarioRepository;
import com.ventimetriconsulting.inventario.repository.StorageRepository;
import com.ventimetriconsulting.supplier.dto.ProductDTO;
import com.ventimetriconsulting.supplier.entity.Product;
import com.ventimetriconsulting.supplier.entity.Supplier;
import com.ventimetriconsulting.supplier.repository.SupplierRepository;
import jakarta.transaction.Transactional;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class StorageService {

    private final StorageRepository storageRepository;
    private final SupplierRepository supplierRepository;
    private final BranchRepository branchRepository;
    private final InventarioRepository inventarioRepository;

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
    public InventarioDTO insertProductToStorage(ProductDTO productDTO,
                                                long storageId,
                                                String userName) {

        log.info("Adding product {} to the storage with id {} - User ({})", productDTO, storageId, userName);
        Storage storage = storageRepository.findById(storageId)
                .orElseThrow(() -> new BranchNotFoundException("Storage not found with id: " + storageId + ". Cannot put the product"));

        Inventario inventario = Inventario
                .builder()
                .inventarioId(0)
                .product(ProductDTO.fromDTO(productDTO))
                .storage(storage)
                .deletionDate(null)
                .deletionDate(null)
                .inventoryActionJson(InventoryAction.toJsonString(new Date(),
                        0,
                        0,
                        userName))
                .build();

        Inventario inventarioSaved = inventarioRepository.save(inventario);
        storage.getInventario().add(inventarioSaved);

        return InventarioDTO.fromEntity(inventarioSaved);
    }

    @Transactional
    public List<InventarioDTO> insertSupplierProductsintoStorage(
                                                long supplierId,
                                                long storageId,
                                                String userName) {

        List<InventarioDTO> inventarioDTOS = new ArrayList<>();

        log.info("Adding products of the supplier with id {} to the storage with id {} - User ({})", supplierId, storageId, userName);
        Storage storage = storageRepository.findById(storageId)
                .orElseThrow(() -> new BranchNotFoundException("Storage not found with id: " + storageId + ". Cannot put the product"));

        Supplier supplier = supplierRepository.findById(supplierId)
                .orElseThrow(() -> new BranchNotFoundException("Supplier not found with id: " + supplierId + ". Cannot retrieve products to insert into storage"));;

        for(Product product : supplier.getProducts()){

            Inventario inventario = Inventario
                    .builder()
                    .inventarioId(0)
                    .product(product)
                    .storage(storage)
                    .insertionDate(LocalDate.now())
                    .deletionDate(null)
                    .inventoryActionJson(InventoryAction.toJsonString(new Date(),
                            0,
                            0 ,
                            userName))
                    .build();

            Inventario inventarioSaved = inventarioRepository.save(inventario);
            storage.getInventario().add(inventarioSaved);
            inventarioDTOS.add(InventarioDTO.fromEntity(inventarioSaved));
        }


        return inventarioDTOS;
    }

    @Transactional
    public InventarioDTO putDataIntoInventario(long inventarioId,
                                                     long insertedAmount,
                                                     long removedAmount,
                                                     String userName) {
        log.info("Insert data into inventario with id {}. Amount to add {}, amount to remove {}, user {}", inventarioId, insertedAmount, removedAmount, userName);

        Optional<Inventario> inventario = inventarioRepository.findById(inventarioId);

        inventario.ifPresent(value -> value.getInventoryActions().add(InventoryAction.builder()
                .removedAmount(removedAmount)
                .modifiedByUser(userName)
                .updateDate(new Date())
                .insertedAmount(insertedAmount)
                .build()));

        inventarioRepository.save(inventario.get());

        return InventarioDTO.fromEntity(inventario.get());
    }

    @Transactional
    public InventarioDTO removeProductFromStorage(long inventarioId) {


        Optional<Inventario> inventario = inventarioRepository.findById(inventarioId);
        if(inventario.isPresent()){

            log.info("Delete product from inventario. Inventario id {}, product {}, updating delete date to today", inventarioId, inventario.get().getProduct());
            inventario.get().setDeletionDate(LocalDate.now());
            return InventarioDTO.fromEntity(inventario.get());
        }
        return null;
    }
}
