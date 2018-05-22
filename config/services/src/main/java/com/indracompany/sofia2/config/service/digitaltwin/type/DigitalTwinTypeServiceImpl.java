/**
 * Copyright Indra Sistemas, S.A.
 * 2013-2018 SPAIN
 *
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
package com.indracompany.sofia2.config.service.digitaltwin.type;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import com.indracompany.sofia2.config.model.ActionsDigitalTwinType;
import com.indracompany.sofia2.config.model.DigitalTwinType;
import com.indracompany.sofia2.config.model.EventsDigitalTwinType;
import com.indracompany.sofia2.config.model.EventsDigitalTwinType.Type;
import com.indracompany.sofia2.config.model.LogicDigitalTwinType;
import com.indracompany.sofia2.config.model.PropertyDigitalTwinType;
import com.indracompany.sofia2.config.model.Role;
import com.indracompany.sofia2.config.model.User;
import com.indracompany.sofia2.config.repository.ActionsDigitalTwinTypeRepository;
import com.indracompany.sofia2.config.repository.DigitalTwinTypeRepository;
import com.indracompany.sofia2.config.repository.EventsDigitalTwinTypeRepository;
import com.indracompany.sofia2.config.repository.LogicDigitalTwinTypeRepository;
import com.indracompany.sofia2.config.repository.PropertyDigitalTwinTypeRepository;
import com.indracompany.sofia2.config.services.exceptions.DigitalTwinServiceException;
import com.indracompany.sofia2.config.services.user.UserService;

import lombok.extern.slf4j.Slf4j;


@Service
@Slf4j
public class DigitalTwinTypeServiceImpl implements DigitalTwinTypeService{
	
	@Autowired
	DigitalTwinTypeRepository digitalTwinTypeRepo;
	
	@Autowired
	PropertyDigitalTwinTypeRepository propDigitalTwinTypeRepo;
	
	@Autowired
	ActionsDigitalTwinTypeRepository actDigitalTwinTypeRepo;
	
	@Autowired
	EventsDigitalTwinTypeRepository evtDigitalTwinTypeRepo;
	
	@Autowired
	LogicDigitalTwinTypeRepository logicDigitalTwinTypeRepo;

	@Autowired
	private UserService userService;
	
	@Override
	public DigitalTwinType getDigitalTwinTypeById(String id) {
		return digitalTwinTypeRepo.findById(id);
	}
	
	@Override
	public List<PropertyDigitalTwinTypeDTO> getPropertiesByDigitalId(String TypeId) {
		
		List<PropertyDigitalTwinTypeDTO> lPropertiesDTO = new ArrayList<PropertyDigitalTwinTypeDTO>();
		List<PropertyDigitalTwinType> lProperties = propDigitalTwinTypeRepo.findByTypeId(digitalTwinTypeRepo.findById(TypeId));
		
		for(PropertyDigitalTwinType prop : lProperties) {
			lPropertiesDTO.add(new PropertyDigitalTwinTypeDTO(prop.getId(), prop.getType(), prop.getName(), prop.getUnit(), prop.getDirection(), prop.getDescription()));
		}
		
		return lPropertiesDTO;
	}
	
	@Override
	public List<ActionsDigitalTwinTypeDTO> getActionsByDigitalId(String TypeId) {
		
		List<ActionsDigitalTwinTypeDTO> lActionsDTO = new ArrayList<ActionsDigitalTwinTypeDTO>();
		List<ActionsDigitalTwinType> lActions = actDigitalTwinTypeRepo.findByTypeId(digitalTwinTypeRepo.findById(TypeId));
		
		for(ActionsDigitalTwinType act : lActions) {
			lActionsDTO.add(new ActionsDigitalTwinTypeDTO(act.getId(), act.getName(), act.getDescription()));
		}
		
		return lActionsDTO;
	}
	
	@Override
	public List<EventsDigitalTwinTypeDTO> getEventsByDigitalId(String TypeId) {
		
		List<EventsDigitalTwinTypeDTO> lEventsDTO = new ArrayList<EventsDigitalTwinTypeDTO>();
		List<EventsDigitalTwinType> lEvents = evtDigitalTwinTypeRepo.findByTypeId(digitalTwinTypeRepo.findById(TypeId));
		
		for(EventsDigitalTwinType event : lEvents) {
			if(!event.getType().equalsIgnoreCase(EventsDigitalTwinType.Type.PING.name()) && !event.getType().equalsIgnoreCase(EventsDigitalTwinType.Type.REGISTER.name())) {
				lEventsDTO.add(new EventsDigitalTwinTypeDTO(event.getId(), event.getType(), event.getName(), event.isStatus(), event.getDescription()));
			}
		}
		
		return lEventsDTO;
	}
	
	@Override
	public String getLogicByDigitalId(String TypeId) {
		LogicDigitalTwinType logic = logicDigitalTwinTypeRepo.findByTypeId(digitalTwinTypeRepo.findById(TypeId));
		if(logic!=null) {
			return logic.getLogic().replace("\\r", "");
		}
		return "";
	}
	
	@Override
	public List<String> getAllIdentifications() {
		List<DigitalTwinType> digitalTypes = this.digitalTwinTypeRepo.findAllByOrderByNameAsc();
		List<String> identifications = new ArrayList<String>();
		for (DigitalTwinType type : digitalTypes) {
			identifications.add(type.getName());
		}
		return identifications;
	}
	
	@Override
	public List<DigitalTwinType> getAll() {
		return digitalTwinTypeRepo.findAll();
	}

	@Override
	public void createDigitalTwinType(DigitalTwinType digitalTwinType, HttpServletRequest httpServletRequest) {
		try {
			log.error("1- entro en createDigitalTwinType");
			String[] properties = httpServletRequest.getParameterValues("propiedades");
			log.error("2- entro en createDigitalTwinType");
			String[] actions = httpServletRequest.getParameterValues("acciones");
			log.error("3- entro en createDigitalTwinType");
			String[] events = httpServletRequest.getParameterValues("eventos");
			log.error("4- entro en createDigitalTwinType");
			String logic = httpServletRequest.getParameter("logic");
			log.error("5- entro en createDigitalTwinType");
			
			Set<PropertyDigitalTwinType> propertyDigitalTwinTypes = new HashSet<>();
			Set<ActionsDigitalTwinType> actionDigitalTwinTypes = new HashSet<>();
			Set<EventsDigitalTwinType> eventDigitalTwinTypes = new HashSet<>();
			Set<LogicDigitalTwinType> logicDigitalTwinTypes = new HashSet<>();
			
			JSONObject json;
			
			log.error("6- obteniendo user");
			User user = userService.getUser(digitalTwinType.getUser().getUserId());
			log.error("7- obteniendo user");
			if (user != null) {
				digitalTwinType.setUser(user);
				
				log.error("8- PING");
				//Add PING and REGISTER events by default
				EventsDigitalTwinType ping = new EventsDigitalTwinType();
				ping.setName("ping");
				ping.setStatus(true);
				ping.setType(Type.PING);
				ping.setDescription("Ping the platform to keepalive the device");
				ping.setTypeId(digitalTwinType);
				eventDigitalTwinTypes.add(ping);
				
				log.error("9- REGISTER");
				EventsDigitalTwinType register = new EventsDigitalTwinType();
				register.setDescription("REGISTER");
				register.setName("register");
				register.setStatus(true);
				register.setType(Type.REGISTER);
				register.setDescription("Register the device into the plaform");
				register.setTypeId(digitalTwinType);
				eventDigitalTwinTypes.add(register);
				
				if(properties !=null && !properties[0].equals("")) {
					log.error("10- Recorre array de propiedades");
					for(String prop : properties) {
						json = new JSONObject(prop);
						PropertyDigitalTwinType p = new PropertyDigitalTwinType();
						p.setDescription(json.getString("description"));
						p.setName(json.getString("name"));
						p.setType(json.getString("type"));
						p.setUnit(json.getString("units"));
						p.setDirection(PropertyDigitalTwinType.Direction.valueOf(json.getString("direction").toUpperCase()));
						p.setTypeId(digitalTwinType);
						propertyDigitalTwinTypes.add(p);
					}
					log.error("11- Recorre array de propiedades");
				}
				
				if(actions!=null && !actions[0].equals("")) {
					log.error("12- Recorre array de actions");
					for(String action : actions) {
						ActionsDigitalTwinType act = new ActionsDigitalTwinType();
						json = new JSONObject(action);
						act.setName(json.getString("name"));
						act.setDescription(json.getString("description"));
						act.setTypeId(digitalTwinType);
						actionDigitalTwinTypes.add(act);
					}
					log.error("13- Recorre array de actions");
				}
				
				if(events != null && !events[0].equals("")) {
					log.error("14- Recorre array de eventos");
					for(String event : events) {
						EventsDigitalTwinType evt = new EventsDigitalTwinType();
						json = new JSONObject(event);
						evt.setName(json.getString("name"));
						evt.setDescription(json.getString("description"));
						evt.setStatus(json.getBoolean("status"));
						evt.setType(EventsDigitalTwinType.Type.valueOf(json.getString("type").toUpperCase()));
						evt.setTypeId(digitalTwinType);
						eventDigitalTwinTypes.add(evt);
					}
					log.error("15- Recorre array de eventos");
				}
				
				if(logic!=null) {
					LogicDigitalTwinType l = new LogicDigitalTwinType();
					l.setTypeId(digitalTwinType);
					l.setLogic(logic.replace("\\n", System.getProperty("line.separator")).substring(1, logic.length()-1).replace("\\r", "").replace("\\t", "   "));
					logicDigitalTwinTypes.add(l);
				}
				
				digitalTwinType.setPropertyDigitalTwinTypes(propertyDigitalTwinTypes);
				digitalTwinType.setActionDigitalTwinTypes(actionDigitalTwinTypes);
				digitalTwinType.setEventDigitalTwinTypes(eventDigitalTwinTypes);
				digitalTwinType.setLogicDigitalTwinTypes(logicDigitalTwinTypes);
				log.error("16- Persistencia en bd");
				this.digitalTwinTypeRepo.save(digitalTwinType);
				log.error("17- Persistencia en bd");
			} else {
				log.error("Invalid user");
			}				
			
		} catch (Exception e) {
			throw new DigitalTwinServiceException("Problems creating the digital twin type", e);
		}
	}
	
	@Override
	public void getDigitalTwinToUpdate(Model model, String id) {
		DigitalTwinType digitalTwinType = digitalTwinTypeRepo.findById(id);
		if(digitalTwinType!=null) {
			model.addAttribute("digitaltwintype", digitalTwinType);
			model.addAttribute("propiedades",getPropertiesByDigitalId(id));
			model.addAttribute("acciones",getActionsByDigitalId(id));
			model.addAttribute("eventos",getEventsByDigitalId(id));
			model.addAttribute("logica",getLogicByDigitalId(id));
		}else {
			log.error("DigitalTwinType with id:" + id + ", not found.");
		}
	}
	
	@Override
	public void updateDigitalTwinType(DigitalTwinType digitalTwinType, HttpServletRequest httpServletRequest) {
		
		//Update DigitalTwinType
		DigitalTwinType digitalTwinTypeDb = this.digitalTwinTypeRepo.findById(digitalTwinType.getId());
		if(digitalTwinTypeDb!=null) {
			this.digitalTwinTypeRepo.delete(digitalTwinTypeDb);
			this.createDigitalTwinType(digitalTwinType, httpServletRequest);
		}else {
			log.error("DigitalTwinType with identigication:" + digitalTwinType.getName() + "don't exist in data base to update.");
		}
		
	}
	
	@Override
	public void deleteDigitalTwinType(DigitalTwinType digitalTwinType) {
		this.digitalTwinTypeRepo.delete(digitalTwinType);
	}

	@Override
	public List<DigitalTwinType> getDigitalTwinTypesByUserId(String sessionUserId) {
		User sessionUser = this.userService.getUser(sessionUserId);
		if (sessionUser.getRole().getId().equals(Role.Type.ROLE_ADMINISTRATOR.toString())) {
			return this.digitalTwinTypeRepo.findAll();
		} else {
			return this.digitalTwinTypeRepo.findByUser(sessionUser);
		}
	}

}
