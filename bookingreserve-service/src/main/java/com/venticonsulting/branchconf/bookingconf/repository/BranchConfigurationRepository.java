package com.venticonsulting.branchconf.bookingconf.repository;


import com.venticonsulting.branchconf.bookingconf.entity.configuration.BranchConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BranchConfigurationRepository extends JpaRepository<BranchConfiguration, Long> {
    Optional<BranchConfiguration> findByBranchCode(String branchCode);
}