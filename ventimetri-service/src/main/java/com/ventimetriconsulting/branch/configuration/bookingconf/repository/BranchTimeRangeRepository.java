package com.ventimetriconsulting.branch.configuration.bookingconf.repository;

import com.ventimetriconsulting.branch.configuration.bookingconf.entity.BranchTimeRange;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BranchTimeRangeRepository extends JpaRepository<BranchTimeRange, Long> {
    @Query("SELECT branchTimeRange FROM BranchTimeRange branchTimeRange where branchTimeRange.branchTimeRangeId IN ?1")
    Optional<List<BranchTimeRange>> findByBranchTimeRangeId(List<Long> restaurantConfIds);
}
