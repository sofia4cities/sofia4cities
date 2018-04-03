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
package com.indracompany.sofia2.controlpanel.helper.market;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.indracompany.sofia2.config.model.Api;
import com.indracompany.sofia2.config.model.MarketAsset;
import com.indracompany.sofia2.config.model.Role;
import com.indracompany.sofia2.config.model.User;
import com.indracompany.sofia2.config.repository.ApiRepository;
import com.indracompany.sofia2.config.repository.MarketAssetRepository;
import com.indracompany.sofia2.config.services.user.UserService;
import com.indracompany.sofia2.controlpanel.multipart.MarketAssetMultipart;
import com.indracompany.sofia2.controlpanel.utils.AppWebUtils;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class MarketAssetHelper {
	
	@Autowired
	ApiRepository apiRepository;
	
	@Autowired
	private MarketAssetRepository marketAssetRepository;
	
	@Autowired
	UserService userService;
	
	@Autowired
	AppWebUtils utils;

	public void populateMarketAssetListForm(Model model) {
		// TODO Auto-generated method stub
	}

	public void populateMarketAssetCreateForm(Model model) {
		model.addAttribute("marketasset", new MarketAsset());
		model.addAttribute("marketassettypes", MarketAsset.MarketAssetType.values());
		model.addAttribute("marketassetmodes", MarketAsset.MarketAssetPaymentMode.values());
	}

	public void populateMarketAssetUpdateForm(Model model, String id) {
		MarketAsset marketAsset = marketAssetRepository.findById(id);
		model.addAttribute("marketasset", marketAsset);
		model.addAttribute("marketassettypes", MarketAsset.MarketAssetType.values());
		model.addAttribute("marketassetmodes", MarketAsset.MarketAssetPaymentMode.values());
		
		populateMarketAssetFragment(model, marketAsset.getMarketAssetType().toString());
	}

	public void populateMarketAssetShowForm(Model model, String id) {
		MarketAsset marketAsset = marketAssetRepository.findById(id);
		model.addAttribute("marketasset", marketAsset);
		model.addAttribute("json_desc", marketAsset.getJsonDesc());
	}

	public void populateMarketAssetFragment(Model model, String type) {
		
		if (type.equals(MarketAsset.MarketAssetType.API.toString())) {
			
			User user = this.userService.getUser(utils.getUserId());
			
			List<Api> apiList = null;
			
			if (utils.getRole().equals(Role.Type.ROLE_ADMINISTRATOR.toString())){
				apiList = apiRepository.findAll();
			} else {
				apiList = apiRepository.findByUser(user);
			}
			
			List<Api> apis = getApisIds(apiList);
			
			model.addAttribute("apis", apis);
			
		} else if (type.equals(MarketAsset.MarketAssetType.DOCUMENT.toString())){
			
		} else if (type.equals(MarketAsset.MarketAssetType.APPLICATION.toString())){
			
		} else if (type.equals(MarketAsset.MarketAssetType.WEBPROJECT.toString())){
			
		}

	}

	private List<Api> getApisIds(List<Api> apiList) {
		List<Api> apis = new ArrayList<Api>();
		for (Api api : apiList) {
			if (api.getState().equals(Api.ApiStates.DEVELOPMENT) || api.getState().equals(Api.ApiStates.PUBLISHED)) {
				apis.add(api);
			}
		}
		return apis;
	}

	public void populateApiVersions(Model model, String identification) {
		User user = this.userService.getUser(utils.getUserId());
		
		List<Api> apiList = null;
		
		if (utils.getRole().equals(Role.Type.ROLE_ADMINISTRATOR.toString())){
			apiList = apiRepository.findByIdentification(identification);
		} else {
			apiList = apiRepository.findByIdentificationAndUser(identification, user);
		}
		
		List<Api> apis = getApisIds(apiList);
		
		model.addAttribute("apis", apis);
	}

//	public void populateApiDescription(Model model, String identification, String version) {
//		List<Api> apis = apiRepository.findByIdentificationAndNumversion(identification, Integer.parseInt(version));
//		model.addAttribute("api", apis);
//	}
	
	public String getApiDescription(String apiData) {
		List<Api> apis = null;
		try {
			Map<String, String> obj = new ObjectMapper().readValue(apiData, new TypeReference<Map<String, String>>(){});
			
			String identification = obj.get("identification");
			String version = obj.get("version");
			
			apis = apiRepository.findByIdentificationAndNumversion(identification, Integer.parseInt(version));

		} catch (JsonParseException e) {
			log.warn(e.getClass().getName() + ":" + e.getMessage());
		} catch (JsonMappingException e) {
			log.warn(e.getClass().getName() + ":" + e.getMessage());
		} catch (IOException e) {
			log.warn(e.getClass().getName() + ":" + e.getMessage());
		}
		return (apis.get(0).getDescription());
	}
	
	public String validateId(String marketAssetId) {
		try {
			Map<String, String> obj = new ObjectMapper().readValue(marketAssetId, new TypeReference<Map<String, String>>(){});
			
			String identification = obj.get("identification");
			
			MarketAsset marketAsset = marketAssetRepository.findByIdentification(identification);
			
			if (marketAsset!= null) {
				return "ERROR";
			}
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}


	public MarketAsset marketAssetMultipartMap(MarketAssetMultipart marketAssetMultipart) {

		MarketAsset marketAsset = new MarketAsset();
		
		marketAsset.setIdentification(marketAssetMultipart.getIdentification());
		
		marketAsset.setUser(this.userService.getUser(utils.getUserId()));

		marketAsset.setPublic(marketAssetMultipart.isPublic());
		marketAsset.setState(marketAssetMultipart.getState());
		marketAsset.setMarketAssetType(marketAssetMultipart.getMarketAssetType());
		marketAsset.setPaymentMode(marketAssetMultipart.getPaymentMode());
		
		marketAsset.setJsonDesc(marketAssetMultipart.getJsonDesc().toString());
		
		try {
			if (marketAssetMultipart.getContent()!=null) {
				marketAsset.setContent(marketAssetMultipart.getContent().getBytes());
				marketAsset.setContentType(getFileExt(marketAssetMultipart.getContent().getOriginalFilename()));
			}
			if (marketAssetMultipart.getImage()!=null) {
				marketAsset.setImage(marketAssetMultipart.getImage().getBytes());
				marketAsset.setImageType(getFileExt(marketAssetMultipart.getImage().getOriginalFilename()));
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		marketAsset.setCreatedAt(marketAssetMultipart.getCreatedAt());
		marketAsset.setUpdatedAt(marketAssetMultipart.getUpdatedAt());

		return marketAsset;
	}

	private String getFileExt(String originalFilename) {
		return originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
	}

}
