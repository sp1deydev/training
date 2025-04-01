package com.gtel.srpingtutorial.repository;

import com.gtel.srpingtutorial.entity.AirportEntity;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

public interface CustomAirportRepository {

    List<AirportEntity> getCustomSearch(Map<String, String> params);
}
