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
package com.indracompany.sofia2.controlpanel.controller.welcome;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.indracompany.sofia2.config.model.ClientPlatform;
import com.indracompany.sofia2.config.model.ClientPlatformOntology;
import com.indracompany.sofia2.config.model.Dashboard;
import com.indracompany.sofia2.config.model.DashboardType;
import com.indracompany.sofia2.config.model.Gadget;
import com.indracompany.sofia2.config.model.Ontology;
import com.indracompany.sofia2.config.model.UserCDB;
import com.indracompany.sofia2.config.repository.ClientPlatformRepository;
import com.indracompany.sofia2.config.repository.DashboardRepository;
import com.indracompany.sofia2.config.repository.DashboardTypeRepository;
import com.indracompany.sofia2.config.repository.GadgetRepository;
import com.indracompany.sofia2.config.repository.OntologyRepository;
import com.indracompany.sofia2.config.repository.UserCDBRepository;
import com.indracompany.sofia2.controlpanel.utils.AppWebUtils;

import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class WelcomeController {

	private String urlClientPlatform;
	private String urlDashboard;
	private String urlGadget;
	private String urlOntology;
	private String genericUserName="USER";
	private UserCDB user;

	@Autowired
	private OntologyRepository ontologyRepository;

	@Autowired
	private ClientPlatformRepository clientPlatformRepository;

	@Autowired
	private GadgetRepository gadgetRepository;
	
	@Autowired
	private UserCDBRepository userCDBRepository;

	@Autowired
	private DashboardTypeRepository dashboardTypeRepository;
	@Autowired
	private DashboardRepository dashboardRepository;

	@Autowired 
	private AppWebUtils utils;

	@Value("${sofia2.urls.iotbroker}")
	String url;

	@GetMapping("/main")
	public String home1(Model model) {
		Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
		user=userCDBRepository.findByUserId(authentication.getName());
		//rol de usuario
		//authentication.getAuthorities().toArray();

		//initialize URLS
		this.urlClientPlatform=this.url+"/console/clientPlatforms/";
		this.urlGadget=this.url+"/console/gadget/";
		this.urlDashboard=this.url+ "/console/dashboard/";
		this.urlOntology=this.url+"/console/ontologies/";

		List<GraphDTO> arrayLinks=new LinkedList<GraphDTO>();

		arrayLinks.add(GraphDTO.constructSingleNode(genericUserName,null,genericUserName,user.getUserId()));


		arrayLinks.addAll(constructGraphWithOntologies());

		arrayLinks.addAll(constructGraphWithClientPlatforms());

		arrayLinks.addAll(constructGraphWithVisualization());

		System.out.println(arrayLinks);
		return "/main";
	}

	private List<GraphDTO> constructGraphWithOntologies(){

		List<GraphDTO> arrayLinks=new LinkedList<GraphDTO>();
		String name=utils.getMessage("name.ontologies","ONTOLOGIES");
		String description=utils.getMessage("tooltip.ontologies",null);
		//carga de nodo ontologia con link a crear y con titulo
		arrayLinks.add(new GraphDTO(genericUserName,name, null, urlOntology+"list",genericUserName,name,user.getUserId(),name, "suit",description,urlOntology+"create"));

		List<Ontology> ontologies=ontologyRepository.findByUserId(user.getUserId());
		for (Ontology ont: ontologies){
			arrayLinks.add(new GraphDTO(name, ont.getId(), urlOntology+"list", urlOntology+ont.getId(),name,"ontology",name, ont.getIdentification(), "licensing"));
		}
		return arrayLinks;
	}
	
	private List<GraphDTO> constructGraphWithClientPlatforms(){

		List<GraphDTO> arrayLinks=new LinkedList<GraphDTO>();
		String name=utils.getMessage("name.clients","PLATFORM CLIENTS");
		String description=utils.getMessage("tooltip.clients",null);

		// carga de nodo clientPlatform
		arrayLinks.add(new GraphDTO(genericUserName,name, null, urlClientPlatform+"list",genericUserName,name,user.getUserId(),name, "suit",description,urlClientPlatform+"create"));

		List<ClientPlatform> clientPlatforms=clientPlatformRepository.findByUserId(user.getUserId());

		for (ClientPlatform clientPlatform:clientPlatforms){
			//Creación de enlaces
			arrayLinks.add(new GraphDTO(name, clientPlatform.getId(), urlClientPlatform+"list", urlClientPlatform+clientPlatform.getId(), name, "clientplatform", name, clientPlatform.getIdentification(), "licensing"));

			if (clientPlatform.getClientPlatformOntologies()!=null){
				List<ClientPlatformOntology> clientPlatformOntologies= new LinkedList<ClientPlatformOntology>(clientPlatform.getClientPlatformOntologies());
				for (ClientPlatformOntology clientPlatformOntology: clientPlatformOntologies){
					Ontology ontology=clientPlatformOntology.getOntologyId();
					//Crea link entre ontologia y clientPlatform
					arrayLinks.add(new GraphDTO(ontology.getId(),clientPlatform.getId(),urlOntology+ontology.getId(), urlClientPlatform+clientPlatform.getId(),"ontology","clientplatform", 
							ontology.getIdentification(), clientPlatform.getIdentification(),"licensing"));
				}
			}
		}
		return arrayLinks;
	}

	private List<GraphDTO> constructGraphWithGadgets(String visualizationId,String visualizationName){

		List<GraphDTO> arrayLinks=new LinkedList<GraphDTO>();
		String name=utils.getMessage("name.gadgets","GADGETS");

		// carga de nodo gadget dependiente de visualizacion
		arrayLinks.add(new GraphDTO(visualizationId,name, null, urlGadget+"list",visualizationId,name,visualizationName,name, "suit",null,urlGadget+"selectWizard"));

		List<Gadget> gadgets=gadgetRepository.findByUserId(user.getUserId());

		if (gadgets!=null){
			for (Gadget gadget:gadgets){
				//Creación de enlaces
				arrayLinks.add(new GraphDTO(name, gadget.getId(), urlGadget+"list", urlDashboard+gadget.getId(), name, "gadget", name,gadget.getName(),"licensing"));						
				if(gadget.getTokenId()!=null){
					//si tiene token , tiene kp
					arrayLinks.add(new GraphDTO(gadget.getTokenId().getClientPlatformId().getId(),gadget.getId(),urlClientPlatform+gadget.getTokenId().getClientPlatformId().getId(), 
							urlDashboard+gadget.getId(),"clientplatform","gadget",gadget.getTokenId().getClientPlatformId().getIdentification(), gadget.getName(),"suit"));
				}
			}
			gadgets.clear();
		}
		return arrayLinks;
	}

	private List<GraphDTO> constructGraphWithDashboard(String visualizationId,String visualizationName){

		List<GraphDTO> arrayLinks=new LinkedList<GraphDTO>();
		String name=utils.getMessage("name.dashboards","DASHBOARDS");

		arrayLinks.add(new GraphDTO(visualizationId,name,null,urlDashboard+"list",visualizationId,name,visualizationName,name, "suit",null,urlDashboard+"creategroup?"));

		// dashboardTipo---> son los dashboard
		List<DashboardType> dashboardTypes=dashboardTypeRepository.findByUserId(user.getUserId());
		for (DashboardType dashboardType:dashboardTypes){
			//Ahora hay que buscar la relacion entre dashboard y gadget. Eso nos lo da el dashboard
			List<Dashboard> dashboards=dashboardRepository.findByDashboardTypeId(Integer.toString(dashboardType.getId()));
			arrayLinks.add(new GraphDTO(name,Integer.toString(dashboardType.getId()),urlDashboard+"list",urlDashboard+Integer.toString(dashboardType.getId()),name,"dashboard", null,dashboardType.getType(),"licensing"));
			//
			//			for (Dashboard dashboard:dashboards){
			//				try{
			//					List<String> gadgetIds=GraphUtil.getGadgetIdsFromModel(dashboard.getModel());
			//					for (String gadget:gadgetIds){
			//						arrayLinks.add(new GraphDTO(gadget,Integer.toString(dashboardType.getId()),urlDashboard+gadget,urlDashboard+dashboardType.getId(),"gadget","dashboard", null,dashboardType.getType(),"licensing"));
			//					}
			//				}catch(Exception e){
			//					LOG.error("Error al recuperar gadgets del dashboard",e);
			//				}
			//			}
			//			dashboards.clear();
		}

		return arrayLinks;
	}
	private List<GraphDTO> constructGraphWithVisualization(){

		List<GraphDTO> arrayLinks=new LinkedList<GraphDTO>();
		String name=utils.getMessage("name_visualization","VISUALIZATION");
		String description=utils.getMessage("tooltip_visualization",null);
		// carga de nodo gadget
		arrayLinks.add(new GraphDTO(genericUserName,name,null,null,genericUserName,name,user.getUserId(),name, "suit",description,null));

		arrayLinks.addAll(constructGraphWithGadgets(name,name));


		arrayLinks.addAll(constructGraphWithDashboard(name,name));
		

		return arrayLinks;
	}



	//	private List<ClientPlatform> getClientPlatformList()
	//	{
	//		List<ClientPlatform> list;
	//		//Admin can see everything
	//		if(user.getRole().equals("ROLE_ADMINISTRATOR"))
	//		{
	//			list=clientPlatformRepository.findAll();
	//			
	//		}else{			
	//			
	//			list=clientPlatformRepository.findByUserId(user.getUserId());
	//			
	//		}
	//		
	//		return list;
	//		
	//		
	//	}

}
