package com.indracompany.sofia2.api.audit.aop;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

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
import com.indracompany.sofia2.audit.bean.Sofia2AuditError;
import com.indracompany.sofia2.audit.bean.Sofia2AuditEvent.EventType;
import com.indracompany.sofia2.audit.bean.Sofia2AuditEvent.Module;
import com.indracompany.sofia2.audit.bean.Sofia2AuditEvent.OperationType;
import com.indracompany.sofia2.audit.bean.Sofia2AuditEvent.ResultOperationType;
import com.indracompany.sofia2.config.model.ApiOperation;
import com.indracompany.sofia2.config.model.Ontology;
import com.indracompany.sofia2.config.model.User;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest
public class ApiManagerAuditProcessorTest {

	@InjectMocks
	ApiManagerAuditProcessor apiManagerAuditProcessor;

	private final String REMOTE_ADDRESS = "0.0.0.1";
	private final String REASON = "Reason dummy";
	private final String USER_ID = "userTest";
	private final String ONTOLOGY_NAME = "testOntology";

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

	@Test
	public void given_insert_event_getEvent() {

		Map<String, Object> data = new HashMap<>();

		String body = "{'test':'test'}";

		data.put(ApiServiceInterface.REMOTE_ADDRESS, REMOTE_ADDRESS);
		data.put(ApiServiceInterface.ONTOLOGY, getOntologyTest());
		data.put(ApiServiceInterface.METHOD, ApiOperation.Type.POST.name());
		data.put(ApiServiceInterface.BODY, body);
		data.put(ApiServiceInterface.USER, getUserTest());

		ApiManagerAuditEvent event = apiManagerAuditProcessor.getEvent(data);

		assertEquals(ONTOLOGY_NAME, event.getOntology());
		assertEquals(REMOTE_ADDRESS, event.getRemoteAddress());
		assertEquals(OperationType.INSERT.name(), event.getOperationType());
		assertEquals(ResultOperationType.SUCCESS, event.getResultOperation());
		assertEquals(Module.APIMANAGER, event.getModule());
		assertEquals(EventType.APIMANAGER, event.getType());
		assertEquals(body, event.getData());
		Assert.assertNull(event.getQuery());
		assertEquals(USER_ID, event.getUser());
	}

	@Test
	public void given_event_getErrorEvent() {
		CamelContext ctx = new DefaultCamelContext();
		Exchange exchange = new DefaultExchange(ctx);

		Map<String, Object> data = new HashMap<>();
		Exception ex = new Exception();

		Sofia2AuditError event = apiManagerAuditProcessor.getErrorEvent(data, exchange, ex);

		assertEquals(EventType.ERROR, event.getType());
		Assert.assertNotNull(event.getMessage());
		assertEquals(ex, event.getEx());
		assertEquals(Module.APIMANAGER, event.getModule());
	}

	@Test
	public void given_event_completeEvent_user_is_null() {
		ApiManagerAuditEvent event = ApiManagerAuditEvent.builder().build();

		Map<String, Object> retVal = new HashMap<>();
		retVal.put(ApiServiceInterface.USER, null);

		event = apiManagerAuditProcessor.completeEvent(retVal, event);
		assertEquals(AuditConst.ANONYMOUS_USER, event.getUser());
	}

	@Test
	public void given_event_completeEvent_user_is_not_null() {

		ApiManagerAuditEvent event = ApiManagerAuditEvent.builder().user(USER_ID).build();
		event = apiManagerAuditProcessor.completeEvent(new HashMap<>(), event);

		assertEquals(USER_ID, event.getUser());
	}

	@Test
	public void given_getAuditOperationFromMethod_with_an_unexisting_method() {
		OperationType operationType = apiManagerAuditProcessor.getAuditOperationFromMethod("");
		Assert.assertNull(operationType);
	}

	@Test
	public void given_getAuditOperationFromMethod_with_null_method() {
		OperationType operationType = apiManagerAuditProcessor.getAuditOperationFromMethod(null);
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

	private User getUserTest() {
		User user = new User();
		user.setUserId(USER_ID);
		return user;
	}

	private Ontology getOntologyTest() {
		Ontology ontology = new Ontology();
		ontology.setIdentification(ONTOLOGY_NAME);
		return ontology;
	}
}
