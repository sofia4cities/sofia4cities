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
package com.indracompany.sofia2.controlpanel.services.hadoop;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.indracompany.sofia2.config.model.Ontology.RtdbDatasource;
import com.indracompany.sofia2.persistence.common.DescribeColumnData;
import com.indracompany.sofia2.persistence.hadoop.json.JsonGeneratorFromHive;
import com.indracompany.sofia2.persistence.hadoop.json.JsonSchemaHive;
import com.indracompany.sofia2.persistence.services.ManageDBPersistenceServiceFacade;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class HadoopService {

	@Autowired
	private ManageDBPersistenceServiceFacade manageDBPersistenceServiceFacade;

	@Autowired
	private JsonGeneratorFromHive jsonGenerator;

	public List<String> getHiveTables() {
		return manageDBPersistenceServiceFacade.getListOfTables(RtdbDatasource.Hadoop);
	}

	public List<DescribeColumnData> describe(String name) {
		List<DescribeColumnData> columns = manageDBPersistenceServiceFacade.describeTable(RtdbDatasource.Hadoop, name);
		return columns;
	}

	public String generateSchemaFromHive(String tablename) {
		List<DescribeColumnData> columns = describe(tablename);
		JsonSchemaHive schema = jsonGenerator.parse(tablename, columns);
		return schema.build();
	}
}
