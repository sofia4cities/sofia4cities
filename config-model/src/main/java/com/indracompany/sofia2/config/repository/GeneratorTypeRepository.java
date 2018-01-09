/*******************************************************************************
 * © Indra Sistemas, S.A.
 * 2013 - 2018  SPAIN
 *
 * All rights reserved
 ******************************************************************************/
package com.indracompany.sofia2.config.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.indracompany.sofia2.config.model.GeneratorType;

public interface GeneratorTypeRepository extends JpaRepository<GeneratorType, Integer>{

	List<GeneratorType> findByIdentification(String identification);
	GeneratorType findById(Integer id);
}
