package com.ventimetriconsulting.inventario.controller;

import com.ventimetriconsulting.inventario.entity.dto.InventarioDTO;
import com.ventimetriconsulting.inventario.entity.dto.StorageDTO;
import com.ventimetriconsulting.inventario.entity.dto.TransactionInventoryRequest;
import com.ventimetriconsulting.inventario.service.StorageService;
import com.ventimetriconsulting.supplier.dto.ProductDTO;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "api/storage/")
@AllArgsConstructor
public class StorageController {

    private StorageService storageService;

    @PostMapping(path = "/create")
    public ResponseEntity<StorageDTO> addStorage(
            @RequestBody StorageDTO supplierDTO,
            @RequestParam("branchCode") String branchCode) {

        return ResponseEntity.status(HttpStatus.OK)
                .body(storageService.createStorage(supplierDTO, branchCode));
    }

    @PostMapping(path = "/addproduct")
    public ResponseEntity<InventarioDTO> appProduct(
            @RequestBody ProductDTO productDTO,
            @RequestParam("storageId") long storageId,
            @RequestParam("userName") String userName) {

        return ResponseEntity.status(HttpStatus.OK)
                .body(storageService.insertProductToStorage(
                        productDTO,
                        storageId,
                        userName));
    }

    /**
     *
     * This method retrieve the supplier and all the associated products
     * and put in a storage. It give back the inventory list
     *
     * @param supplierId
     * @param storageId
     * @param userName
     * @return
     */
    @PostMapping(path = "/insertsupplierproducts")
    public ResponseEntity<List<InventarioDTO>> insertProductsFromSupplierList(
            @RequestParam("supplierId") long supplierId,
            @RequestParam("storageId") long storageId,
            @RequestParam("userName") String userName) {

        return ResponseEntity.status(HttpStatus.OK)
                .body(storageService.insertSupplierProductsintoStorage(
                        supplierId,
                        storageId,
                        userName));
    }

    @PutMapping(path = "/putdata")
    public ResponseEntity<InventarioDTO> putData(
            @RequestParam("inventarioId") long inventarioId,
            @RequestParam("insertedAmount") long insertedAmount,
            @RequestParam("deletedAmount") long deletedAmount,
            @RequestParam("userName") String userName) {

        return ResponseEntity.status(HttpStatus.OK)
                .body(storageService.putDataIntoInventario(
                        inventarioId,
                        insertedAmount,
                        deletedAmount,
                        userName));
    }

    @DeleteMapping(path = "/delete/product")
    public ResponseEntity<InventarioDTO> removeProductFromStorage(
            @RequestParam("inventarioId") long inventarioId){
        return ResponseEntity.status(HttpStatus.OK)
                .body(storageService.removeProductFromStorage(inventarioId));
    }

    @PutMapping(path = "/insert/inventariodata")
    public ResponseEntity<StorageDTO> insertDataIntoInventario(
            @RequestBody TransactionInventoryRequest transactionInventoryRequest){
        return ResponseEntity.status(HttpStatus.OK)
                .body(storageService.insertDataIntoInventario(transactionInventoryRequest));
    }
}
