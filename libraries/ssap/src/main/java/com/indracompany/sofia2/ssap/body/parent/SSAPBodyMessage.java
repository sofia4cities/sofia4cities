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
package com.indracompany.sofia2.ssap.body.parent;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.indracompany.sofia2.ssap.body.SSAPBodyDeleteByIdMessage;
import com.indracompany.sofia2.ssap.body.SSAPBodyDeleteMessage;
import com.indracompany.sofia2.ssap.body.SSAPBodyEmptyMessage;
import com.indracompany.sofia2.ssap.body.SSAPBodyInsertMessage;
import com.indracompany.sofia2.ssap.body.SSAPBodyJoinMessage;
import com.indracompany.sofia2.ssap.body.SSAPBodyLeaveMessage;
import com.indracompany.sofia2.ssap.body.SSAPBodyQueryMessage;
import com.indracompany.sofia2.ssap.body.SSAPBodyReturnMessage;
import com.indracompany.sofia2.ssap.body.SSAPBodyUpdateByIdMessage;
import com.indracompany.sofia2.ssap.body.SSAPBodyUpdateMessage;

@JsonTypeInfo(use=Id.NAME)
@JsonSubTypes({
	@JsonSubTypes.Type(SSAPBodyDeleteByIdMessage.class),
	@JsonSubTypes.Type(SSAPBodyDeleteMessage.class),
	@JsonSubTypes.Type(SSAPBodyEmptyMessage.class),
	@JsonSubTypes.Type(SSAPBodyInsertMessage.class),
	@JsonSubTypes.Type(SSAPBodyJoinMessage.class),
	@JsonSubTypes.Type(SSAPBodyLeaveMessage.class),
	@JsonSubTypes.Type(SSAPBodyQueryMessage.class),
	@JsonSubTypes.Type(SSAPBodyReturnMessage.class),
	@JsonSubTypes.Type(SSAPBodyUpdateByIdMessage.class),
	@JsonSubTypes.Type(SSAPBodyUpdateMessage.class)
})
public abstract class SSAPBodyMessage {

	public abstract boolean isSessionKeyMandatory();

	public abstract boolean isOntologyMandatory();

}
