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

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
		MarketAsset market= marketAssetRepository.findById(id);
		
		byte[] buffer = market.getContent();
		
		return buffer;
	}

}
