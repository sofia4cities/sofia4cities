/**
 * Copyright Indra Sistemas, S.A.
 * 2013-2018 SPAIN
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *      http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.indracompany.sofia2.service.ontology;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.indracompany.sofia2.config.model.Ontology;
import com.indracompany.sofia2.config.model.User;
import com.indracompany.sofia2.config.repository.OntologyRepository;
import com.indracompany.sofia2.config.repository.UserRepository;


@Service
public class OntologyServiceImpl implements OntologyService{

	@Autowired
	private OntologyRepository ontologyRepository;
	@Autowired
	private UserRepository userRepository;
	
	public static final String ADMINISTRATOR="ROLE_ADMINISTRATOR";

	public List<Ontology> findAllOntologies()
	{
		List<Ontology> ontologies=this.ontologyRepository.findAll();

		return ontologies;
	}

	public List<Ontology> findOntolgiesWithDescriptionAndIdentification(String userId,String identification, String description)
	{
		List<Ontology> ontologies;
		User user= this.userRepository.findByUserId(userId);
		
		if(user.getRoleTypeId().getName().equals(this.ADMINISTRATOR))
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

				ontologies=this.ontologyRepository.findByUserIdAndIdentificationContainingAndDescriptionContaining(user,identification, description);

			}else if(description==null && identification!=null){

				ontologies=this.ontologyRepository.findByUserIdAndIdentificationContaining(user,identification);

			}else if(description!=null && identification==null){	

				ontologies=this.ontologyRepository.findByUserIdAndDescriptionContaining(user,description);

			}else{

				ontologies=this.ontologyRepository.findByUserId(user);
			}
		}
		return ontologies;
	}


}
