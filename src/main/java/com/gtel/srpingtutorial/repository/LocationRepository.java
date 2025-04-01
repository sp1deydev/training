package com.gtel.srpingtutorial.repository;

import com.gtel.srpingtutorial.entity.LocationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LocationRepository extends JpaRepository<LocationEntity, String> {
}
