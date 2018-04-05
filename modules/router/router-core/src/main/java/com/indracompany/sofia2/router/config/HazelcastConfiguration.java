/**
 * Copyright Indra Sistemas, S.A.
 * 2013-2018 SPAIN
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
package com.indracompany.sofia2.router.config;

import org.apache.camel.component.hazelcast.queue.HazelcastQueueComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.hazelcast.core.HazelcastInstance;
import com.indracompany.sofia2.router.camel.CamelContextHandler;

@Configuration

public class HazelcastConfiguration {

	@Autowired
	CamelContextHandler camelContextHandler;

	@Autowired
	HazelcastInstance instance;

	@Bean
	public HazelcastQueueComponent hazelcastQueueComponent() {
		// setup camel hazelcast
		HazelcastQueueComponent hazelcast = new HazelcastQueueComponent();
		hazelcast.setHazelcastInstance(instance);
		camelContextHandler.getDefaultCamelContext().addComponent("hazelcast-queue", hazelcast);
		return hazelcast;
	}

}