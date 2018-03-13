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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.indracompany.sofia2.config.model.Gadget;
import com.indracompany.sofia2.config.model.GadgetDatasource;
import com.indracompany.sofia2.config.model.GadgetMeasure;
import com.indracompany.sofia2.config.model.User;
import com.indracompany.sofia2.config.repository.GadgetDatasourceRepository;
import com.indracompany.sofia2.config.repository.GadgetMeasureRepository;
import com.indracompany.sofia2.config.repository.GadgetRepository;
import com.indracompany.sofia2.config.repository.UserRepository;
import com.indracompany.sofia2.config.services.exceptions.GadgetDatasourceServiceException;

@Service
public class GadgetServiceImpl implements GadgetService {
	
	@Autowired
	private GadgetRepository gadgetRepository;
	
	@Autowired
	private GadgetMeasureRepository gadgetMeasureRepository;
	
	@Autowired
	private GadgetDatasourceService gadgetDatasourceService;
	
	@Autowired
	private UserRepository userRepository;
	
	public static final String ADMINISTRATOR="ROLE_ADMINISTRATOR";
	
	@Override
	public List<Gadget> findAllGadgets()
	{
		List<Gadget> gadgets=this.gadgetRepository.findAll();

		return gadgets;
	}
	
	@Override
	public List<Gadget> findGadgetWithIdentificationAndDescription(String identification,String description, String userId)
	{
		List<Gadget> gadgets;
		User user= this.userRepository.findByUserId(userId);
		
		if(user.getRole().getName().equals(GadgetServiceImpl.ADMINISTRATOR))
		{
			if(description!=null && identification!=null){

				gadgets=this.gadgetRepository.findByIdentificationContainingAndDescriptionContaining(identification, description);

			}else if(description==null && identification!=null){

				gadgets=this.gadgetRepository.findByIdentificationContaining(identification);

			}else if(description!=null && identification==null){	

				gadgets=this.gadgetRepository.findByDescriptionContaining(description);

			}else{

				gadgets=this.gadgetRepository.findAll();
			}
		}else
		{
			if(description!=null && identification!=null){

				gadgets=this.gadgetRepository.findByUserAndIdentificationContainingAndDescriptionContaining(user ,identification, description);

			}else if(description==null && identification!=null){

				gadgets=this.gadgetRepository.findByUserAndIdentificationContaining(user,identification);

			}else if(description!=null && identification==null){	

				gadgets=this.gadgetRepository.findByUserAndDescriptionContaining(user,description);

			}else{

				gadgets=this.gadgetRepository.findByUser(user);
			}
		}
		return gadgets;
	}
	
	@Override
	public List<String> getAllIdentifications()
	{
		List<Gadget> gadgets=this.gadgetRepository.findAllByOrderByIdentificationAsc();
		List<String> names=new ArrayList<String>();
		for(Gadget gadget:gadgets)
		{
			names.add(gadget.getIdentification());
			
		}
		return names;
	}
	
	@Override
	public Gadget getGadgetById(String userID, String gadgetId) {
		User user = userRepository.findByUserId(userID);
		Gadget gadget = gadgetRepository.findById(gadgetId);
		if(user.getRole().getId().equals("ROLE_ADMINISTRATOR") || gadget.getUser().getUserId().equals(userID)) {
			return gadget;
		}
		return null;
	}
	
	@Override
	public void createGadget(Gadget gadget) {
		//Compruebo que no exista ninguno con ese nombre
		if(this.gadgetRepository.findByIdentification(gadget.getIdentification()) == null) {
			this.gadgetRepository.save(gadget);
		}
		
	}

	@Override
	public List<Gadget> getUserGadgetsByType(String userID, String type) {
		User user = userRepository.findByUserId(userID);
		if(user.getRole().getId().equals("ROLE_ADMINISTRATOR")) {
			return gadgetRepository.findByType(type);
		}
		else {
			return gadgetRepository.findByUserAndType(user, type);
		}
	}

	@Override
	public List<GadgetMeasure> getGadgetMeasuresByGadgetId(String userID, String gadgetId) {
		User user = userRepository.findByUserId(userID);
		Gadget gadget = gadgetRepository.findById(gadgetId);
		List<GadgetMeasure> lgm = gadgetMeasureRepository.findByGadget(gadgetRepository.findById(gadgetId));
		if(user.getRole().getId().equals("ROLE_ADMINISTRATOR") || gadget.getUser().getUserId().equals(userID)) {
			return lgm;
		}
		return null;
	}

	@Override
	public boolean hasUserPermission(String id, String userId) {
		User user = userRepository.findByUserId(userId);
		if(user.getRole().getName().equals("ROLE_ADMINISTRATOR")) {
			return true;
		}
		else {
			return gadgetRepository.findById(id).getUser().getUserId().equals(userId);
		}
	}
	
	@Override
	public void deleteGadget(String gadgetId, String userId) {
		if(hasUserPermission(gadgetId, userId)) {
			Gadget gadget = this.gadgetRepository.findById(gadgetId);
			if (gadget != null) {
				List<GadgetMeasure> lgmeasure = gadgetMeasureRepository.findByGadget(gadget);
				for(GadgetMeasure gm : lgmeasure) {
					this.gadgetMeasureRepository.delete(gm);
				}
				this.gadgetRepository.delete(gadget);
			} else
				throw new GadgetDatasourceServiceException("Cannot delete gadget that does not exist");
		}
				
	}

	@Override
	public void updateGadget(Gadget gadget, String gadgetDatasourceIds, String jsonMeasures) {
		List<GadgetMeasure> lgmeasure = gadgetMeasureRepository.findByGadget(gadget);
		for(GadgetMeasure gm : lgmeasure) {
			this.gadgetMeasureRepository.delete(gm);
		}
		Gadget gadgetDB = this.gadgetRepository.findById(gadget.getId());
		gadget.setId(gadgetDB.getId());
		gadget.setIdentification(gadgetDB.getIdentification());
		gadget.setUser(gadgetDB.getUser());
		saveGadgetAndMeasures(gadget, gadgetDatasourceIds, jsonMeasures);
	}

	@Override
	public void createGadget(Gadget gadget, String gadgetDatasourceIds, String jsonMeasures) {
		saveGadgetAndMeasures(gadget, gadgetDatasourceIds, jsonMeasures);
	}
	
	private List<MeasureDto> fromJSONMeasuresStringToListString(String inputStr){
		ObjectMapper objectMapper = new ObjectMapper();
		TypeFactory typeFactory = objectMapper.getTypeFactory();
		List<MeasureDto> listStr = null;
		try {
			listStr = objectMapper.readValue(inputStr, typeFactory.constructCollectionType(List.class, MeasureDto.class));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return listStr;
	}
	
	private List<String> fromStringToListString(String inputStr){
		ObjectMapper objectMapper = new ObjectMapper();
		TypeFactory typeFactory = objectMapper.getTypeFactory();
		List<String> listStr = null;
		try {
			listStr = objectMapper.readValue(inputStr, typeFactory.constructCollectionType(List.class, String.class));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return listStr;
	}
 	
	private void saveGadgetAndMeasures(Gadget g, String gadgetDatasourceIds, String jsonMeasures) {
		g = gadgetRepository.save(g);
		List<MeasureDto> listJsonMeasures = fromJSONMeasuresStringToListString(jsonMeasures);
		List<String> listDatasources = fromStringToListString(gadgetDatasourceIds);
		for (int i=0; i< listJsonMeasures.size(); i++) {
			GadgetMeasure gadgetMeasure = new GadgetMeasure();
			gadgetMeasure.setGadget(g);
			gadgetMeasure.setDatasource(gadgetDatasourceService.getGadgetDatasourceById(listDatasources.get(i)));
			gadgetMeasure.setConfig(listJsonMeasures.get(i).getConfig());
			gadgetMeasureRepository.save(gadgetMeasure);
		}
	}
}


