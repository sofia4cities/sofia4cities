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
package com.indracompany.sofia2.service.gadget;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.indracompany.sofia2.config.model.Gadget;
import com.indracompany.sofia2.config.model.User;
import com.indracompany.sofia2.config.repository.GadgetRepository;
import com.indracompany.sofia2.config.repository.UserRepository;

@Service
public class GadgetServiceImpl implements GadgetService {
	
	@Autowired
	private GadgetRepository gadgetRepository;
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
		
		if(user.getRoleTypeId().getName().equals(GadgetServiceImpl.ADMINISTRATOR))
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
	public Gadget getGadgetById(String id) {
		return gadgetRepository.findById(id);
	}
	
	@Override
	public void createGadget(Gadget gadget) {
		//Compruebo que no exista ninguno con ese nombre
		if(this.gadgetRepository.findByIdentification(gadget.getIdentification()) == null) {
			this.gadgetRepository.save(gadget);
		}
		
	}

}


