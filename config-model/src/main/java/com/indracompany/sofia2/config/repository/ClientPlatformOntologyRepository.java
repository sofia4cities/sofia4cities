/*******************************************************************************
 * © Indra Sistemas, S.A.
 * 2013 - 2018  SPAIN
 *
 * All rights reserved
 ******************************************************************************/
package com.indracompany.sofia2.config.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

import com.indracompany.sofia2.config.model.ClientPlatform;
import com.indracompany.sofia2.config.model.ClientPlatformOntology;
import com.indracompany.sofia2.config.model.Ontology;

public interface ClientPlatformOntologyRepository extends JpaRepository<ClientPlatformOntology, String>{

	List<ClientPlatformOntology> findByOntologyIdAndClientPlatformId(Ontology ontologyId,ClientPlatform clientPlatformId);
	List<ClientPlatformOntology> findByClientPlatformId(ClientPlatform clientPlatformId);
	List<ClientPlatformOntology> findById(String id);
	List<ClientPlatformOntology> findByOntologyId(Ontology ontologyId);

}
