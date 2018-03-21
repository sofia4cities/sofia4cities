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
package com.indracompany.sofia2.ssap.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.indracompany.sofia2.ssap.binary.Base64;
import com.indracompany.sofia2.ssap.binary.Encoder;
import com.indracompany.sofia2.ssap.binary.Encoding;
import com.indracompany.sofia2.ssap.binary.Mime;
import com.indracompany.sofia2.ssap.binary.Storage;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

public class BinarySerializer {

	// Method for Base64 encoding
	public JsonNode getJsonBinary(String fieldName, File file, Mime mime) throws FileNotFoundException, IOException {

		ObjectMapper mapper = new ObjectMapper();
		JsonNode returnNode = mapper.createObjectNode();
		JsonNode binaryNode = mapper.createObjectNode();
		JsonNode mediaNode = mapper.createObjectNode();
		Encoder base64 = new Base64();

		String data = base64.encode(IOUtils.toByteArray(new FileInputStream(file)));
		((ObjectNode) mediaNode).put("binaryEncoding", Encoding.Base64.name());
		((ObjectNode) mediaNode).put("mime", mime.name());
		((ObjectNode) mediaNode).put("name", file.getName());
		((ObjectNode) mediaNode).put("storageArea", Storage.SERIALIZED.name());

		((ObjectNode) binaryNode).put("data", data);
		((ObjectNode) binaryNode).set("media", mediaNode);
		

		((ObjectNode) returnNode).set(fieldName, binaryNode);

		return returnNode;
	}

	public byte[] binaryJsonToFile(JsonNode binaryNode) {
		Encoder base64 = new Base64();

		String data = binaryNode.get("data").asText();
		String binaryEnconding = binaryNode.get("media").get("binaryEnconding").asText();
		String mime = binaryNode.get("media").get("mime").asText();
		String name = binaryNode.get("media").get("name").asText();
		String storageArea = binaryNode.get("media").get("storageArea").asText();

		if (binaryEnconding.equals(Encoding.Base64.name())) {
			return base64.decode(data);
		} else {
			return null;
		}

	}
	public void binaryJsonToFile(JsonNode binaryNode, String path) throws IOException {
		Encoder base64 = new Base64();

		String data = binaryNode.get("data").asText();
		String binaryEnconding = binaryNode.get("media").get("binaryEnconding").asText();
		String mime = binaryNode.get("media").get("mime").asText();
		String name = binaryNode.get("media").get("name").asText();
		String storageArea = binaryNode.get("media").get("storageArea").asText();

		if (binaryEnconding.equals(Encoding.Base64.name())) 
			FileUtils.writeByteArrayToFile(new File(path+name), base64.decode(data)); 
		
	}

}
