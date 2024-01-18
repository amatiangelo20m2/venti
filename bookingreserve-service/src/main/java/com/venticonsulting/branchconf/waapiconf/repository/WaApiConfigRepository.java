package com.venticonsulting.branchconf.waapiconf.repository;

import com.venticonsulting.branchconf.waapiconf.entity.WaApiConfigEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WaApiConfigRepository extends JpaRepository<WaApiConfigEntity, Long> {
}
