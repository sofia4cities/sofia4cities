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
package com.indracompany.sofia2.config.services.gadget;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.indracompany.sofia2.config.model.GadgetDatasource;
import com.indracompany.sofia2.config.model.User;
import com.indracompany.sofia2.config.repository.GadgetDatasourceRepository;
import com.indracompany.sofia2.config.repository.UserRepository;
import com.indracompany.sofia2.config.services.exceptions.GadgetDatasourceServiceException;
import com.indracompany.sofia2.config.services.exceptions.UserServiceException;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class GadgetDatasourceServiceImpl implements GadgetDatasourceService{

	@Autowired
	private GadgetDatasourceRepository gadgetDatasourceRepository;
	@Autowired
	private UserRepository userRepository;
	
	public static final String ADMINISTRATOR="ROLE_ADMINISTRATOR";
	
	@Override
	public List<GadgetDatasource> findAllDatasources()
	{
		List<GadgetDatasource> datasources=this.gadgetDatasourceRepository.findAll();

		return datasources;
	}
	

	@Override
	public List<GadgetDatasource> findGadgetDatasourceWithIdentificationAndDescription(String identification,String description, String userId)
	{
		List<GadgetDatasource> datasources;
		User user= this.userRepository.findByUserId(userId);
		
		if(user.getRole().getName().equals(GadgetServiceImpl.ADMINISTRATOR))
		{
			if(description!=null && identification!=null){

				datasources=this.gadgetDatasourceRepository.findByIdentificationContainingAndDescriptionContaining(identification, description);

			}else if(description==null && identification!=null){

				datasources=this.gadgetDatasourceRepository.findByIdentificationContaining(identification);

			}else if(description!=null && identification==null){	

				datasources=this.gadgetDatasourceRepository.findByDescriptionContaining(description);

			}else{

				datasources=this.gadgetDatasourceRepository.findAll();
			}
		}else
		{
			if(description!=null && identification!=null){

				datasources=this.gadgetDatasourceRepository.findByUserAndIdentificationContainingAndDescriptionContaining(user ,identification, description);

			}else if(description==null && identification!=null){

				datasources=this.gadgetDatasourceRepository.findByUserAndIdentificationContaining(user,identification);

			}else if(description!=null && identification==null){	

				datasources=this.gadgetDatasourceRepository.findByUserAndDescriptionContaining(user,description);

			}else{

				datasources=this.gadgetDatasourceRepository.findByUser(user);
			}
		}
		return datasources;
	}
	
	@Override
	public List<String> getAllIdentifications()
	{
		List<GadgetDatasource> datasources=this.gadgetDatasourceRepository.findAllByOrderByIdentificationAsc();
		List<String> names=new ArrayList<String>();
		for(GadgetDatasource datasource:datasources)
		{
			names.add(datasource.getIdentification());
			
		}
		return names;
	}
	
	@Override
	public GadgetDatasource getGadgetDatasourceById(String id) {
		return gadgetDatasourceRepository.findById(id);
	}


	@Override
	public void createGadgetDatasource(GadgetDatasource gadgetDatasource) {
		if (!this.gadgetDatasourceExists(gadgetDatasource)) {
			log.debug("Gadget datasource no exist, creating...");
			this.gadgetDatasourceRepository.save(gadgetDatasource);
		} else
			throw new UserServiceException("Gadget Datasource already exists in Database");
	}

	@Override
	public boolean gadgetDatasourceExists(GadgetDatasource gadgetDatasource) {
		if (this.gadgetDatasourceRepository.findById(gadgetDatasource.getId()) != null)
			return true;
		else
			return false;
	}

	@Override
	public void updateGadgetDatasource(GadgetDatasource gadgetDatasource) {
		if (this.gadgetDatasourceExists(gadgetDatasource)) {
			GadgetDatasource gadgetDatasourceDB = this.gadgetDatasourceRepository.findById(gadgetDatasource.getId());
			gadgetDatasourceDB.setConfig(gadgetDatasource.getConfig());
			gadgetDatasourceDB.setDbtype(gadgetDatasource.getDbtype());
			gadgetDatasourceDB.setDescription(gadgetDatasource.getDescription());
			gadgetDatasourceDB.setMaxvalues(gadgetDatasource.getMaxvalues());
			gadgetDatasourceDB.setMode(gadgetDatasource.getMode());
			gadgetDatasourceDB.setOntology(gadgetDatasource.getOntology());
			gadgetDatasourceDB.setQuery(gadgetDatasource.getQuery());
			gadgetDatasourceDB.setRefresh(gadgetDatasource.getRefresh());
			this.gadgetDatasourceRepository.save(gadgetDatasourceDB);
		} else
			throw new GadgetDatasourceServiceException("Cannot update GadgetDatasource that does not exist");
	}


	@Override
	public void deleteGadgetDatasource(String gadgetDatasourceId) {
		GadgetDatasource gadgetDatasource = this.gadgetDatasourceRepository.findById(gadgetDatasourceId);
		if (gadgetDatasource != null) {
			this.gadgetDatasourceRepository.delete(gadgetDatasource);
		} else
			throw new GadgetDatasourceServiceException("Cannot delete gadget datasource that does not exist");		
	}


	@Override
	public boolean hasUserPermission(String id, String userId) {
		User user = userRepository.findByUserId(userId);
		if(user.getRole().getName().equals("ROLE_ADMINISTRATOR")) {
			return true;
		}
		else {
			return gadgetDatasourceRepository.findById(id).getUser().getUserId().equals(userId);
		}
	}
	
	@Override
	public List<GadgetDatasource> getUserGadgetDatasources(String userId){
		User user = userRepository.findByUserId(userId);
		if(user.getRole().getId().equals("ROLE_ADMINISTRATOR")) {
			return gadgetDatasourceRepository.findAll();
		}
		else {
			return gadgetDatasourceRepository.findByUser(user);
		}
	}


	@Override
	public String getSampleQueryGadgetDatasourceById(String datasourceId) {
		String query = gadgetDatasourceRepository.findById(datasourceId).getQuery();
		return "select * from (" + query + ") AS Sample limit 1";
	}
	
	@Override
	public GadgetDatasource getDatasourceByIdentification(String dsIdentification) {
		return gadgetDatasourceRepository.findByIdentification(dsIdentification);
	}

}
	
	
	
 

