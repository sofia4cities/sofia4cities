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
package com.indra.sofia2.support.util.sib.loader.events;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ContextLoaderEventListenersRegistryImpl implements ContextLoaderEventListenersRegistry {

	private static final Logger log = LoggerFactory.getLogger(ContextLoaderEventListenersRegistry.class);

	private Map<String, ContextLoaderEventListener> eventListeners;
	
	@PostConstruct
	public void init(){
		log.info("Initializing ContextLoader event listener registry...");
		eventListeners = new ConcurrentHashMap<String, ContextLoaderEventListener>();
	}
	
	@Override
	public void addEventListener(ContextLoaderEventListener listener) {
		eventListeners.put(listener.getId(), listener);
	}

	@Override
	public void removeEventListener(ContextLoaderEventListener listener) {
		eventListeners.remove(listener.getId());
	}

	@Override
	public void notifyContextDestroyedEvent() {
		for (ContextLoaderEventListener listener : eventListeners.values()) {
			listener.onContextDestroyed();
		}
	}
}