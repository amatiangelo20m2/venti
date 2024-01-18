package com.venticonsulting.waapi.repository;

import com.venticonsulting.waapi.entity.configuration.WaApiConfigEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WaApiConfigRepository extends JpaRepository<WaApiConfigEntity, Long> {
}
