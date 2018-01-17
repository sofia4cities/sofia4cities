package com.indracompany.sofia2.controlpanel.controller.welcome;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.GetMapping;

import com.indracompany.sofia2.config.model.ClientPlatform;
import com.indracompany.sofia2.config.model.ClientPlatformOntology;
import com.indracompany.sofia2.config.model.Dashboard;
import com.indracompany.sofia2.config.model.DashboardType;
import com.indracompany.sofia2.config.model.Gadget;
import com.indracompany.sofia2.config.model.Ontology;
import com.indracompany.sofia2.config.model.UserCDB;
import com.indracompany.sofia2.config.repository.OntologyRepository;
import com.indracompany.sofia2.config.repository.ClientPlatformRepository;
import com.indracompany.sofia2.config.repository.DashboardRepository;
import com.indracompany.sofia2.config.repository.DashboardTypeRepository;
import com.indracompany.sofia2.config.repository.GadgetRepository;

@Controller
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
	private DashboardTypeRepository dashboardTypeRepository;
	@Autowired
	private DashboardRepository dashboardRepository;

	@Autowired
	private MessageSource messageSource;

	@Value("${sofia2.urls.iotbroker}")
	String url;

	@GetMapping("/")
	public String home1(Model model) {
		//Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
		//rol de usuario
		//authentication.getAuthorities().toArray();

		//initialize URLS
		this.urlClientPlatform=this.url+"/console/clientPlatforms/";
		this.urlGadget=this.url+"/console/gadget/";
		this.urlDashboard=this.url+ "/console/dashboard/";
		this.urlOntology=this.url+"/console/ontologias/";

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
		String idOntology="ONTOLOGIES";
		//Tener en cuenta mas adelante para internacionalizacion(LOCALE)
		String name=getParamInter("menu_category_ontologias_label",idOntology);
		String description=getParamInter("tooltip_ontologias",null);
		//carga de nodo ontologia con link a crear y con titulo
		arrayLinks.add(new GraphDTO(genericUserName,idOntology, null, urlOntology+"list",genericUserName,idOntology,user.getUserId(),name, "suit",description,urlOntology+"create"));

		List<Ontology> ontologies=ontologyRepository.findByUserId(user.getUserId());
		for (Ontology ont: ontologies){
			arrayLinks.add(new GraphDTO(idOntology, ont.getId(), urlOntology+"list", urlOntology+ont.getId(),idOntology,"ontology",name, ont.getIdentification(), "licensing"));
		}
		return arrayLinks;


	}
	private List<GraphDTO> constructGraphWithClientPlatforms(){

		List<GraphDTO> arrayLinks=new LinkedList<GraphDTO>();
		String idClientPlatforms="Platform Clients";
		String name=getParamInter("menu_category_KPS_label2",idClientPlatforms);
		String description=getParamInter("tooltip_clientPlatforms",null);

		// carga de nodo clientPlatform
		arrayLinks.add(new GraphDTO(genericUserName,idClientPlatforms, null, urlClientPlatform+"list",genericUserName,idClientPlatforms,user.getUserId(),name, "suit",description,urlClientPlatform+"create"));

		List<ClientPlatform> clientPlatforms=clientPlatformRepository.findByUserId(user.getUserId());

		for (ClientPlatform clientPlatform:clientPlatforms){
			//Creación de enlaces
			arrayLinks.add(new GraphDTO(idClientPlatforms, clientPlatform.getId(), urlClientPlatform+"list", urlClientPlatform+clientPlatform.getId(), idClientPlatforms, "clientplatform", name, clientPlatform.getIdentification(), "licensing"));

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
		String gadgetId="GADGETS";
		String name=getParamInter("gadgets_breadcrumb",gadgetId);

		// carga de nodo gadget dependiente de visualizacion
		arrayLinks.add(new GraphDTO(visualizationId,gadgetId, null, urlGadget+"list",visualizationId,gadgetId,visualizationName,name, "suit",null,urlGadget+"selectWizard"));

		List<Gadget> gadgets=gadgetRepository.findByUserId(user.getUserId());

		if (gadgets!=null){
			for (Gadget gadget:gadgets){
				//Creación de enlaces
				arrayLinks.add(new GraphDTO(gadgetId, gadget.getId(), urlGadget+"list", urlDashboard+gadget.getId(), gadgetId, "gadget", name,gadget.getName(),"licensing"));						
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
		String dashboardId="DASHBOARDS";
		String name=getParamInter("dashboards_breadcrumb",dashboardId);

		arrayLinks.add(new GraphDTO(visualizationId,dashboardId,null,urlDashboard+"list",visualizationId,dashboardId,visualizationName,name, "suit",null,urlDashboard+"creategroup?"));

		// dashboardTipo---> son los dashboard
		List<DashboardType> dashboardTypes=dashboardTypeRepository.findByUserId(user.getUserId());
		for (DashboardType dashboardType:dashboardTypes){
			//Ahora hay que buscar la relacion entre dashboard y gadget. Eso nos lo da el dashboard
			List<Dashboard> dashboards=dashboardRepository.findByDashboardTypeId(Integer.toString(dashboardType.getId()));
			arrayLinks.add(new GraphDTO(dashboardId,Integer.toString(dashboardType.getId()),urlDashboard+"list",urlDashboard+Integer.toString(dashboardType.getId()),dashboardId,"dashboard", null,dashboardType.getType(),"licensing"));
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

		String visualizationId="VISUALIZACION";
		List<GraphDTO> arrayLinks=new LinkedList<GraphDTO>();
		String visualizationName=getParamInter("visualizaciones_breadcrumb",visualizationId);
		String description=getParamInter("tooltip_visualizacion",null);
		// carga de nodo gadget
		arrayLinks.add(new GraphDTO(genericUserName,visualizationId,null,null,genericUserName,visualizationId,user.getUserId(),visualizationName, "suit",description,null));

		arrayLinks.addAll(constructGraphWithGadgets(visualizationId,visualizationName));


		arrayLinks.addAll(constructGraphWithDashboard(visualizationId,visualizationName));
		

		return arrayLinks;
	}

	private String getParamInter(String reference,String valueDefault){

		try{
			Locale locale = LocaleContextHolder.getLocale();
			return messageSource.getMessage(reference, null, locale);
		}catch (Exception e){
			return valueDefault;
		}
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
