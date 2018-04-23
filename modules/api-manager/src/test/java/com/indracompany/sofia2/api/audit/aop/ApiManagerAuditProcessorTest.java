package com.indracompany.sofia2.api.audit.aop;

import static org.junit.Assert.assertEquals;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.DefaultExchange;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;

import com.indracompany.sofia2.api.audit.bean.ApiManagerAuditEvent;
import com.indracompany.sofia2.api.service.ApiServiceInterface;
import com.indracompany.sofia2.audit.bean.AuditConst;
import com.indracompany.sofia2.audit.bean.Sofia2AuditEvent.EventType;
import com.indracompany.sofia2.audit.bean.Sofia2AuditEvent.Module;
import com.indracompany.sofia2.audit.bean.Sofia2AuditEvent.OperationType;
import com.indracompany.sofia2.audit.bean.Sofia2AuditEvent.ResultOperationType;
import com.indracompany.sofia2.config.model.ApiOperation;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest
public class ApiManagerAuditProcessorTest {

	@InjectMocks
	ApiManagerAuditProcessor apiManagerAuditProcessor;

	private final String REMOTE_ADDRESS = "0.0.0.1";
	private final String REASON = "Reason dummy";

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void given_getStoppedEvent_no_stop_status() {
		CamelContext ctx = new DefaultCamelContext();
		Exchange exchange = new DefaultExchange(ctx);
		exchange.getIn().setHeader(ApiServiceInterface.STATUS, "FOLLOW");
		ApiManagerAuditEvent event = apiManagerAuditProcessor.getStoppedEvent(exchange);
		Assert.assertNull(event);
	}

	@Test
	public void given_getStoppedEvent_stop_status() {
		CamelContext ctx = new DefaultCamelContext();
		Exchange exchange = new DefaultExchange(ctx);
		exchange.getIn().setHeader(ApiServiceInterface.STATUS, "STOP");

		exchange.getIn().setHeader(ApiServiceInterface.REASON, REASON);

		exchange.getIn().setHeader(ApiServiceInterface.REMOTE_ADDRESS, REMOTE_ADDRESS);
		exchange.getIn().setHeader(ApiServiceInterface.ONTOLOGY, null);
		exchange.getIn().setHeader(ApiServiceInterface.METHOD, ApiOperation.Type.GET.name());

		exchange.getIn().setHeader(ApiServiceInterface.QUERY, "query test");
		exchange.getIn().setHeader(ApiServiceInterface.BODY, "body test");

		exchange.getIn().setHeader(ApiServiceInterface.USER, null);

		ApiManagerAuditEvent event = apiManagerAuditProcessor.getStoppedEvent(exchange);

		Assert.assertNull(event.getOntology());
		assertEquals(AuditConst.ANONYMOUS_USER, event.getUser());
		assertEquals(REMOTE_ADDRESS, event.getRemoteAddress());
		assertEquals(OperationType.QUERY.name(), event.getOperationType());
		assertEquals(ResultOperationType.ERROR, event.getResultOperation());
		assertEquals(Module.APIMANAGER, event.getModule());
		assertEquals(EventType.APIMANAGER, event.getType());
		assertEquals(REASON, event.getMessage());
	}

	/*
	 * @Test public void given_getEvent() {
	 * 
	 * String remoteAddress = (String) data.get(ApiServiceInterface.REMOTE_ADDRESS);
	 * Ontology ontology = (Ontology) data.get(ApiServiceInterface.ONTOLOGY); String
	 * method = (String) data.get(ApiServiceInterface.METHOD);
	 * 
	 * String query = (String) data.get(ApiServiceInterface.QUERY); String body =
	 * (String) data.get(ApiServiceInterface.BODY);
	 * 
	 * User user = (User) data.get(ApiServiceInterface.USER);
	 * 
	 * OperationType operationType = getAuditOperationFromMethod(method); }
	 */
	@Test
	public void given_getAuditOperationFromMethod_with_an_unexisting_method() {
		String method = "";
		OperationType operationType = apiManagerAuditProcessor.getAuditOperationFromMethod(method);
		Assert.assertNull(operationType);
	}

	@Test
	public void given_getAuditOperationFromMethod_with_get_method() {
		OperationType operationType = apiManagerAuditProcessor
				.getAuditOperationFromMethod(ApiOperation.Type.GET.name());
		assertEquals(OperationType.QUERY, operationType);
	}

	@Test
	public void given_getAuditOperationFromMethod_with_post_method() {
		OperationType operationType = apiManagerAuditProcessor
				.getAuditOperationFromMethod(ApiOperation.Type.POST.name());
		assertEquals(OperationType.INSERT, operationType);
	}

	@Test
	public void given_getAuditOperationFromMethod_with_put_method() {
		OperationType operationType = apiManagerAuditProcessor
				.getAuditOperationFromMethod(ApiOperation.Type.PUT.name());
		assertEquals(OperationType.UPDATE, operationType);
	}

	@Test
	public void given_getAuditOperationFromMethod_with_delete_method() {
		OperationType operationType = apiManagerAuditProcessor
				.getAuditOperationFromMethod(ApiOperation.Type.DELETE.name());
		assertEquals(OperationType.DELETE, operationType);
	}
}
