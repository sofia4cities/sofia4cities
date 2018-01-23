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
package com.indra.sofia2.support.util.json;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;

import junit.framework.TestCase;

import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jackson.JsonLoader;

public class JsonSchemaPlannerTest extends TestCase{
	
	private static final String SCHEMA_DIR = File.separator + "META-INF" + File.separator
			+ "json-schemas";
	
	private final static String[] targetFiles = {"feedDesigual.json"};
	
	private final static HashSet<String> targetFilesSet = new HashSet<String>(Arrays.asList(targetFiles));

	private JsonSchemaPlanner schemaProcessor;
	
	@Override
	public void setUp(){
		schemaProcessor = new JsonSchemaPlanner();
	}
	
	@Test
	public void testJsonSchemaPlanner() throws IOException{
		File src_dir = new File(System.getProperty("user.dir") + File.separator + "src"
				+ File.separator + "test" + File.separator + "resources" + File.separator
				+ SCHEMA_DIR);
		File[] schemas = src_dir.listFiles();
		for (int i = 0; i < schemas.length; i++) {
			if (targetFilesSet.size() != 0 && !targetFilesSet.contains(schemas[i].getName())){
				System.out.println("Excluding schema " + schemas[i].getName());
				continue;
			}
			processOntologySchema(schemas[i]);
		}
		assertTrue(true);
	}
	
	private OntologySchema processOntologySchema(File sourceFile) throws IOException {
		printSeparator();
		printText("Generating ontology map for schema " + sourceFile.getName());
		JsonNode schema = JsonLoader.fromFile(sourceFile);		
		OntologySchema map = schemaProcessor.buildOntologySchema(schema.toString());
		printOntologyMap(map);
		return map;
	}
	
	private void printOntologyMap(OntologySchema ontologyMap) {
		for (String key : ontologyMap.getAttributeNames()) {
			System.out.println(key + " ---> " + ontologyMap.getAttributeValue(key));
		}
	}

	private void printText(String text){
		System.out.println();
		System.out.println(text);
		System.out.println();
	}

	private void printSeparator() {
		System.out.println();
		System.out.println();
		System.out.println("####################################################");
		System.out.println();
		System.out.println();
	}	
}
