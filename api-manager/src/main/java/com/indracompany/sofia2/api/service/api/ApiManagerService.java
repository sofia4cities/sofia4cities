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
package com.indracompany.sofia2.api.service.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.indracompany.sofia2.config.repository.ApiRepository;

@Service
public class ApiManagerService {

	@Autowired
	private ApiRepository apiRepository;

	public ApiRepository getApiRepository() {
		return apiRepository;
	}

	public void setApiRepository(ApiRepository apiRepository) {
		this.apiRepository = apiRepository;
	}
	
	
	
	
}
