
package com.bikehealth.service;

import com.bikehealth.entity.Maintenance;
import com.bikehealth.repository.MaintenanceRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MaintenanceService {

    private final MaintenanceRepository maintenanceRepository;

    public MaintenanceService(MaintenanceRepository maintenanceRepository) {
        this.maintenanceRepository = maintenanceRepository;
    }

    public Maintenance addMaintenance(Maintenance maintenance) {
        return maintenanceRepository.save(maintenance);
    }

    public List<Maintenance> getAllMaintenance() {
        return maintenanceRepository.findAll();
    }

    public List<Maintenance> getMaintenanceByBikeId(Long bikeId) {
        return maintenanceRepository.findByBikeId(bikeId);
    }

    public Optional<Maintenance> getMaintenanceById(Long id) {
        return maintenanceRepository.findById(id);
    }

    public Maintenance updateMaintenance(Long id, Maintenance updatedMaintenance) {
        Maintenance maintenance = maintenanceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Maintenance record not found"));

        maintenance.setBikeId(updatedMaintenance.getBikeId());
        maintenance.setTaskName(updatedMaintenance.getTaskName());
        maintenance.setDescription(updatedMaintenance.getDescription());
        maintenance.setServiceDate(updatedMaintenance.getServiceDate());
        maintenance.setNextDueDate(updatedMaintenance.getNextDueDate());
        maintenance.setCost(updatedMaintenance.getCost());
        maintenance.setStatus(updatedMaintenance.getStatus());

        return maintenanceRepository.save(maintenance);
    }

    public void deleteMaintenance(Long id) {
        maintenanceRepository.deleteById(id);
    }
}