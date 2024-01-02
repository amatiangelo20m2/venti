package com.venticonsulting.waapi.repository;


import com.venticonsulting.waapi.entity.RestaurantConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RestaurantConfigurationRepository extends JpaRepository<RestaurantConfiguration, Long> {
    Optional<RestaurantConfiguration> findByBranchCode(String branchCode);
}
