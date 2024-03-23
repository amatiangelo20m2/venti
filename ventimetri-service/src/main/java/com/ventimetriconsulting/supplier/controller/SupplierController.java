package com.ventimetriconsulting.supplier.controller;

import com.ventimetriconsulting.supplier.dto.ProductDTO;
import com.ventimetriconsulting.supplier.dto.SupplierDTO;
import com.ventimetriconsulting.supplier.service.SupplierService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @PostMapping(path = "/addlist")
    public ResponseEntity<List<SupplierDTO>> insertSupplierList(
            @RequestBody List<SupplierDTO> supplierDTOList,
            @RequestParam("branchCode") String branchCode) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(supplierService.insertSupplierList(supplierDTOList, branchCode));
    }

    @PostMapping(path = "/product/add")
    public ResponseEntity<ProductDTO> insertProduct(
            @RequestBody ProductDTO productDTO, @RequestParam("supplierId") Long supplierId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(supplierService.createProduct(productDTO, supplierId));
    }

    @PostMapping(path = "/product/insertlist")
    public ResponseEntity<List<ProductDTO>> insertProductList(
            @RequestBody List<ProductDTO> productDTOList, @RequestParam("supplierId") Long supplierId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(supplierService.insertListProduct(productDTOList, supplierId));
    }


    @PutMapping(path = "/unlinkfrombranch")
    public ResponseEntity<Boolean> unlinkSupplierFromBranch(@RequestParam("supplierId") Long supplierId,
                                                            @RequestParam("branchId") Long branchId) {

        supplierService.unlinkSupplierFromBranch(supplierId, branchId);
        return ResponseEntity.status(HttpStatus.OK).body(true);
    }

    @PutMapping(path = "/associatetobranch")
    public ResponseEntity<SupplierDTO> associateSupplierToBranch(@RequestParam("supplierId") Long supplierId,
                                                                 @RequestParam("branchId") Long branchId) {

        SupplierDTO supplierDTO = supplierService.associateSupplierToBranch(supplierId, branchId);
        return ResponseEntity.status(HttpStatus.OK).body(supplierDTO);
    }

    @DeleteMapping(path = "/product/delete")
    public ResponseEntity<Boolean> deleteProductById(
            @RequestParam("productId") Long productId,
            @RequestParam("supplierId") Long supplierId) {

        supplierService.deleteProductById(productId, supplierId);

        return ResponseEntity.status(HttpStatus.OK).body(true);
    }

    @PutMapping(path = "/product/update")
    public ResponseEntity<ProductDTO> updateProduct(@RequestBody ProductDTO productDTO){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(supplierService.updateProduct(productDTO));

    }

//    @GetMapping(path = "/retrieve/bybranchcode")
//    public ResponseEntity<List<SupplierDTO>> retrieveByBranchCode(@RequestParam String branchCode) {
//        return ResponseEntity.status(HttpStatus.OK).body(supplierService.retrieveByBranchCode(branchCode));
//    }
}
