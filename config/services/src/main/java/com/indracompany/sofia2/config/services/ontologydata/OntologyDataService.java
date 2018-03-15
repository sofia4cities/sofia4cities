package com.indracompany.sofia2.config.services.ontologydata;

import com.indracompany.sofia2.config.model.Ontology;

public interface OntologyDataService {

	public boolean hasOntologySchemaCompliance(final String data, final Ontology ontology);
}
