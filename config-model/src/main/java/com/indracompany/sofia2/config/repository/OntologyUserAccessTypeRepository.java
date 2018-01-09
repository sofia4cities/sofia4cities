package com.indracompany.sofia2.config.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.indracompany.sofia2.config.model.OntologyUserAccessType;

public interface OntologyUserAccessTypeRepository extends JpaRepository<OntologyUserAccessType, Integer>{
	
	List<OntologyUserAccessType> findById(Integer id);
	List<OntologyUserAccessType> findByName(String name);
	
	
	
}
