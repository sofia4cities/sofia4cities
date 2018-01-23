package com.indracompany.sofia2.service.ontology;


import java.util.List;

import com.indracompany.sofia2.config.model.Ontology;


public interface OntologyService {

	List<Ontology> findAllOntologies();
	List<Ontology> findOntolgiesWithDescriptionAndIdentification(String userId,String identification, String description);
}
