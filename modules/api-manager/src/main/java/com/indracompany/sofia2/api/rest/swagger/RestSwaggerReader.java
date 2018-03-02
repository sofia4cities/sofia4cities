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
package com.indracompany.sofia2.api.rest.swagger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.ws.rs.core.MediaType;

import org.apache.commons.lang3.SerializationUtils;

import com.indracompany.sofia2.api.rest.api.dto.ApiDTO;
import com.indracompany.sofia2.api.rest.api.dto.ApiHeaderDTO;
import com.indracompany.sofia2.api.rest.api.dto.ApiQueryParameterDTO;
import com.indracompany.sofia2.api.rest.api.dto.OperacionDTO;

import io.swagger.jaxrs.config.BeanConfig;
import io.swagger.models.Contact;
import io.swagger.models.Info;
import io.swagger.models.License;
import io.swagger.models.Operation;
import io.swagger.models.Path;
import io.swagger.models.Response;
import io.swagger.models.Scheme;
import io.swagger.models.Swagger;
import io.swagger.models.Tag;
import io.swagger.models.parameters.Parameter;

public class RestSwaggerReader {

	public static String SWAGGER_VERSION = "2.0";

	public static String INFO_VERSION = "Apache 2.0 License";
	public static String INFO_TITLE = "Sofia2Open API Manager";
	public static String INFO_DESCRIPTION = "The API MANAGER DESCRIPTION";

	public static String LICENSE_NAME = "1.0.0";
	public static String LICENSE_URL = "http://www.apache.org/licenses/LICENSE-2.0.html";
	//public static String BASE_PATH = "/api";

	public static String CONTACT_NAME = "The Sofia2Open team";
	public static String CONTACT_URL = "http://sofia2.com";
	public static String CONTACT_EMAIL = "ljsantos@indra.es";

	public static String SCHEMES = "http";

	public static String XSOFIA2APIKey = "X-SOFIA2-APIKey";
	public static String XSOFIAEXTENSION = "x-sofia2-extension";

	static List<String> PRODUCES = new ArrayList<String>(
			Arrays.asList(MediaType.APPLICATION_JSON, MediaType.APPLICATION_ATOM_XML, MediaType.TEXT_PLAIN));
	static List<String> CONSUMES = new ArrayList<String>(Arrays.asList(MediaType.APPLICATION_JSON));

	static Map<String, Response> responses = new HashMap<String, Response>();
	static List<Scheme> schemes = new ArrayList<Scheme>();

	static {

		Response r1 = new Response();
		r1.setDescription("No Content");
		responses.put("204", r1);

		Response r2 = new Response();
		r2.setDescription("Bad Request");
		responses.put("400", r2);

		Response r3 = new Response();
		r3.setDescription("Unauthorized");
		responses.put("401", r3);

		Response r4 = new Response();
		r4.setDescription("Internal Server Error");
		responses.put("501", r4);

		Response r5 = new Response();
		r5.setDescription("OK");
		responses.put("200", r5);

		schemes.add(Scheme.HTTP);

		/*
		 * Parameter sofia2Header = new HeaderParameter();
		 * sofia2Header.setDescription(XSOFIA2APIKey);
		 * sofia2Header.setName(XSOFIA2APIKey); sofia2Header.setRequired(false);
		 * sofia2Header.ty serializableParameter.setItems(new StringProperty());
		 * 
		 * { "name": "X-SOFIA2-APIKey", "in": "header", "required": false, "type":
		 * "string" }
		 */
	}

	public Swagger read(ApiDTO apiDto, BeanConfig config) {
		Swagger swagger = new Swagger();

		Info info = new Info();
		info.setDescription(INFO_DESCRIPTION);
		License license = new License();
		license.setName(LICENSE_NAME);
		license.setUrl(LICENSE_URL);

		info.setLicense(license);

		info.setTitle(INFO_TITLE);
		info.setVersion(INFO_VERSION);

		Contact contact = new Contact();
		contact.setName(CONTACT_NAME);
		contact.setUrl(CONTACT_URL);
		contact.setEmail(CONTACT_EMAIL);
		info.setContact(contact);
		swagger.setInfo(info);

		swagger.setConsumes(CONSUMES);
		swagger.setProduces(PRODUCES);
		swagger.setResponses(responses);

		int version = apiDto.getVersion();
		String vVersion="v"+version;
		String identification = apiDto.getIdentification();
		
		swagger.setBasePath("/server/api"+"/"+vVersion+"/"+identification);

		swagger.setSchemes(schemes);

		List<Tag> tags = new ArrayList<Tag>();
		Tag tag = new Tag();
		tag.setName(apiDto.getIdentification());
		tags.add(tag);
		swagger.setTags(tags);

		// swagger.setDefinitions(definitions);

		swagger.setVendorExtension(XSOFIAEXTENSION, populateApiDTOLite(apiDto));

		/*
		 * Map<String, Model> definitions=new HashMap<String, Model>();
		 * 
		 * swagger.setDefinitions(definitions);
		 * 
		 * for (String type : types) { Class<?> clazz =
		 * classResolver.resolveClass(type); RestSwaggerReaderHelper.appendModels(clazz,
		 * swagger); }
		 * 
		 * 
		 * RestSwaggerReaderHelper.appendModels(String.class, swagger);
		 */

		ArrayList<OperacionDTO> operations = apiDto.getOperations();

		for (OperacionDTO operacionDTO : operations) {
			parse(swagger, operacionDTO);
		}

		// configure before returning
		// swagger = config.configure(swagger);
		return swagger;
	}

	private ApiDTO populateApiDTOLite(ApiDTO apidto) {
		ApiDTO api2 = SerializationUtils.clone(apidto);
		api2.setOperations(null);
		api2.setAuthentication(null);
		return api2;
	}

	private void parse(Swagger swagger, OperacionDTO operacionDTO) {

		String description = operacionDTO.getDescription();
		String endpoint = operacionDTO.getEndpoint();
		ArrayList<ApiHeaderDTO> headers = operacionDTO.getHeaders();
		String identification = operacionDTO.getIdentification();
		String operation = operacionDTO.getOperation().name();
		String path = operacionDTO.getPath();
		if (!path.startsWith("/"))
			path = "/" + path;
		ArrayList<ApiQueryParameterDTO> queryParams = operacionDTO.getQueryParams();

		Path swaggerPath = swagger.getPath(path);
		if (swaggerPath == null) {
			swaggerPath = new Path();
			swagger.path(path, swaggerPath);
		}

		Operation op = new Operation();
		op.operationId(description.replaceAll(" ", "_"));

		String method = operation.toLowerCase(Locale.US);
		Parameter sofia2Api = RestSwaggerReaderHelper.populateParameter(swagger, "X-SOFIA2-APIKey", "X-SOFIA2-APIKey",
				false, "header", "string", null, null);
		op.addParameter(sofia2Api);
		op.setConsumes(CONSUMES);
		op.setProduces(PRODUCES);
		op.setResponses(responses);

		swaggerPath = swaggerPath.set(method, op);

		for (ApiQueryParameterDTO apiQueryParameterDTO : queryParams) {

			String desc = apiQueryParameterDTO.getDescription();
			String name = apiQueryParameterDTO.getName();
			String type = apiQueryParameterDTO.getDataType().name();
			String value = apiQueryParameterDTO.getValue();
			String condition = apiQueryParameterDTO.getHeaderType().name();

			Parameter parameter = RestSwaggerReaderHelper.populateParameter(swagger, name, description, false,
					condition, type, null, value);
			op.addParameter(parameter);
		}

	}

}