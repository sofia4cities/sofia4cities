package com.indracompany.sofia2.config.services.ontologydata;

import java.io.IOException;
import java.util.Iterator;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jackson.JsonLoader;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.LogLevel;
import com.github.fge.jsonschema.core.report.ProcessingMessage;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import com.indracompany.sofia2.config.model.Ontology;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class OntologyDataServiceImpl implements OntologyDataService {
	
	@Override
	public boolean hasOntologySchemaCompliance(final String data, final Ontology ontology)  {
		final String jsonSchema = ontology.getJsonSchema();
		return isJsonCompliantWithSchema(data, jsonSchema);
	}
		
	boolean isJsonCompliantWithSchema(final String dataString, final String schemaString) {
		JsonNode dataJson;
		JsonNode schemaJson;
		JsonSchema schema;
		ProcessingReport report;
		try {
			dataJson = JsonLoader.fromString(dataString);
			schemaJson = JsonLoader.fromString(schemaString);
			final JsonSchemaFactory factory = JsonSchemaFactory.byDefault();
			schema = factory.getJsonSchema(schemaJson);
			report = schema.validate(dataJson);
		} catch (IOException e) {
			log.error("Error reading data for cheaking schema compliance",e);
			return false;
		} catch (ProcessingException e) {
			log.error("Error checking data schema compliance",e);
			return false;
		}
		
		if (report != null && !report.isSuccess()) {
			Iterator<ProcessingMessage> it = report.iterator();
			StringBuffer msgerror = new StringBuffer();
			while (it.hasNext()) {
				ProcessingMessage msg = it.next();
				if (msg.getLogLevel().equals(LogLevel.ERROR)) {
					msgerror.append(msg.asJson());
				}
			}
			
			log.error(msgerror.toString());
		}
		
		return report.isSuccess();

	}

}
