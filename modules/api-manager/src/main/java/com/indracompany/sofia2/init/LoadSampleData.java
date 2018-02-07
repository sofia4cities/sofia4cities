package com.indracompany.sofia2.init;

import java.io.File;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.indracompany.sofia2.api.rest.api.dto.ApiDTO;
import com.indracompany.sofia2.api.rest.api.fiql.ApiFIQL;
import com.indracompany.sofia2.api.service.api.ApiServiceRest;
import com.indracompany.sofia2.config.model.Api;

@Component
public class LoadSampleData implements ApplicationRunner {

	@Autowired
	private ApiServiceRest apiService;

	@Autowired
	private ApiFIQL apiFIQL;
	
	ObjectMapper mapper = new ObjectMapper();
	
	@Override
	public void run(ApplicationArguments arg0) throws Exception {
		loadDataFromJson();
	}


	private void loadDataFromJson() throws Exception {
		String token = "acbca01b-da32-469e-945d-05bb6cd1552e";
		try {
			Api theApi = apiService.findApi("APITEST", token);
		} catch (Exception e) {
			File in = new ClassPathResource("data/data.json").getFile();
			
			ApiDTO api = mapper.readValue(in, ApiDTO.class);
			apiService.createApi(api, token);
			ApiDTO out = apiFIQL.toApiDTO(apiService.findApi("APITEST", token));
			
			System.out.println(out);
		}
			
			
		
	}
}