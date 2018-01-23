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
/*******************************************************************************
 * © Indra Sistemas, S.A.
 * 2013 - 2014  SPAIN
 * 
 * All rights reserved
 ******************************************************************************/
package com.indra.sofia2.support.util.plugins.interfaces.audit;

import java.util.Date;

import com.indra.sofia2.ssap.ssap.SSAPMessage;
import com.indra.sofia2.support.entity.gestion.dominio.Script;
import com.indra.sofia2.support.util.plugins.dto.audit.AuditDirection;
import com.indra.sofia2.support.util.plugins.dto.audit.AuditOperation;
import com.indra.sofia2.support.util.plugins.dto.audit.AuditType;
import com.indra.sofia2.support.util.plugins.dto.audit.ClientGatewayData;

public interface AuditPlugin {

	void audit(SSAPMessage<?> message, String cipherKey, Date timeStamp, ClientGatewayData client, AuditDirection direction, AuditType type);
	void audit(Script script, Date timeStamp, AuditOperation operation, String user, AuditType type);
	void audit(String scriptId, String ontology, String ontologyName, Date timeStamp, AuditOperation operation, String sessionKey, AuditType type);
	
}
