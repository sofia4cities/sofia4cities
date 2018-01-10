package com.indracompany.sofia2.config.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.indracompany.sofia2.config.model.InstanceGenerator;

public interface InstanceGeneratorRepository extends JpaRepository<InstanceGenerator, Integer>{

	List<InstanceGenerator> findByIdentification(String identification);
	List<InstanceGenerator> findByUser(String user);
	InstanceGenerator findById(Integer id);
}
