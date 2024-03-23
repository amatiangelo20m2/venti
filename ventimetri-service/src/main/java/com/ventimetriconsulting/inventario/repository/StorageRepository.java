package com.ventimetriconsulting.inventario.repository;

import com.ventimetriconsulting.inventario.entity.Storage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StorageRepository extends JpaRepository<Storage, Long> {
}
