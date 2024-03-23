package com.ventimetriconsulting.inventario.controller;

import com.ventimetriconsulting.inventario.entity.dto.InventarioDTO;
import com.ventimetriconsulting.inventario.entity.dto.StorageDTO;
import com.ventimetriconsulting.inventario.repository.StorageRepository;
import com.ventimetriconsulting.inventario.service.StorageService;
import com.ventimetriconsulting.supplier.dto.ProductDTO;
import com.ventimetriconsulting.supplier.dto.SupplierDTO;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
                .body(storageService.addProduct(
                        productDTO,
                        storageId,
                        userName));
    }





}
