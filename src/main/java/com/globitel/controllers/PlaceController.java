package com.globitel.controllers;

import com.globitel.entities.Place;
import com.globitel.exceptions.ResourceNotFoundException;
import com.globitel.repos.PlaceRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;
import java.util.List;

@RestController
@RequestMapping("/api/v1/places")
public class PlaceController {
    @Autowired
    private PlaceRepo placeRepo;

    public record PlaceRecord(
            Integer place_id,
            String name,
            String type,
            Integer capacity
    ) {
    }

    // Get all places
    @GetMapping("")
    public List<PlaceRecord> getAllPlaces() {
        return placeRepo.findAll().stream()
                .map(place -> new PlaceRecord(
                        place.getID(),
                        place.getName(),
                        place.getType(),
                        place.getCapacity()))
                .collect(Collectors.toList());
    }

    // Get a single place given ID
    @GetMapping("/{id}")
    public PlaceRecord getOnePlace(@PathVariable Integer id) {
        return placeRepo.findById(id).map(place -> new PlaceRecord(
                        place.getID(),
                        place.getName(),
                        place.getType(),
                        place.getCapacity()))
                .orElseThrow(() -> new ResourceNotFoundException("Place not found with ID: " + id));
    }

    public record newPlace(
            String name,
            String type,
            Integer capacity
    ) {
    }

    @PostMapping("")
    public ResponseEntity<String> addPlace(@RequestBody newPlace request){
        Place place = new Place();

        place.setName(request.name);
        place.setType(request.type);
        place.setCapacity(request.capacity);

        placeRepo.save(place);
        return ResponseEntity.status(HttpStatus.CREATED).body("Place added successfully");
    }
}
