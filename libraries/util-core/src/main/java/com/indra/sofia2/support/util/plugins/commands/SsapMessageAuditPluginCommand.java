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
package com.indra.sofia2.support.util.plugins.commands;

import java.util.Date;

import com.indra.sofia2.ssap.ssap.SSAPMessage;
import com.indra.sofia2.support.util.plugins.dto.audit.AuditDirection;
import com.indra.sofia2.support.util.plugins.dto.audit.AuditType;
import com.indra.sofia2.support.util.plugins.dto.audit.ClientGatewayData;
import com.indra.sofia2.support.util.plugins.interfaces.audit.AuditPlugin;
import com.netflix.hystrix.HystrixCommand;

public class SsapMessageAuditPluginCommand extends HystrixCommand<Boolean>{

	private final AuditPlugin plugin;
	
	private SSAPMessage<?> message;
	private String cipherKey;
	private Date timeStamp; 
	private ClientGatewayData client;
	private AuditDirection direction;
	private AuditType type;
	
	public SsapMessageAuditPluginCommand(Setter setter, AuditPlugin plugin, SSAPMessage<?> message, String cipherKey, Date timeStamp, ClientGatewayData client, AuditDirection direction, AuditType type) {
        super(setter);
        this.plugin = plugin;
        this.message=message;
        this.cipherKey=cipherKey;
        this.timeStamp=timeStamp;
        this.client=client;
        this.direction=direction;
        this.type=type;
        
    }
		
	@Override
	protected Boolean run() throws Exception {
		try{
			plugin.audit(message, cipherKey, timeStamp, client, direction, type);
		}catch(Exception e){
			return false;
		}
		return true;
	}
	
	@Override
    protected Boolean getFallback() {
		return false;
    }

}
