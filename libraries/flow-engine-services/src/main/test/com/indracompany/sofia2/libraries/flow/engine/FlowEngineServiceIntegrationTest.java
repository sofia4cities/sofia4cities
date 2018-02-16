package com.indracompany.sofia2.libraries.flow.engine;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.test.context.junit4.SpringRunner;

import com.indracompany.sofia2.libraries.flow.engine.dto.FlowEngineDomain;
import com.indracompany.sofia2.libraries.flow.engine.dto.FlowEngineDomainStatus;

import lombok.extern.slf4j.Slf4j;

@RunWith(SpringRunner.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@Slf4j
public class FlowEngineServiceIntegrationTest {

	private FlowEngineService flowEngineService;
	private String baseUrl = "http://localhost:8082/sofia2/flowengine/admin";
	private int restTimeuot = 5000;

	@Before
	public void setup() {
		flowEngineService = FlowEngineServiceFactory.getFlowEngineService(this.baseUrl, this.restTimeuot);
	}

	@Test
	public void test1_getAllDomins() {
		List<FlowEngineDomainStatus> domains = flowEngineService.getAllFlowEnginesDomains();
		Assert.assertTrue(domains != null);
	}

	@Test
	public void test2_getDomin() {
		FlowEngineDomain domain = flowEngineService.getFlowEngineDomain("DominioTest");
		Assert.assertTrue(domain != null);
	}

	@Test
	public void test3_getDominListStatus() {
		List<String> domainList = new ArrayList<>();
		domainList.add("DominioTest");
		domainList.add("DomainTesting");
		List<FlowEngineDomainStatus> domainStatus = flowEngineService.getFlowEngineDomainStatus(domainList);
		Assert.assertTrue(domainStatus != null);
	}
}
