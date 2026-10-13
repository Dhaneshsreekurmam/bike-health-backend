package com.bikehealth.controller;

import com.bikehealth.entity.Bike;
import com.bikehealth.service.BikeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bikes")
public class BikeController {

    private final BikeService bikeService;

    public BikeController(BikeService bikeService) {
        this.bikeService = bikeService;
    }

    @PostMapping
    public ResponseEntity<Bike> addBike(@RequestBody Bike bike) {
        return ResponseEntity.ok(bikeService.addBike(bike));
    }

    @GetMapping
    public ResponseEntity<List<Bike>> getAllBikes() {
        return ResponseEntity.ok(bikeService.getAllBikes());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Bike> getBikeById(@PathVariable Long id) {
        return bikeService.getBikeById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Bike> updateBike(@PathVariable Long id, @RequestBody Bike bike) {
        return ResponseEntity.ok(bikeService.updateBike(id, bike));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteBike(@PathVariable Long id) {
        bikeService.deleteBike(id);
        return ResponseEntity.ok(Map.of("message", "Bike and associated maintenance records deleted successfully"));
    }
}