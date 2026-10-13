package com.bikehealth.repository;

import com.bikehealth.entity.Maintenance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface MaintenanceRepository extends JpaRepository<Maintenance, Long> {

    List<Maintenance> findByBikeId(Long bikeId);

    List<Maintenance> findByBikeIdOrderByServiceDateDesc(Long bikeId);

    @Modifying
    @Transactional
    void deleteByBikeId(Long bikeId);
}