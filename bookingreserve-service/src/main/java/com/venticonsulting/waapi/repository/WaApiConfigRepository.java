package com.venticonsulting.waapi.repository;

import com.venticonsulting.waapi.entity.WaApiConfigEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WaApiConfigRepository extends JpaRepository<WaApiConfigEntity, Long> {
    @Query("SELECT waConf FROM WaApiConfig waConf WHERE waConf.branchCode = ?1")
    Optional<WaApiConfigEntity> findAllByBranchCode(String branchCode);

    @Query("DELETE FROM WaApiConfig wa WHERE wa.branchCode = ?1")
    void deleteByBranchCode(String branchCode);
}
