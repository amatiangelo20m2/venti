package com.ventimetriconsulting.supplier.controller;

import com.ventimetriconsulting.supplier.dto.ProductDTO;
import com.ventimetriconsulting.supplier.dto.SupplierDTO;
import com.ventimetriconsulting.supplier.service.SupplierService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/supplier")
@RequiredArgsConstructor
public class SupplierController {

    private final SupplierService supplierService;

    @PostMapping(path = "/add")
    public ResponseEntity<SupplierDTO> addSupplier(
            @RequestBody SupplierDTO supplierDTO, @RequestParam("branchCode") String branchCode) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(supplierService.createSupplier(supplierDTO, branchCode));
    }

    @PostMapping(path = "/addproduct")
    public ResponseEntity<ProductDTO> addProduct(
            @RequestBody ProductDTO productDTO, @RequestParam("supplierId") Long supplierId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(supplierService.createProduct(productDTO, supplierId));
    }


    @PutMapping(path = "/unlinksupplierfrombranch")
    public ResponseEntity<Boolean> unlinkSupplierFromBranch(@RequestParam("supplierId") Long supplierId,
                                                            @RequestParam("branchId") Long branchId) {

        supplierService.unlinkSupplierFromBranch(supplierId, branchId);
        return ResponseEntity.status(HttpStatus.OK).body(true);
    }

    @DeleteMapping(path = "/deleteproduct")
    public ResponseEntity<Boolean> deleteProductById(@RequestParam("productId") Long productId) {
        supplierService.deleteProductById(productId);
        return ResponseEntity.status(HttpStatus.OK).body(true);
    }
}
