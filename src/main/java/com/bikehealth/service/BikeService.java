package com.bikehealth.service;

import com.bikehealth.entity.Bike;
import com.bikehealth.repository.BikeRepository;
import com.bikehealth.repository.MaintenanceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class BikeService {

    private final BikeRepository bikeRepository;
    private final MaintenanceRepository maintenanceRepository;

    public BikeService(BikeRepository bikeRepository, MaintenanceRepository maintenanceRepository) {
        this.bikeRepository = bikeRepository;
        this.maintenanceRepository = maintenanceRepository;
    }

    public Bike addBike(Bike bike) {
        return bikeRepository.save(bike);
    }

    public List<Bike> getAllBikes() {
        return bikeRepository.findAll();
    }

    public Optional<Bike> getBikeById(Long id) {
        return bikeRepository.findById(id);
    }

    public Bike updateBike(Long id, Bike updatedBike) {
        Bike bike = bikeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bike not found with id: " + id));

        bike.setName(updatedBike.getName());
        bike.setBrand(updatedBike.getBrand());
        bike.setModel(updatedBike.getModel());
        bike.setManufacturingYear(updatedBike.getManufacturingYear());
        bike.setCurrentKm(updatedBike.getCurrentKm());
        bike.setPurchaseDate(updatedBike.getPurchaseDate());

        return bikeRepository.save(bike);
    }

    @Transactional
    public void deleteBike(Long id) {
        maintenanceRepository.deleteByBikeId(id);
        bikeRepository.deleteById(id);
    }
}