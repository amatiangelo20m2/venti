package com.ventimetriconsulting.supplier.service;

import com.ventimetriconsulting.branch.entity.Branch;
import com.ventimetriconsulting.branch.exception.customexceptions.BranchNotFoundException;
import com.ventimetriconsulting.branch.exception.customexceptions.SupplierNotFoundException;
import com.ventimetriconsulting.branch.repository.BranchRepository;
import com.ventimetriconsulting.supplier.dto.ProductDTO;
import com.ventimetriconsulting.supplier.dto.SupplierDTO;
import com.ventimetriconsulting.supplier.entity.Product;
import com.ventimetriconsulting.supplier.entity.Supplier;
import com.ventimetriconsulting.supplier.repository.ProductRepository;
import com.ventimetriconsulting.supplier.repository.SupplierRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class SupplierService {

    private final ProductRepository productRepository;
    private final SupplierRepository supplierRepository;
    private final BranchRepository branchRepository;

    @Transactional
    public SupplierDTO createSupplier(SupplierDTO supplierDTO, String branchCode) {
        log.info("Crete supplier {}. Associate it with branch with code {}", supplierDTO, branchCode);

        Supplier supplier = SupplierDTO.fromDTO(supplierDTO);

        Branch branch = branchRepository.findByBranchCode(branchCode)
                .orElseThrow(() -> new BranchNotFoundException("Branch not found with code: " + branchCode + ". Cannot associate the supplier" ));

        Supplier save = supplierRepository.save(supplier);
        branch.getSuppliers().add(save);

        return SupplierDTO.fromEntity(save);
    }

    @Transactional
    public ProductDTO createProduct(ProductDTO productDTO,
                                    Long supplierId) {
        log.info("Saving product {} for supplier with id {}", productDTO, supplierId);

        Supplier supplier = supplierRepository
                .findById(supplierId).orElseThrow(() -> new SupplierNotFoundException("Supplier not found with code: " + supplierId + ". Cannot create any product"));


        Product product = ProductDTO.fromDTO(productDTO);
        supplier.getProducts().add(product);

        Product savedProduct = productRepository.save(product);

        return ProductDTO.toDTO(savedProduct);

    }

    @Transactional
    @Modifying
    public void unlinkSupplierFromBranch(Long supplierId, Long branchId) {
        log.info("Remove supplier with id {} from branch with id {}",
                supplierId,
                branchId);

        Branch branch = branchRepository
                .findById(branchId).orElseThrow(() -> new BranchNotFoundException("Branch not found with code: "
                        + branchId + ". Cannot unlink supplier with code " + supplierId));

        boolean removed = branch.getSuppliers().removeIf(supplier -> supplier.getSupplierId() == supplierId);
        if (removed) {
            branchRepository.save(branch);
        } else {
            throw new RuntimeException("Supplier not found in branch");
        }
    }

    @Transactional
    public void deleteProductById(Long productId) {
        log.info("Remove product by id{}", productId);
        productRepository.deleteById(productId);
    }
}
