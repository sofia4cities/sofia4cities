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
package com.indracompany.sofia2.controller;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;

import com.indracompany.sofia2.dto.socket.InputMessage;
import com.indracompany.sofia2.dto.socket.OutputMessage;
import com.indracompany.sofia2.service.SolverService;

import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class SocketController {

	@Autowired
	private SimpMessagingTemplate simpMessagingTemplate;

	@Autowired
	private SolverService solverService;

	@CrossOrigin
	@MessageMapping("/dsengine/solver/{id}")
	// @SendTo("/secured/history")
	public void response(@DestinationVariable("id") Long id, SimpMessageHeaderAccessor headerAccessor, InputMessage msg)
			throws Exception {
		String dataSolved = solverService.solveDatasource(msg);
		OutputMessage out = new OutputMessage(dataSolved,
				new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").format(new Date()));
		//
		simpMessagingTemplate.convertAndSend("/dsengine/broker/" + id, out);
	}

	/*
	 * private MessageHeaders createHeaders(String sessionId) {
	 * SimpMessageHeaderAccessor headerAccessor =
	 * SimpMessageHeaderAccessor.create(SimpMessageType.MESSAGE);
	 * headerAccessor.setSessionId(sessionId); headerAccessor.setLeaveMutable(true);
	 * return headerAccessor.getMessageHeaders(); }
	 */
}
