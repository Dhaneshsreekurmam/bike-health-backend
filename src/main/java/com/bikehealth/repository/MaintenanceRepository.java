
package com.bikehealth.repository;

import com.bikehealth.entity.Maintenance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MaintenanceRepository extends JpaRepository<Maintenance, Long> {

    List<Maintenance> findByBikeId(Long bikeId);
}