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
package com.indracompany.sofia2.iotbroker.audit.listener;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.indracompany.sofia2.audit.listener.Sofia2EventListener;
import com.indracompany.sofia2.iotbroker.audit.bean.IotBrokerAuditEvent;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class IotBrokerAuditEventListener extends Sofia2EventListener {
	
	@EventListener
    @Async
    public void handleSofia2AuditErrorEvent(IotBrokerAuditEvent event) {    	
    	log.info("iotbroker audit event: " + event.toString());   	
    	eventRouter.notify(event.toJson());
    }
}
