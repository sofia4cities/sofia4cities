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

@Service
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

	}
	
	
	
 

