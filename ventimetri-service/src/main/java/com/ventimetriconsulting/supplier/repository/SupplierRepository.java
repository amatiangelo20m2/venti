package com.ventimetriconsulting.supplier.repository;

import com.ventimetriconsulting.supplier.entity.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Long> {

    @Transactional
    @Modifying
    @Query("DELETE FROM Supplier s WHERE s.supplierId = :supplierId")
    void deleteBySupplierId(Long supplierId);

}
