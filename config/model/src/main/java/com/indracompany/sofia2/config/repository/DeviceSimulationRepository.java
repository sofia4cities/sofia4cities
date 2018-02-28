package com.indracompany.sofia2.config.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.indracompany.sofia2.config.model.DeviceSimulation;


public interface DeviceSimulationRepository extends JpaRepository<DeviceSimulation, String>{

	DeviceSimulation findByIdentification(String identification);

}
