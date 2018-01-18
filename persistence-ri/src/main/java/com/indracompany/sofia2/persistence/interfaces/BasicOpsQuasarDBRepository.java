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
package com.indracompany.sofia2.persistence.interfaces;

import com.indracompany.sofia2.persistence.quasar.connector.dto.QuasarResponseDTO;
import com.indracompany.sofia2.ssap.SSAPQueryResultFormat;

public interface BasicOpsQuasarDBRepository {
	
	public static final String ACCEPT_TEXT_CSV="text/csv";
	public static final String ACCEPT_APPLICATION_JSON="application/json";
	
	public QuasarResponseDTO executeQuery(String query, int offset, SSAPQueryResultFormat resultType/*, UserCDB user*/, String formatter) throws Exception;
}
