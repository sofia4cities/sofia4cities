package threads;

import java.io.IOException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.indracompany.sofia2.client.MQTTClient;
import com.indracompany.sofia2.client.MQTTClient.LOG_LEVEL;
import com.indracompany.sofia2.client.MQTTClient.STATUS_TYPE;
import com.indracompany.sofia2.client.SubscriptionListener;

import application.Application;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DeviceConnectionThread implements Runnable {

	private static final String deviceTemplate = "Device Master";
	private static final String token = "56686a5a0d7e497d9cafbbbd4b2563ee";
	private static final int timeout = 50;
	private double latitude;
	private double longitude;
	private String logMessage;
	private String deviceId;

	private String sessionKey;
	private String serverUrl;
	private String tags;
	private MQTTClient client;
	private ObjectMapper mapper = new ObjectMapper();
	private JsonNode deviceConfig;

	public DeviceConnectionThread(String serverUrl, double latitude, double longitude, String logMessage,
			String deviceId, String tags) {
		super();
		this.serverUrl = serverUrl;
		this.latitude = latitude;
		this.longitude = longitude;
		this.logMessage = logMessage;
		this.deviceId = deviceId;
		this.tags = tags;
		client = new MQTTClient(this.serverUrl);
		try {
			deviceConfig = mapper.readTree(
					"[{\"action_power\":{\"shutdown\":0,\"start\":1,\"reboot\":2}},{\"action_light\":{\"on\":1,\"off\":0}}]");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		this.sessionKey = client.connect(token, deviceTemplate, deviceId, tags, timeout, deviceConfig);
		if (this.sessionKey != null) {
			client.subscribeCommands(new SubscriptionListener() {

				@Override
				public void onMessageArrived(String message) {
					try {
						JsonNode cmdMsg = mapper.readTree(message);
						generateCommandResponse(cmdMsg);
					} catch (IOException e) {

						e.printStackTrace();
					}

				}

			});

			while (true) {
				LOG_LEVEL level = LOG_LEVEL.INFO;
				STATUS_TYPE status = STATUS_TYPE.OK;
				if (this.logMessage.equals(Application.ERROR_MESSAGE)) {
					level = LOG_LEVEL.ERROR;
					status = STATUS_TYPE.ERROR;
				}
				client.log(this.logMessage, this.latitude, this.longitude, status, level, timeout);
				try {
					Thread.sleep(30000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	protected void generateCommandResponse(JsonNode cmdMsg) {
		this.client.logCommand("Executed command " + cmdMsg.get("params").toString(), this.latitude, this.longitude,
				STATUS_TYPE.OK, LOG_LEVEL.INFO, cmdMsg.get("commandId").asText(), timeout);

	}

}