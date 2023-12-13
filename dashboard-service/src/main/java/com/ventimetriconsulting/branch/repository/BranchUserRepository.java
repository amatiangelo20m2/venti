package com.ventimetriconsulting.branch.repository;

import com.ventimetriconsulting.branch.entity.BranchUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BranchUserRepository  extends JpaRepository<BranchUser, Long> {

    @Query("SELECT bu FROM BranchUser bu WHERE bu.userCode = ?1")
    Optional<List<BranchUser>> findBranchesByUserCode(String userCode);
}
