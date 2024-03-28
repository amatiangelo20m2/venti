package com.ventimetriconsulting.supplier.repository;

import com.ventimetriconsulting.supplier.entity.Product;
import com.ventimetriconsulting.supplier.entity.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findBySupplier_SupplierId(Long supplierId);
    void deleteBySupplierAndProductId(Supplier supplier, Long productId);
    void deleteAllBySupplier(Supplier supplier);
}
