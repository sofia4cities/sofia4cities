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
package com.indracompany.sofia2.resources.service;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.indracompany.sofia2.config.components.Urls;
import com.indracompany.sofia2.config.services.configuration.ConfigurationService;

@Service
public class IntegrationResourcesServiceImpl implements IntegrationResourcesService {

	@Autowired
	private ConfigurationService configurationService;
	@Autowired
	private Environment environment;

	private Urls urls;

	public enum ServiceUrl {
		base, advice, management, router, hawtio, swaggerUI, api, swaggerUIManagement, swaggerJson
	}

	public enum Module {
		iotbroker, scriptingEngine, flowEngine, routerStandAlone, apiManager, controlpanel, digitalTwinBroker, domain
	}

	public final static String SWAGGER_UI_SUFFIX = "swagger-ui.html";
	public final static String LOCALHOST = "localhost";

	@PostConstruct
	public void getActiveProfile() {
		String[] profiles = environment.getActiveProfiles();
		String activeProfile = "default";
		if (profiles.length > 0)
			activeProfile = profiles[0];
		else
			activeProfile = environment.getDefaultProfiles()[0];
		this.urls = this.configurationService.getEndpointsUrls(activeProfile);
	}

	@Override
	public String getUrl(Module module, ServiceUrl service) {

		switch (module) {
		case controlpanel:
			switch (service) {
			case base:
				return this.urls.getControlpanel().getBase();
			default:
				break;

			}
		case iotbroker:
			switch (service) {
			case base:
				return this.urls.getIotbroker().getBase();
			case advice:
				return this.urls.getIotbroker().getAdvice();
			default:
				break;
			}
			break;
		case scriptingEngine:
			switch (service) {
			case base:
				return this.urls.getScriptingEngine().getBase();
			case advice:
				return this.urls.getScriptingEngine().getAdvice();
			default:
				break;
			}
			break;
		case flowEngine:
			switch (service) {
			case base:
				return this.urls.getFlowEngine().getBase();
			case advice:
				return this.urls.getFlowEngine().getAdvice();
			default:
				break;
			}
			break;
		case routerStandAlone:
			switch (service) {
			case base:
				return this.urls.getRouterStandAlone().getBase();
			case advice:
				return this.urls.getRouterStandAlone().getAdvice();
			case management:
				return this.urls.getRouterStandAlone().getManagement();
			case router:
				return this.urls.getRouterStandAlone().getRouter();
			case hawtio:
				return this.urls.getRouterStandAlone().getHawtio();
			case swaggerUI:
				return this.urls.getRouterStandAlone().getSwaggerUI();
			default:
				break;
			}
			break;
		case apiManager:
			switch (service) {
			case base:
				return this.urls.getApiManager().getBase();
			case api:
				return this.urls.getApiManager().getApi();
			case swaggerUI:
				return this.urls.getApiManager().getSwaggerUI();
			case swaggerUIManagement:
				return this.urls.getApiManager().getSwaggerUIManagement();
			case swaggerJson:
				return this.urls.getApiManager().getSwaggerJson();

			default:
				break;
			}

			break;
		case digitalTwinBroker:
			switch (service) {
			case base:
				return this.urls.getDigitalTwinBroker().getBase();
			default:
				break;
			}
		case domain:
			switch (service) {
			case base:
				return this.urls.getDomain().getBase();
			default:
				break;
			}
		default:
			break;
		}
		return "RESOURCE_URL_NOT_FOUND";
	}

	@Override
	public Map<String, String> getSwaggerUrls() {
		Map<String, String> map = new HashMap<>();
		String base = this.urls.getDomain().getBase();
		String controlpanel = base.endsWith("/") ? base.concat("/controlpanel")
				: base.concat("/").concat("/controlpanel");
		String iotbroker = base.endsWith("/") ? base.concat("/iotbroker") : base.concat("/").concat("/iotbroker");
		String apimanager = base.endsWith("/") ? base.concat("/apimanager") : base.concat("/").concat("/apimanager");
		String router = base.endsWith("/") ? base.concat("/router") : base.concat("/").concat("/router");
		String digitalTwinBroker = base.endsWith("/") ? base.concat("/digitaltwinbroker")
				: base.concat("/").concat("/digitaltwinbroker");
		if (base.contains(LOCALHOST)) {
			controlpanel = this.urls.getControlpanel().getBase();
			iotbroker = this.urls.getIotbroker().getBase();
			apimanager = this.urls.getApiManager().getBase();
			router = this.urls.getRouterStandAlone().getBase();
			digitalTwinBroker = this.urls.getDigitalTwinBroker().getBase();
		}
		map.put(Module.controlpanel.name(), controlpanel.endsWith("/") ? controlpanel.concat(SWAGGER_UI_SUFFIX)
				: controlpanel.concat("/").concat(SWAGGER_UI_SUFFIX));
		map.put(Module.iotbroker.name(), iotbroker.endsWith("/") ? iotbroker.concat(SWAGGER_UI_SUFFIX)
				: iotbroker.concat("/").concat(SWAGGER_UI_SUFFIX));
		map.put(Module.apiManager.name(), apimanager.endsWith("/") ? apimanager.concat(SWAGGER_UI_SUFFIX)
				: apimanager.concat("/").concat(SWAGGER_UI_SUFFIX));
		map.put(Module.routerStandAlone.name(),
				router.endsWith("/") ? router.concat(SWAGGER_UI_SUFFIX) : router.concat("/").concat(SWAGGER_UI_SUFFIX));
		map.put(Module.digitalTwinBroker.name(),
				digitalTwinBroker.endsWith("/") ? digitalTwinBroker.concat(SWAGGER_UI_SUFFIX)
						: digitalTwinBroker.concat("/").concat(SWAGGER_UI_SUFFIX));

		return map;
	}

}
