package com.ventimetriconsulting.branch.repository;

import com.ventimetriconsulting.branch.entity.Branch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BranchRepository extends JpaRepository<Branch, Long> {

    @Query("SELECT br FROM Branch br WHERE br.branchCode = ?1")
    Optional<Branch> findByBranchCode(String branchCode);
}
