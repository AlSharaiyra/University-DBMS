package com.globitel.repos;

import com.globitel.entities.Place;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlaceRepo extends JpaRepository <Place, Integer> {
}
