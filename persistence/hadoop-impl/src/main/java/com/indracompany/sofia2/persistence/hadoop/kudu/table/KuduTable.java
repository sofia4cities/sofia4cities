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
package com.indracompany.sofia2.persistence.hadoop.kudu.table;

import java.util.ArrayList;
import java.util.List;

import com.indracompany.sofia2.persistence.hadoop.hive.table.HiveColumn;
import com.indracompany.sofia2.persistence.hadoop.util.JsonFieldType;

import lombok.Getter;
import lombok.Setter;

public class KuduTable {

	@Getter
	@Setter
	private String name;

	@Getter
	@Setter
	private List<HiveColumn> columns = new ArrayList<>();

	public String build() {
		StringBuilder sentence = new StringBuilder();

		sentence.append("CREATE TABLE IF NOT EXISTS ");
		sentence.append(name);
		sentence.append(" (");

		if (columns != null && !columns.isEmpty()) {
			int numOfColumns = columns.size();
			int i = 0;

			for (HiveColumn column : columns) {
				sentence.append(column.getName()).append(" ").append(column.getColumnType());
				if (i < numOfColumns - 1) {
					sentence.append(", ");
				}
				i++;
			}
		}
		sentence.append(",\n PRIMARY KEY (" + JsonFieldType.PRIMARY_ID_FIELD + ")");
		sentence.append(") PARTITION BY HASH(" + JsonFieldType.PRIMARY_ID_FIELD + ") PARTITIONS 2");
		sentence.append(" STORED AS KUDU ");
		sentence.append("TBLPROPERTIES(");
		sentence.append("'kudu.master_addresses' = 'localhost:7051',");
		sentence.append("'kudu.table_name' = '" + name + "',");
		sentence.append("'kudu.num_tablet_replicas' = '1'");
		sentence.append(");");

		return sentence.toString();
	}

}
