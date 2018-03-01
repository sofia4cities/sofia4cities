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
import com.indracompany.sofia2.scheduler.scheduler.bean.TaskOperation;
import com.indracompany.sofia2.scheduler.scheduler.bean.response.ScheduleResponseInfo;
import com.indracompany.sofia2.scheduler.scheduler.service.TaskService;

@Service
public class SimulationServiceImpl implements SimulationService {

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
	public void createSimulation(String identification, int interval, String userId, String json) throws JsonProcessingException, IOException {

		DeviceSimulation simulation = this.deviceSimulationService.createSimulation(identification, interval, userId,
				json);
		this.scheduleSimulation(simulation);
	}

	@Override
	public void unscheduleSimulation(DeviceSimulation deviceSimulation) {
		String jobName = deviceSimulation.getJobName();
		if (jobName != null && deviceSimulation.isActive()) {
			TaskOperation operation = new TaskOperation();
			operation.setJobName(jobName);
			this.taskService.unscheduled(operation);
			deviceSimulation.setActive(false);
			deviceSimulation.setJobName(null);
			this.deviceSimulationService.save(deviceSimulation);
		}
		

	}

	@Override
	public void scheduleSimulation(DeviceSimulation deviceSimulation) {

		if (!deviceSimulation.isActive()) {
			TaskInfo task = new TaskInfo();
			task.setSchedulerType(SchedulerType.Script);

			Map<String, Object> jobContext = new HashMap<String, Object>();
			jobContext.put("id", deviceSimulation.getId());
			jobContext.put("json", deviceSimulation.getJson());
			task.setJobName("Device Simulation");
			task.setData(jobContext);
			task.setSingleton(false);
			task.setCronExpression(deviceSimulation.getCron());
			task.setUsername(deviceSimulation.getUser().getUserId());
			ScheduleResponseInfo response = this.taskService.addJob(task);
			deviceSimulation.setActive(true);
			deviceSimulation.setJobName(response.getJobName());
			this.deviceSimulationService.save(deviceSimulation);

		}

	}

}
