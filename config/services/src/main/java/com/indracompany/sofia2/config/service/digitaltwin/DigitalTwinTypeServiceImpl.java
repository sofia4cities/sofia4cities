package com.indracompany.sofia2.config.service.digitaltwin;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.indracompany.sofia2.config.model.ActionsDigitalTwinType;
import com.indracompany.sofia2.config.model.DigitalTwinType;
import com.indracompany.sofia2.config.model.EventsDigitalTwinType;
import com.indracompany.sofia2.config.model.LogicDigitalTwinType;
import com.indracompany.sofia2.config.model.PropertyDigitalTwinType;
import com.indracompany.sofia2.config.model.User;
import com.indracompany.sofia2.config.repository.ActionsDigitalTwinTypeRepository;
import com.indracompany.sofia2.config.repository.DigitalTwinTypeRepository;
import com.indracompany.sofia2.config.repository.EventsDigitalTwinTypeRepository;
import com.indracompany.sofia2.config.repository.LogicDigitalTwinTypeRepository;
import com.indracompany.sofia2.config.repository.PropertyDigitalTwinTypeRepository;
import com.indracompany.sofia2.config.services.exceptions.OntologyServiceException;
import com.indracompany.sofia2.config.services.user.UserService;


@Service
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
	public void createDigitalTwinType(DigitalTwinType digitalTwinType, HttpServletRequest httpServletRequest) {
		try {
			String[] properties = httpServletRequest.getParameterValues("propiedades");
			String[] actions = httpServletRequest.getParameterValues("acciones");
			String[] events = httpServletRequest.getParameterValues("eventos");
			String logic = httpServletRequest.getParameter("logic");
			
			JSONObject json;
			if (digitalTwinTypeRepo.findByName(digitalTwinType.getName())==null) {
				
				User user = userService.getUser(digitalTwinType.getUser().getUserId());
				if (user != null) {
					digitalTwinType.setUser(user);
					this.digitalTwinTypeRepo.save(digitalTwinType);
					
					if(!properties[0].equals("")) {
						for(String prop : properties) {
							json = new JSONObject(prop);
							PropertyDigitalTwinType p = new PropertyDigitalTwinType();
							p.setDescription(json.getString("description"));
							p.setName(json.getString("name"));
							p.setType(json.getString("type"));
							p.setUnit(json.getString("units"));
							p.setDirection(PropertyDigitalTwinType.Direction.valueOf(json.getString("direction").toUpperCase()));
							p.setTypeId(digitalTwinType);
							this.propDigitalTwinTypeRepo.save(p);
						}
					}
					
					if(!actions[0].equals("")) {
						for(String action : actions) {
							ActionsDigitalTwinType act = new ActionsDigitalTwinType();
							json = new JSONObject(action);
							act.setName(json.getString("name"));
							act.setDescription(json.getString("description"));
							act.setTypeId(digitalTwinType);
							this.actDigitalTwinTypeRepo.save(act);
						}
					}
					
					if(!events[0].equals("")) {
						for(String event : events) {
							EventsDigitalTwinType evt = new EventsDigitalTwinType();
							json = new JSONObject(event);
							evt.setName(json.getString("name"));
							evt.setDescription(json.getString("description"));
							evt.setStatus(json.getBoolean("status"));
							evt.setType(EventsDigitalTwinType.Type.valueOf(json.getString("type").toUpperCase()));
							evt.setTypeId(digitalTwinType);
							this.evtDigitalTwinTypeRepo.save(evt);
						}
					}
					
					LogicDigitalTwinType l = new LogicDigitalTwinType();
					l.setTypeId(digitalTwinType);
					l.setLogic(logic.replaceAll("//n", "").replace("//t", ""));
					this.logicDigitalTwinTypeRepo.save(l);
					
				} else {
					throw new OntologyServiceException("Invalid user");
				}				
			} else {
				throw new OntologyServiceException(
						"DigitalTwinType with identification:" + digitalTwinType.getName() + " exists");
			}
		} catch (Exception e) {
			throw new OntologyServiceException("Problems creating the digital twin type", e);
		}
	}

}
