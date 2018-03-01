package com.indracompany.sofia2.controlpanel.services.simulation;



import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.indracompany.sofia2.config.model.DeviceSimulation;
import com.indracompany.sofia2.config.services.simulation.DeviceSimulationService;
import com.indracompany.sofia2.scheduler.SchedulerType;
import com.indracompany.sofia2.scheduler.scheduler.bean.TaskInfo;
import com.indracompany.sofia2.scheduler.scheduler.bean.response.ScheduleResponseInfo;
import com.indracompany.sofia2.scheduler.scheduler.service.TaskService;

@Service
public class SimulationServiceImpl implements SimulationService  {

	@Autowired
	private DeviceSimulationService deviceSimulationService;
	@Autowired
	private TaskService taskService;
	
	@Override
	public String getDeviceSimulationJson(String clientPlatform, String token, String ontology, String jsonMap)
			throws JsonProcessingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		JsonNode rootNode = mapper.createObjectNode();

		((ObjectNode) rootNode).put("clientPlatform", clientPlatform);
		((ObjectNode) rootNode).put("token", token);
		((ObjectNode) rootNode).put("ontology", ontology);
		((ObjectNode) rootNode).set("fields", mapper.readTree(jsonMap));
		return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(rootNode);
	}

	@Override
	public void scheduleSimulation(String identification, int interval, String userId, String json) {
		
		DeviceSimulation simulation = this.deviceSimulationService.createSimulation(identification, interval, userId, json);
		
		TaskInfo task = new TaskInfo();
		task.setSchedulerType(SchedulerType.Script);
		
		Map<String, Object> jobContext = new HashMap<String, Object>();
		jobContext.put("id", simulation.getId());
		jobContext.put("json", simulation.getJson());
		
		task.setData(jobContext);
		task.setSingleton(false);
		task.setCronExpression(simulation.getCron());
		
		ScheduleResponseInfo response= this.taskService.addJob(task);
		
		simulation.setJobName(response.getJobName());
		this.deviceSimulationService.save(simulation);
	}
	

	
	
}
