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

import com.indra.sofia2.support.util.plugins.dto.audit.AuditOperation;
import com.indra.sofia2.support.util.plugins.dto.audit.AuditType;
import com.indra.sofia2.support.util.plugins.interfaces.audit.AuditPlugin;
import com.netflix.hystrix.HystrixCommand;

public class ScriptRuntimeAuditPluginCommand extends HystrixCommand<Boolean>{

	private final AuditPlugin plugin;
	
	private String scriptId;
	private String ontology;
	private String ontologyName;
	private Date timeStamp;
	private AuditOperation operation;
	private String sessionKey;
	private AuditType type;
	
	public ScriptRuntimeAuditPluginCommand(Setter setter, AuditPlugin plugin, String scriptId, String ontology, String ontologyName, Date timeStamp, AuditOperation operation, String sessionKey, AuditType type) {
        super(setter);
        this.plugin = plugin;
        this.scriptId=scriptId;
		this.ontology=ontology;
		this.ontologyName=ontologyName;
		this.timeStamp=timeStamp;
		this.operation=operation;
		this.sessionKey=sessionKey;
		this.type=type;
        
    }
	
	@Override
	protected Boolean run() throws Exception {
		try{
			plugin.audit(scriptId, ontology, ontologyName, timeStamp, operation, sessionKey,type);
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
