package com.indracompany.sofia2.config.services.ontology;

import static org.mockito.Mockito.*;

import java.util.ArrayList;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.indracompany.sofia2.config.model.Ontology;
import com.indracompany.sofia2.config.model.OntologyUserAccess;
import com.indracompany.sofia2.config.repository.OntologyRepository;
import com.indracompany.sofia2.config.repository.OntologyUserAccessRepository;

@RunWith(MockitoJUnitRunner.class)
public class OntologyServiceTest {

	@Mock
	private OntologyRepository ontologyRepository;
	@Mock
	private OntologyUserAccessRepository ontologyUserAccessRepository;
	
	@InjectMocks
	OntologyServiceImpl service;
	
	@Test
	public void testIsOntologyAuthorizedForOthersNull() {
		String id = "1";
		Ontology ontology = new Ontology();
		ontology.setId(id);
		when(ontologyRepository.findById(id)).thenReturn(ontology);
		when(ontologyUserAccessRepository.findByOntology(ontology)).thenReturn(null);
		assertFalse(service.isOntologyAuthorizedForOthers("1"));
	}
	
	@Test
	public void testIsOntologyAuthorizedForOthersEmptyList() {
		String id = "1";
		Ontology ontology = new Ontology();
		ontology.setId(id);
		when(ontologyRepository.findById(id)).thenReturn(ontology);
		when(ontologyUserAccessRepository.findByOntology(ontology)).thenReturn(new ArrayList<OntologyUserAccess>(1));
		assertFalse(service.isOntologyAuthorizedForOthers("1"));
	}
	
	@Test
	public void testIsOntologyAuthorizedForOthersList() {
		String id = "1";
		Ontology ontology = new Ontology();
		ontology.setId(id);
		OntologyUserAccess ontologyUserAccess = new OntologyUserAccess();
		ontologyUserAccess.setId("1");
		ArrayList<OntologyUserAccess> authorizies = new ArrayList<OntologyUserAccess>(1);
		authorizies.add(ontologyUserAccess);
		when(ontologyRepository.findById(id)).thenReturn(ontology);
		when(ontologyUserAccessRepository.findByOntology(ontology)).thenReturn(authorizies);
		assertTrue(service.isOntologyAuthorizedForOthers("1"));
	}
	
}
