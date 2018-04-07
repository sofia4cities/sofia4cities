/**
 * Copyright Indra Sistemas, S.A.
 * 2013-2018 SPAIN
 *
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
package com.indracompany.sofia2.digitaltwin.event.manager;

import java.util.Arrays;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.indracompany.sofia2.digitaltwin.event.model.LogMessage;
import com.indracompany.sofia2.digitaltwin.event.model.PingMessage;
import com.indracompany.sofia2.digitaltwin.event.model.RegisterMessage;
import com.indracompany.sofia2.digitaltwin.event.model.ShadowMessage;

import lombok.extern.slf4j.Slf4j;


@Slf4j
@Component
public class EventRestManager implements EventManager {
	
	@Value("${api.key}")
	private String apikey;
	
	@Value("${device.id}")
	private String deviceId;
	
	@Value("${device.rest.local.schema}")
	private String localUrlSchema;
	
	@Value("${device.rest.local.ip}")
	private String localIP;
	
	@Value("${server.port}")
	private String localPort;
	
	@Value("${device.rest.basepath}")
	private String localBasePath;
	
	@Value("${sofia2.digitaltwin.broker.rest}")
	private String brokerEndpoint;
	
	
	@Value("${device.register.fail.retry.seconds:60}")
	private int registerRetryInterval;
			
	@Value("${device.ping.interval.seconds:10}")
	private int pingInterval;
					
	
	private RestTemplate restTemplate;
	
	private HttpHeaders headers;
	

	@PostConstruct
	public void init() {
		this.restTemplate = new RestTemplate();
		
		this.headers = new HttpHeaders();
		this.headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		this.headers.setContentType(MediaType.APPLICATION_JSON);
		this.headers.set("Authorization", this.apikey);
		
		this.register();
	}
	
	private void register() {
		new RegistryAndKeepAliveThread().start();
	}
	
	/**
	 * Hilo que gestiona el registro y envio de KeepAlive a plataforma 
	 * @author INDRA SISTEMAS
	 *
	 *
	 */
	class RegistryAndKeepAliveThread extends Thread{

		@Override
		public void run() {
			RegisterMessage registerMessage=new RegisterMessage();
			registerMessage.setId(deviceId);
			registerMessage.setEndpoint(localUrlSchema+"://"+localIP+":"+localPort+"/"+localBasePath);
			
			PingMessage pingMessage=new PingMessage();
			pingMessage.setId(deviceId);
			
			HttpEntity<RegisterMessage> registerEntity = new HttpEntity<RegisterMessage>(registerMessage, headers);
			HttpEntity<PingMessage> pingEntity = new HttpEntity<PingMessage>(pingMessage, headers);
			
			
			while(true) {
				boolean register=false;
				
				//Send Register message to platform
				while(!register) {
					try {
						log.info("Attemp to register in broker {}", brokerEndpoint);
						ResponseEntity<String> resp = restTemplate.exchange(brokerEndpoint+"/event/register", HttpMethod.POST, registerEntity, String.class);
						if(resp.getStatusCode()==HttpStatus.OK) {
							log.info("Registered in broker {}", brokerEndpoint);
							register=true;
						}else {
							log.warn("HTTP code {} registering in broker {}", resp.getStatusCode(), brokerEndpoint);
							log.warn("Broker message {}", resp.getBody());
							try {
								Thread.sleep(registerRetryInterval*1000);
							} catch (InterruptedException e) {}
						}
					}catch(Exception e) {
						log.error("Error trying to register in broker", e);
						register=false;
						try {
							Thread.sleep(registerRetryInterval*1000);
						} catch (InterruptedException ex) {}
					}
				}
				
				//Send ping message to platform
				while(register) {
					try {
						log.info("Attemp to ping broker {}", brokerEndpoint);
						ResponseEntity<String> resp = restTemplate.exchange(brokerEndpoint+"/event/ping", HttpMethod.POST, pingEntity, String.class);
						if(resp.getStatusCode()==HttpStatus.OK) {
							log.info("Successful ping in broker {}", brokerEndpoint);
							try {
								Thread.sleep(registerRetryInterval*1000);
							} catch (InterruptedException e) {}
							
						}else {
							log.warn("HTTP code {} trying to ping broker {}", resp.getStatusCode(), brokerEndpoint);
							log.warn("Broker message {}", resp.getBody());
							register=false;
						}
					}catch(Exception e) {
						log.error("Error trying to ping broker", e);
						register=false;
					}
				}
			}			
		}
		
	}
	
	@Override
	public void updateShadow(Map<String, Object> status) {
		ShadowMessage shadowMessage=new ShadowMessage();
		shadowMessage.setId(deviceId);
		shadowMessage.setStatus(status);
		
		HttpEntity<ShadowMessage> shadowEntity = new HttpEntity<ShadowMessage>(shadowMessage, headers);
		
		log.info("Attemp to update shadow in broker {}", brokerEndpoint);
		ResponseEntity<String> resp = restTemplate.exchange(brokerEndpoint+"/event/shadow", HttpMethod.POST, shadowEntity, String.class);
		if(resp.getStatusCode()==HttpStatus.OK) {
			log.info("Updated shadow in broker {}", brokerEndpoint);
		}else {
			//Meter en almacenamiento??
			log.warn("HTTP code {} updating shadow in broker {}", resp.getStatusCode(), brokerEndpoint);
			log.warn("Broker message {}", resp.getBody());
		}
	}
	
	@Override
	public void log(String trace) {
		LogMessage logMessage=new LogMessage();
		logMessage.setId(deviceId);
		logMessage.setLog(trace);
		
		HttpEntity<LogMessage> logEntity = new HttpEntity<LogMessage>(logMessage, headers);
		
		log.info("Attemp to log in broker {}", brokerEndpoint);
		ResponseEntity<String> resp = restTemplate.exchange(brokerEndpoint+"/event/log", HttpMethod.POST, logEntity, String.class);
		if(resp.getStatusCode()==HttpStatus.OK) {
			log.info("Log in broker {}", brokerEndpoint);
		}else {
			//Meter en almacenamiento??
			log.warn("HTTP code {} log in broker {}", resp.getStatusCode(), brokerEndpoint);
			log.warn("Broker message {}", resp.getBody());
		}
	}
	
	
}
