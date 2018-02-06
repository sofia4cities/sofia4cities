package com.indracompany.sofia2.api.rest.camel;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.indracompany.sofia2.api.service.ApiServiceInterface;

@Service
public class ThrottlerProcessor implements Processor {

	@Autowired
	ApiServiceInterface apiService;
	
	@Override
	public void process(Exchange exchange) throws Exception {
		System.out.println(exchange.toString());
		
	}
	
	public int getThrottle() {
		return 3;
	}

}
