
package com.bikehealth.controller;

import com.bikehealth.entity.Bike;
import com.bikehealth.service.BikeService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bikes")
@CrossOrigin(origins = "http://localhost:5173")
public class BikeController {

    private final BikeService bikeService;

    public BikeController(BikeService bikeService) {
        this.bikeService = bikeService;
    }

    @PostMapping
    public Bike addBike(@RequestBody Bike bike) {
        return bikeService.addBike(bike);
    }

    @GetMapping
    public List<Bike> getAllBikes() {
        return bikeService.getAllBikes();
    }

    @GetMapping("/{id}")
    public Bike getBikeById(@PathVariable Long id) {
        return bikeService.getBikeById(id)
                .orElseThrow(() ->
                        new RuntimeException("Bike not found with id: " + id));
    }

    @PutMapping("/{id}")
    public Bike updateBike(@PathVariable Long id, @RequestBody Bike bike) {
        return bikeService.updateBike(id, bike);
    }

    @DeleteMapping("/{id}")
    public String deleteBike(@PathVariable Long id) {
        bikeService.deleteBike(id);
        return "Bike deleted successfully";
    }
}