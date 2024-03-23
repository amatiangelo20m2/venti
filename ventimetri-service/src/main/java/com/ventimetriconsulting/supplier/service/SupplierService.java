package com.ventimetriconsulting.supplier.service;

import com.ventimetriconsulting.branch.entity.Branch;
import com.ventimetriconsulting.branch.exception.customexceptions.BranchNotFoundException;
import com.ventimetriconsulting.branch.exception.customexceptions.ProductNotFoundException;
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
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class SupplierService {

    private final ProductRepository productRepository;
    private final SupplierRepository supplierRepository;
    private final BranchRepository branchRepository;

    @Transactional
    public SupplierDTO createSupplier(SupplierDTO supplierDTO,
                                      String branchCode) {
        log.info("Crete supplier {}. Associate it with branch with code {}", supplierDTO, branchCode);

        Supplier supplier = SupplierDTO.fromDTO(supplierDTO);

        Branch branch = branchRepository.findByBranchCode(branchCode)
                .orElseThrow(() -> new BranchNotFoundException("Branch not found with code: " + branchCode + ". Cannot associate the supplier" ));

        Supplier save = supplierRepository.save(supplier);
        branch.getSuppliers().add(save);

        return SupplierDTO.fromEntity(save);
    }

    @Transactional
    @Modifying
    public ProductDTO createProduct(ProductDTO productDTO,
                                    Long supplierId) {
        log.info("Saving product {} for supplier with id {}", productDTO, supplierId);

        Supplier supplier = supplierRepository
                .findById(supplierId).orElseThrow(() -> new SupplierNotFoundException("Supplier not found with code: " + supplierId + ". Cannot create any product"));


        Product product = ProductDTO.fromDTO(productDTO);
        product.setSupplier(supplier);
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
    @Modifying
    public void deleteProductById(Long productId,
                                  Long supplierId) {
        log.info("Remove product by id : {}", productId);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        Supplier supplier = supplierRepository.findById(supplierId).orElseThrow(() -> new SupplierNotFoundException("Supplier not found with code: "
                + supplierId + ". Cannot delete product with id " + productId));

        if (supplier != null) {
            supplier.getProducts().remove(product);
            supplierRepository.save(supplier);
        }

        productRepository.delete(product);
    }

    @Transactional
    @Modifying
    public SupplierDTO associateSupplierToBranch(Long supplierId,
                                                 Long branchId) {
        log.info("Associate supplier with id {} to branch with id {}",
                supplierId,
                branchId);

        Branch branch = branchRepository
                .findById(branchId).orElseThrow(() -> new BranchNotFoundException("Branch not found with code: "
                        + branchId + ". Cannot link associate with code " + supplierId));

        Supplier supplier = supplierRepository.findById(supplierId).orElseThrow(() -> new SupplierNotFoundException("Supplier not found with code: "
                + supplierId + ". Cannot associate supplier with code " + supplierId));

        branch.getSuppliers().add(supplier);

        log.info("Branch [{}] with code [{}] is been associated with supplier [{}] with code [{}]",
                branch.getBranchCode(),
                branch.getName(),
                supplier.getName(),
                supplier.getSupplierCode());

        return SupplierDTO.fromEntity(supplier);
    }

    @Transactional
    public ProductDTO updateProduct(ProductDTO productDTO) {

        log.info("Updating product {}", productDTO);

        Product product = productRepository.findById(productDTO.getProductId())
                .orElseThrow(() -> new ProductNotFoundException("No product found with code " + productDTO.getProductId()));

        product.setProductCode(productDTO.getProductCode());
        product.setName(productDTO.getName());
        product.setPrice(productDTO.getPrice());
        product.setCategory(productDTO.getCategory());
        product.setDescription(productDTO.getDescription());

        product.setUnitMeasure(productDTO.getUnitMeasure());
        product.setSku(productDTO.getSku());
        product.setVatApplied(productDTO.getVatApplied());

        return ProductDTO.toDTO(product);
    }

    @Transactional
    public List<ProductDTO> insertListProduct(List<ProductDTO> productDTOList,
                                        Long supplierId) {

        List<ProductDTO> productDTOS = new ArrayList<>();
        for(ProductDTO productDTO : productDTOList){
            productDTOS.add(createProduct(productDTO, supplierId));
        }
        return productDTOList;
    }

    @Transactional
    public List<SupplierDTO> insertSupplierList(List<SupplierDTO> supplierDTOList,
                                                String branchCode) {
        List<SupplierDTO> savedSuppliersDTO = new ArrayList<>();

        for (SupplierDTO supplierDTO : supplierDTOList){
            savedSuppliersDTO.add(createSupplier(supplierDTO, branchCode));
        }

        return savedSuppliersDTO;
    }

    public List<SupplierDTO> retrieveByBranchCode(String branchCode) {
        log.info("Retrieve suppliers associated with branch with code {}", branchCode);
        Branch branch = branchRepository.findByBranchCode(branchCode).orElseThrow(() -> new ProductNotFoundException("No branch found with code " + branchCode));

        return SupplierDTO.toDTOList(branch.getSuppliers());
    }
}
