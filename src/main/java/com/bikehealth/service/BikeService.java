
package com.bikehealth.service;

import com.bikehealth.entity.Bike;
import com.bikehealth.repository.BikeRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BikeService {

    private final BikeRepository bikeRepository;

    public BikeService(BikeRepository bikeRepository) {
        this.bikeRepository = bikeRepository;
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
                .orElseThrow(() -> new RuntimeException("Bike not found"));

        bike.setName(updatedBike.getName());
        bike.setBrand(updatedBike.getBrand());
        bike.setModel(updatedBike.getModel());
        bike.setManufacturingYear(updatedBike.getManufacturingYear());
        bike.setCurrentKm(updatedBike.getCurrentKm());
        bike.setPurchaseDate(updatedBike.getPurchaseDate());

        return bikeRepository.save(bike);
    }

    public void deleteBike(Long id) {
        bikeRepository.deleteById(id);
    }
}