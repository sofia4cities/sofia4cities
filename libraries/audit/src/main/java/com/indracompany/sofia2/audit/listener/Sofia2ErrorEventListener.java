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
package com.indracompany.sofia2.audit.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.indracompany.sofia2.audit.bean.Sofia2AuditError;
import com.indracompany.sofia2.audit.bean.Sofia2AuditEvent;
import com.indracompany.sofia2.audit.notify.EventRouter;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class Sofia2ErrorEventListener extends Sofia2EventListener {
	
	/*
	@Async
	@EventListener
	void handleAsync(Sofia2AuditEvent event) throws JsonProcessingException {
		log.info("Sofia2EventListener :: thread '{}' handling '{}' async event", Thread.currentThread(), event);
		eventRouter.notify(event.toJson());
	}
	
	
	*/
	@EventListener
    @Async
    public void handleSofia2AuditErrorEvent(Sofia2AuditError event) {    	
    	log.info("error audit event: " + event.toString());   	
    	eventRouter.notify(event.toJson());
    }

}
