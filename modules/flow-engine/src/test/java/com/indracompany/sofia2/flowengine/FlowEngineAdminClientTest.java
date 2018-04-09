package com.indracompany.sofia2.flowengine;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.indracompany.sofia2.commons.flow.engine.dto.FlowEngineDomain;
import com.indracompany.sofia2.commons.flow.engine.dto.FlowEngineDomainStatus;
import com.indracompany.sofia2.flowengine.nodered.communication.NodeRedAdminClient;
import com.indracompany.sofia2.flowengine.nodered.sync.NodeRedDomainSyncMonitor;

@RunWith(SpringRunner.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@SpringBootTest
public class FlowEngineAdminClientTest {

	private static String domainId = "DomainTest-" + new DateTime().getMillis();
	private static int port = 8005;
	private static int servicePort = 7005;
	private static String home = "/tmp/user/";
	private static FlowEngineDomain domain;
	private static List<FlowEngineDomainStatus> domains;

	@Autowired
	private NodeRedAdminClient nodeRedAdminClient;

	@Autowired
	private NodeRedDomainSyncMonitor nodeRedMonitor;

	@BeforeClass
	public static void setup() throws InterruptedException {
		// TODO: Unimplemented

		FlowEngineDomainStatus domStatus = FlowEngineDomainStatus.builder().domain(domainId).port(port)
				.servicePort(servicePort).home(home).state("STOP").build();
		domains = new ArrayList<>();
		domains.add(domStatus);

		domain = FlowEngineDomain.builder().domain(domainId).port(port).servicePort(servicePort).home(home).build();

	}

	@Test
	public void test1_createDomain() throws InterruptedException {
		nodeRedMonitor.stopMonitor();
		Thread.sleep(40000);
		nodeRedAdminClient.createFlowengineDomain(domain);
		FlowEngineDomain domainCreated = nodeRedAdminClient.getFlowEngineDomain(domain.getDomain());
		Assert.assertTrue(domainCreated != null);
	}

	@Test
	public void test2_startDomain() {
		nodeRedMonitor.stopMonitor();
		FlowEngineDomain domainCreated = nodeRedAdminClient.getFlowEngineDomain(domainId);
		nodeRedAdminClient.startFlowEngineDomain(domainCreated);
		Assert.assertTrue(true);
	}

	@Test
	public void test3_getAllDomins() {
		nodeRedMonitor.stopMonitor();
		List<FlowEngineDomainStatus> domains = nodeRedAdminClient.getAllFlowEnginesDomains();
		Assert.assertTrue(domains != null);
	}

	@Test
	public void test4_getDomin() {
		nodeRedMonitor.stopMonitor();
		FlowEngineDomain domain = nodeRedAdminClient.getFlowEngineDomain(domainId);
		Assert.assertTrue(domain != null);
	}

	@Test
	public void test5_getDominListStatus() {
		nodeRedMonitor.stopMonitor();
		List<String> domainList = new ArrayList<>();
		domainList.add(domainId);
		List<FlowEngineDomainStatus> domainStatus = nodeRedAdminClient.getFlowEngineDomainStatus(domainList);
		Assert.assertTrue(domainStatus != null);
	}

	@Test
	public void test6_stopDomain() {
		nodeRedMonitor.stopMonitor();
		nodeRedAdminClient.stopFlowEngineDomain(domainId);
		Assert.assertTrue(true);
	}

	@Test
	public void test7_deleteDomain() throws InterruptedException {

		nodeRedMonitor.stopMonitor();
		nodeRedAdminClient.deleteFlowEngineDomain(domainId);
		FlowEngineDomain domain = nodeRedAdminClient.getFlowEngineDomain(domainId);
		Assert.assertTrue(domain.getDomain() == null);
	}

}
