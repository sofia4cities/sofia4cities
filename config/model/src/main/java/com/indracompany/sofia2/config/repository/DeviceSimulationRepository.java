package com.indracompany.sofia2.config.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.indracompany.sofia2.config.model.DeviceSimulation;
import com.indracompany.sofia2.config.model.User;


public interface DeviceSimulationRepository extends JpaRepository<DeviceSimulation, String>{

	DeviceSimulation findByIdentification(String identification);

	DeviceSimulation findById(String id);

	List<DeviceSimulation> findByUser(User user);

}
