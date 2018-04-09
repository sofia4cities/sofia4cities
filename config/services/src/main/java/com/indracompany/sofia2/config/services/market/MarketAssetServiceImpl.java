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
package com.indracompany.sofia2.config.services.market;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.indracompany.sofia2.config.model.MarketAsset;
import com.indracompany.sofia2.config.model.MarketAsset.MarketAssetState;
import com.indracompany.sofia2.config.model.Role;
import com.indracompany.sofia2.config.model.User;
import com.indracompany.sofia2.config.repository.MarketAssetRepository;
import com.indracompany.sofia2.config.services.user.UserService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class MarketAssetServiceImpl implements MarketAssetService {
	
	@Autowired
	UserService userService;
	
	@Autowired
	private MarketAssetRepository marketAssetRepository;
	
	@Override
	public List<MarketAsset> loadMarketAssetByFilter(String marketAssetId, String userId) {
		User user = this.userService.getUser(userId);
		
		if (marketAssetId==null || marketAssetId.trim().equals("")) {
			marketAssetId="";
		}
		
		if (user.getRole().getId().equals(Role.Type.ROLE_ADMINISTRATOR.name())) {
			return marketAssetRepository.findByIdentificationLike(marketAssetId);
		} else {
			return (filterByUserOrAproved(marketAssetRepository.findByIdentificationLike(marketAssetId), user));
		}
	}

	@Override
	public String createMarketAsset(MarketAsset marketAsset) {
		
		if (marketAsset.getUser().getRole().getId().equals(Role.Type.ROLE_ADMINISTRATOR.name())){
			marketAsset.setState(MarketAssetState.APPROVED);
		} else {
			marketAsset.setState(MarketAssetState.PENDING);
		}
		
		marketAssetRepository.save(marketAsset);
		
		return marketAsset.getId();
	}
	
	@Override
	public void updateMarketAsset(String id, MarketAsset marketAsset) {
		
		MarketAsset marketAssetMemory = marketAssetRepository.findById(id);
		
		marketAssetMemory.setIdentification(marketAsset.getIdentification());
		
		marketAssetMemory.setUser(marketAsset.getUser());

		marketAssetMemory.setPublic(marketAsset.isPublic());
		marketAssetMemory.setMarketAssetType(marketAsset.getMarketAssetType());
		marketAssetMemory.setPaymentMode(marketAsset.getPaymentMode());
		
		marketAssetMemory.setJsonDesc(marketAsset.getJsonDesc());
		
		if (marketAsset.getContentId()==null || "".equals(marketAsset.getContentId())) {
			marketAssetMemory.setContent(null);
			marketAssetMemory.setContentId(null);
		} else if (marketAsset.getContent()!=null && marketAsset.getContent().length>0) {
			marketAssetMemory.setContent(marketAsset.getContent());
			marketAssetMemory.setContentId(marketAsset.getContentId());
		}
		
		if (marketAsset.getImageType()==null || "".equals(marketAsset.getImageType())) {
			marketAssetMemory.setImage(null);
			marketAssetMemory.setImageType(null);
		} else if (marketAsset.getImage()!=null && marketAsset.getImage().length>0) {
			marketAssetMemory.setImage(marketAsset.getImage());
			marketAssetMemory.setImageType(marketAsset.getImageType());	
		}
		
		marketAssetMemory.setCreatedAt(marketAsset.getCreatedAt());
		marketAssetMemory.setUpdatedAt(marketAsset.getUpdatedAt());

		marketAssetRepository.save(marketAssetMemory);
	}

	
	private List<MarketAsset> filterByUserOrAproved(List<MarketAsset> marketAssetList, User user) {
		List<MarketAsset> marketAssetResult = new ArrayList<MarketAsset>();
		for (MarketAsset marketAsset : marketAssetList) {
			if (marketAsset.getUser().getUserId().equals(user.getUserId())) {
				marketAssetResult.add(marketAsset);
			} else if (marketAsset.getState().name().equals(MarketAsset.MarketAssetState.APPROVED.name()) && marketAsset.isPublic()){
				marketAssetResult.add(marketAsset);
			}
		}
		return marketAssetResult;
	}
	
	@Override
	public byte[] getImgBytes(String id) {
		MarketAsset market= marketAssetRepository.findById(id);
		
		byte[] buffer = market.getImage();
		
		return buffer;
	}

	@Override
	public byte[] getContent(String id) {
		MarketAsset marketAsset= marketAssetRepository.findById(id);
		
		byte[] buffer = marketAsset.getContent();
		
		return buffer;
	}
	
	public void downloadDocument(String id, HttpServletResponse response) throws Exception{
		MarketAsset marketAsset= marketAssetRepository.findById(id);

		InputStream bis = new ByteArrayInputStream(marketAsset.getContent());
		String name = marketAsset.getContentId();
		response.setContentType("application/octet-stream");
		
		ServletOutputStream out;
		try {		 
			response.setHeader("Content-Disposition", "filename=" + name);	
			out = response.getOutputStream();
			IOUtils.copy(bis, out);
			response.flushBuffer();   
		} catch (IOException e) {
		}   
	}

	@Override
	public void updateState(String id, String state, String reason) {
		Map<String, String> obj;
		String rejectReason = "";
		try {
			obj = new ObjectMapper().readValue(reason, new TypeReference<Map<String, String>>(){});
			rejectReason = obj.get("rejectionReason");
		} catch (IOException e) {
			e.printStackTrace();
		}

		MarketAsset marketAsset= marketAssetRepository.findById(id);
		
		marketAsset.setRejectionReason(rejectReason);
		marketAsset.setState(MarketAssetState.valueOf(state));
		
		marketAssetRepository.save(marketAsset);
	}
}
