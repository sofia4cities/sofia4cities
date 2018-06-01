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
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.hazelcast.config.Config;
import com.hazelcast.config.EvictionPolicy;
import com.hazelcast.config.JoinConfig;
import com.hazelcast.config.MapConfig;
import com.hazelcast.config.MaxSizeConfig;
import com.hazelcast.config.MulticastConfig;
import com.hazelcast.config.NetworkConfig;
import com.hazelcast.config.TcpIpConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.indracompany.sofia2.router.camel.CamelContextHandler;

@Configuration
@EnableCaching
public class HazelcastCacheConfiguration {
	
	@Autowired
	CamelContextHandler camelContextHandler;
	
	@Bean
	public HazelcastQueueComponent hazelcastQueueComponent() {
		// setup camel hazelcast
		HazelcastQueueComponent hazelcast = new HazelcastQueueComponent();
		hazelcast.setHazelcastInstance(hazelcastInstance());
		camelContextHandler.getDefaultCamelContext().addComponent("hazelcast-queue", hazelcast);
		return hazelcast;
	}
	
	@Bean
	public HazelcastInstance hazelcastInstance() {
		return Hazelcast.newHazelcastInstance(hazelCastConfig());
	}

	@Bean
	public Config hazelCastConfig() {
		
        NetworkConfig networkConfig = new NetworkConfig();
        JoinConfig joinConfig= new JoinConfig().setTcpIpConfig(new TcpIpConfig().addMember("localhost")); 
        
        joinConfig.setMulticastConfig(new MulticastConfig().setEnabled(false)); //Este te desactiva el multicast
        networkConfig.setJoin(joinConfig);

		
		return new Config()
				//.setInstanceName("sofia2-s4c-hazelcast-instance")
                .setNetworkConfig(networkConfig)
				.addMapConfig(new MapConfig().setName("queries")
						.setMaxSizeConfig(new MaxSizeConfig(2000, MaxSizeConfig.MaxSizePolicy.FREE_HEAP_SIZE))
						.setEvictionPolicy(EvictionPolicy.LRU).setTimeToLiveSeconds(20));
	}
}