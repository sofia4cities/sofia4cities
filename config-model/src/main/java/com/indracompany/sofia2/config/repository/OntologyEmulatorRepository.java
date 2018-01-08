/*******************************************************************************
 * © Indra Sistemas, S.A.
 * 2013 - 2018  SPAIN
 *
 * All rights reserved
 ******************************************************************************/

package com.indracompany.sofia2.config.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

import com.indracompany.sofia2.config.model.OntologyEmulator;

public interface OntologyEmulatorRepository extends JpaRepository<OntologyEmulator, String>{
	
	List<OntologyEmulator> findByIdentification(String identification);
	List<OntologyEmulator> findByIdentificationAndUserId(String identification, String userId);
	List<OntologyEmulator> findByUserId(String userId);
	List<OntologyEmulator> findById(String id);
}
