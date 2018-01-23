package com.indracompany.sofia2.service.ontology;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.indracompany.sofia2.config.model.Ontology;
import com.indracompany.sofia2.config.repository.OntologyRepository;
import com.indracompany.sofia2.controlpanel.utils.AppWebUtils;

@Service
public class OntologyServiceImpl implements OntologyService{

	@Autowired
	private OntologyRepository ontologyRepository;

	@Autowired
	private AppWebUtils utils;

	public List<Ontology> findAllOntologies()
	{
		List<Ontology> ontologies=this.ontologyRepository.findAll();

		return ontologies;
	}

	public List<Ontology> findOntolgiesWithDescriptionAndIdentification(String userId,String identification, String description)
	{
		List<Ontology> ontologies;
		//If user is null, Is ROLE_ADMINISTRATOR
		if(userId==null)
		{
			if(description!=null && identification!=null){

				ontologies=this.ontologyRepository.findByIdentificationContainingAndDescriptionContaining(identification, description);

			}else if(description==null && identification!=null){

				ontologies=this.ontologyRepository.findByIdentificationContaining(identification);

			}else if(description!=null && identification==null){	

				ontologies=this.ontologyRepository.findByDescriptionContaining(description);

			}else{

				ontologies=this.ontologyRepository.findAll();
			}
		}else
		{
			if(description!=null && identification!=null){

				ontologies=this.ontologyRepository.findByUserIdAndIdentificationContainingAndDescriptionContaining(utils.getUserId(),identification, description);

			}else if(description==null && identification!=null){

				ontologies=this.ontologyRepository.findByUserIdAndIdentificationContaining(utils.getUserId(),identification);

			}else if(description!=null && identification==null){	

				ontologies=this.ontologyRepository.findByUserIdAndDescriptionContaining(utils.getUserId(),description);

			}else{

				ontologies=this.ontologyRepository.findByUserId(utils.getUserId());
			}
		}
		return ontologies;
	}


}
