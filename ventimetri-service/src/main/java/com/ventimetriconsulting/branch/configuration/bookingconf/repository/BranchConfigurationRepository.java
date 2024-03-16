package com.ventimetriconsulting.branch.configuration.bookingconf.repository;

import com.ventimetriconsulting.branch.configuration.bookingconf.entity.BranchConfiguration;
import com.ventimetriconsulting.branch.entity.Branch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BranchConfigurationRepository extends JpaRepository<BranchConfiguration, Long> {
//    Optional<BranchConfiguration> findByBranchCode(String branchCode);

    @Query("SELECT conf.instanceId FROM BranchConfiguration conf WHERE conf.branch = ?1")
    String findInstanceCodeByBranchCode(Branch branch);
}
