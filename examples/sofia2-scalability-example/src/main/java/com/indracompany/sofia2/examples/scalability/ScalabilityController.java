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
package com.indracompany.sofia2.examples.scalability;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.indracompany.sofia2.examples.scalability.msgs.BasicMsg;
import com.indracompany.sofia2.examples.scalability.msgs.Connection;
import com.indracompany.sofia2.examples.scalability.msgs.Greeting;
import com.indracompany.sofia2.examples.scalability.msgs.Injector;
import com.indracompany.sofia2.examples.scalability.msgs.InjectorStatus;
import com.indracompany.sofia2.examples.scalability.msgs.InsertionTask;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class ScalabilityController {

	private Object lock = new Object();

	private ConcurrentHashMap<Injector, InsertionTask> tasks = new ConcurrentHashMap<Injector, InsertionTask>();

	private Connection connection;

	@Autowired
	private TaskExecutor taskExecutor;

	@Autowired
	private BeanFactory beanFactory;

	public ScalabilityController() {

	}

	@RequestMapping("/greeting")
	public Greeting greeting(@RequestParam(value = "name", defaultValue = "World") String name) {
		Greeting greeting = new Greeting();
		greeting.setMsg("Hello!!!");
		return greeting;
	}

	@PostMapping(value = "/connection", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public @ResponseBody ResponseEntity<Connection> setConnection(@RequestBody Connection connection) {
		synchronized (lock) {
			if (!(tasks.size() > 0)) {
				this.connection = connection;
				return new ResponseEntity<Connection>(connection, HttpStatus.OK);
			} else {
				return new ResponseEntity<Connection>(HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
	}

	@PostMapping(value = "/startInjector", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public @ResponseBody ResponseEntity<Injector> startInjector(@RequestParam int injector,
			@RequestParam String protocol, @RequestBody String data) throws IOException {

		Client client;
		Injector inject;
		InsertionTask task;

		synchronized (lock) {
			try {
				switch (protocol) {
				case "mqtt":
					client = new ClientMqttWrapper(connection.getMqttURL());
					break;
				case "rest":
					client = new ClientRestWrapper(connection.getRestURL());
					break;
				case "kafka":
					client = new ClientKafkaProducerWrapper(connection.getKafkaURL());
					break;
				default:
					return new ResponseEntity<Injector>(HttpStatus.INTERNAL_SERVER_ERROR);
				}
				client.connect(connection.getToken(), connection.getClientPlatform(),
						connection.getClientPlatformInstance() + "-" + injector, true);
				task = beanFactory.getBean(InsertionTask.class, client, connection.getOntology(), data, injector);
			} catch (Exception e) {
				log.error("Error connectiong with the server", e);
				return new ResponseEntity<Injector>(HttpStatus.INTERNAL_SERVER_ERROR);
			}

			inject = new Injector(injector, data);
			tasks.putIfAbsent(inject, task);
			taskExecutor.execute(task);
		}

		return new ResponseEntity<Injector>(inject, HttpStatus.OK);
	}

	@GetMapping(value = "/status", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public @ResponseBody ResponseEntity<List<InjectorStatus>> getStatus() {
		ArrayList<InjectorStatus> statues = new ArrayList<InjectorStatus>();
		for (InsertionTask task : tasks.values()) {
			InjectorStatus status = task.getStatus();
			statues.add(status);
		}
		return new ResponseEntity<List<InjectorStatus>>(statues, HttpStatus.OK);
	}

	@GetMapping(value = "/getDataConnection", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public @ResponseBody ResponseEntity<Connection> getDataConnection() {
		synchronized (lock) {
			return new ResponseEntity<Connection>(connection, HttpStatus.OK);
		}
	}

	@GetMapping(value = "/stopInjector", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public @ResponseBody ResponseEntity<BasicMsg> stopInjector(@RequestParam int injector) {
		Injector inj = new Injector(injector, null);
		InsertionTask task = tasks.get(inj);
		if (task != null) {
			task.stop();
		}
		tasks.remove(inj);
		BasicMsg msg = new BasicMsg("Injector Removed");
		return new ResponseEntity<BasicMsg>(msg, HttpStatus.OK);
	}
}
