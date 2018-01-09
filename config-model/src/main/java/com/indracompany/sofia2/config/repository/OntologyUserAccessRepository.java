package com.indracompany.sofia2.config.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.indracompany.sofia2.config.model.Ontology;
import com.indracompany.sofia2.config.model.OntologyUserAccess;
import com.indracompany.sofia2.config.model.OntologyUserAccessType;

public interface OntologyUserAccessRepository extends JpaRepository<OntologyUserAccess, String>{
	
	List<OntologyUserAccess> findByOntologyIdAndUserId(Ontology ontologyId, String userId );
	List<OntologyUserAccess> findByUserId(String userId);
	List<OntologyUserAccess> findByUserIdAndOntologyUserAccessTypeId(String userId, OntologyUserAccessType ontologyUserAccessTypeId);
	OntologyUserAccess findById(String id);
	List<OntologyUserAccess> findByOntologyUserAccessTypeId(OntologyUserAccessType ontologyUserAccessTypeId);
	List<OntologyUserAccess> findByOntologyId(Ontology ontologyId);
	
}
