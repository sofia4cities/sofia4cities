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
package com.indracompany.sofia2.controlpanel.helper.market;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import org.springframework.web.multipart.MultipartFile;

import com.indracompany.sofia2.config.model.MarketAsset.MarketAssetPaymentMode;
import com.indracompany.sofia2.config.model.MarketAsset.MarketAssetState;
import com.indracompany.sofia2.config.model.MarketAsset.MarketAssetType;
import com.indracompany.sofia2.config.model.User;

import lombok.Getter;
import lombok.Setter;

public class MarketAssetDTO {
	
	@Getter
	@Setter
	private String id;
	
	@Getter
	@Setter
	private String identification;
	
	@Getter
	@Setter
	private User user;
	
	@Getter
	@Setter
	private boolean isPublic;
	
	@Getter
	@Setter
	private MarketAssetState state;
	
	@Getter
	@Setter
	private MarketAssetType marketAssetType;
	
	@Getter
	@Setter
	private MarketAssetPaymentMode paymentMode;
	
	@Getter
	@Setter
	private String title;
	
	@Getter
	@Setter
	private String description;
	
	@Getter
	@Setter
	private String technologies;
	
	@Getter
	@Setter
	private String jsonDesc;
	
	@Getter
	@Setter 
	private Date createdAt;
	
	@Getter
	@Setter  
	private Date updatedAt;
	
	@Getter
	@Setter
	byte[] image;
}
