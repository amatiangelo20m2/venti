package com.venticonsulting.waapi.repository;

import com.venticonsulting.waapi.entity.WaApiConfigEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WaApiConfigRepository extends JpaRepository<WaApiConfigEntity, Long> {
}
