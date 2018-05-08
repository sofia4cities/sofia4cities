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
package com.indracompany.sofia2.persistence;

import java.util.ArrayList;
import java.util.List;

import com.indracompany.sofia2.persistence.common.DescribeColumnData;
import com.indracompany.sofia2.persistence.hadoop.json.JsonGeneratorFromHive;
import com.indracompany.sofia2.persistence.hadoop.json.JsonSchemaHive;
import com.indracompany.sofia2.persistence.hadoop.util.HiveFieldType;

public class Test {

	public static void main(String[] args) {

		List<DescribeColumnData> columns = new ArrayList<>();

		columns.add(createDescribeColumnd("province", HiveFieldType.STRING_FIELD));
		columns.add(createDescribeColumnd("id", HiveFieldType.FLOAT_FIELD));
		columns.add(createDescribeColumnd("measuresTimestamp", HiveFieldType.TIMESTAMP_FIELD));

		JsonSchemaHive json = new JsonGeneratorFromHive().parse("testhive", columns);

		System.out.println(json.build());
	}

	public static DescribeColumnData createDescribeColumnd(String colName, String dataType) {
		DescribeColumnData data = new DescribeColumnData();
		data.setDataType(dataType);
		data.setColName(colName);
		return data;
	}

}
