package com.indracompany.sofia2.service.gadget;

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
		
		if(user.getRoleTypeId().getName().equals(GadgetServiceImpl.ADMINISTRATOR))
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
	
	
	
 

