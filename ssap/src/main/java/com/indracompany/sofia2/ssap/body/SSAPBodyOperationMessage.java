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
package com.indracompany.sofia2.ssap.body;

import com.indracompany.sofia2.ssap.SSAPQueryResultFormat;
import com.indracompany.sofia2.ssap.SSAPQueryType;
import com.indracompany.sofia2.ssap.body.parent.SSAPBodyMessage;

public class SSAPBodyOperationMessage extends SSAPBodyMessage {

	private String query;
	private SSAPQueryType queryType;
	private SSAPQueryResultFormat resultFormat;

	public SSAPQueryType getQueryType() {
		return queryType;
	}

	public void setQueryType(SSAPQueryType queryType) {
		this.queryType = queryType;
	}

	public SSAPQueryResultFormat getResultFormat() {
		return resultFormat;
	}

	public void setResultFormat(SSAPQueryResultFormat resultFormat) {
		this.resultFormat = resultFormat;
	}

	@Override
	public boolean isClientPlatformMandatory() {
		return true;
	}

	@Override
	public boolean isSessionKeyMandatory() {
		return true;
	}

	@Override
	public boolean isAutorizationMandatory() {
		return true;
	}

	@Override
	public boolean isOntologyMandatory() {
		return true;
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

}
