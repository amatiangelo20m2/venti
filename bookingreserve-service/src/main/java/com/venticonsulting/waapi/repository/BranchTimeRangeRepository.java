package com.venticonsulting.waapi.repository;

import com.venticonsulting.waapi.entity.BranchTimeRange;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BranchTimeRangeRepository extends JpaRepository<BranchTimeRange, Long> {
}
