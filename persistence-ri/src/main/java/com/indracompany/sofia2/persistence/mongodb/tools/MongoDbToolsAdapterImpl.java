/*******************************************************************************
 * Â© Indra Sistemas, S.A.
 * 2013 - 2018  SPAIN
 * 
 * All rights reserved
 ******************************************************************************/
package com.indracompany.sofia2.persistence.mongodb.tools;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.indracompany.sofia2.persistence.mongodb.config.MongoDbCredentials;
import com.indracompany.sofia2.persistence.mongodb.template.MongoDbTemplate;
import com.indracompany.sofia2.persistence.util.commands.CommandExecutionException;
import com.indracompany.sofia2.persistence.util.commands.CommandsLauncher;
import com.mongodb.ServerAddress;

import lombok.extern.slf4j.Slf4j;

@Component
@Lazy
@Slf4j
public class MongoDbToolsAdapterImpl extends MongoDbToolsAdapter {


	@Autowired
	private MongoDbCredentials credentials;

	@Autowired
	private MongoDbTemplate mongoDbConnector;

	@Value("${sofia2.rtdb.mongodb.tools.mongoexport.path:mongoexport}")
	private String mongoexportPath;
	@Value("${sofia2.rtdb.mongodb.tools.mongoimport.path:mongoimport}")
	private String mongoimportPath;
	@Value("${sofia2.rtdb.mongodb.tools.mongoshell.path:mongo}")
	private String mongoshellPath;
	@Value("${sofia2.rtdb.mongodb.tools.useSequentialOps:true}")
	private boolean useSequentialOps;

	@Override
	public String runMongoDbScript(String database, String path, String args) throws CommandExecutionException {
		log.info("Running MongoDB script. Path = {}.", path);
		String command = buildMongoshellCommand(database, path + " " + args);
		String output = runCommand(command);
		log.info("The MongoDB script has been executed. Path = {}, output = {}.", path, output);
		return output;
	}

	@Override
	public String runMongoshellCommand(String database, String args) throws CommandExecutionException {
		log.info("Running mongoshell command. Database = {}, args = {}.", database, args);
		String command = buildMongoshellCommand(database, args);
		String output = runCommand(command);
		log.info("The mongoshell command has been executed. Database = {}, args = {}, output = {}.", database, args,
				output);
		return output;
	}

	@Override
	public String runMongoExportCommand(String database, String collection, String query, String outputFile)
			throws CommandExecutionException {
		String sentence = buildMongoexportCommand(database, collection, query, outputFile,Boolean.FALSE,Integer.valueOf(0));
		log.info("Executing mongoexport command. Database = {}, collection = {}, query = {}, outputFile = {}.",
				database, collection, query, outputFile);
		String output = runCommand(sentence);
		log.info(
				"The mongoexport command has been executed. Database = {}, collection = {}, query = {}, outputFile = {}, output = {}.",
				database, collection, query, outputFile, output);
		return output;
	}
	
	@Override
	public String runMongoExportCommand(String database, String collection, String query, String outputFile, Boolean flagLimit, Integer limit)
			throws CommandExecutionException {
		String sentence = buildMongoexportCommand(database, collection, query, outputFile,flagLimit,limit);
		log.info("Executing mongoexport command. Database = {}, collection = {}, query = {}, outputFile = {}.",
				database, collection, query, outputFile);
		String output = runCommand(sentence);
		log.info(
				"The mongoexport command has been executed. Database = {}, collection = {}, query = {}, outputFile = {}, output = {}.",
				database, collection, query, outputFile, output);
		return output;
	}

	private String buildAuthenticationArgs() {
		String authString = "";
		if (credentials.isEnableMongoDbAuthentication()) {
			authString = " -u " + credentials.getUsername() + " -p " + credentials.getPassword()
					+ " --authenticationDatabase " + credentials.getAuthenticationDatabase();
		}
		return authString;
	}

	private String buildMongoshellCommand(String database, String args) {
		StringBuilder sentence = new StringBuilder(mongoshellPath + " " + database);
		ServerAddress serverAddress = mongoDbConnector.getReplicaSetMaster();
		sentence.append(" --host " + serverAddress.getHost());
		sentence.append(" --port " + serverAddress.getPort());
		sentence.append(" " + buildAuthenticationArgs());
		sentence.append(" " + args);
		return sentence.toString();
	}

	private String runCommand(String commandToExecute) throws CommandExecutionException {
		log.debug("Executing command. Command = {}.", commandToExecute);
		String result = null;
		if (useSequentialOps) {
			synchronized (this) {
				result = CommandsLauncher.executeCommand(commandToExecute);
			}
		} else {
			result = CommandsLauncher.executeCommand(commandToExecute);
		}
		return result;
	}

	/**
	 * 
	 * @param database
	 * @param collection
	 * @param query
	 * @param outputFile
	 * @param flagLimit
	 * @param limit
	 * @return
	 */
	private String buildMongoexportCommand(String database, String collection, String query, String outputFile, Boolean flagLimit, Integer limit) {
		StringBuilder sentence = new StringBuilder();
		sentence.append(mongoexportPath + " --db " + database);
		ServerAddress masterAddress = mongoDbConnector.getReplicaSetMaster();
		sentence.append(" -h " + masterAddress.getHost());
		sentence.append(" --port " + masterAddress.getPort());
		sentence.append(" " + buildAuthenticationArgs());
		sentence.append(" -c " + collection + " -o " + getShellArgDelimiter() + outputFile + getShellArgDelimiter());
		
		if(null!=query && !query.isEmpty()){
			sentence.append(" -q " + query); 
		}

		if(Boolean.TRUE.equals(flagLimit)){
			 /**
			  * Ordenado descendientemente para quedarse con los ultimos registros insertados
			  */
			sentence.append(" --sort " + "{_id:-1}" + " --limit " + limit + " ");	
		}
		
		return sentence.toString();
	}

	@Override
	public String runMongoImportCommand(String database, String collection, String inputFile)
			throws CommandExecutionException {
		String command = buildMongoimportCommand(database, collection, inputFile,null);
		log.info("Executing mongoimport command. Database = {}, collection = {}, inputFile = {}.", database,
				collection, inputFile);
		String output = runCommand(command);
		log.info(
				"The mongoimport command has been executed. Database = {}, collection = {}, inputFile = {}, output = {}.",
				database, collection, inputFile, output);
		return output;
	}
	
	@Override
	public String runMongoImportCommand(String database, String collection, String inputFile, String accion)
			throws CommandExecutionException {
		String command = buildMongoimportCommand(database, collection, inputFile,accion);
		log.info("Executing mongoimport command. Database = {}, collection = {}, inputFile = {}.", database,
				collection, inputFile);
		String output = runCommand(command);
		log.info(
				"The mongoimport command has been executed. Database = {}, collection = {}, inputFile = {}, output = {}.",
				database, collection, inputFile, output);
		return output;
	}

	/**
	 * 
	 * @param database
	 * @param collection
	 * @param inputFile
	 * @param accion
	 * @return
	 */
	private String buildMongoimportCommand(String database, String collection, String inputFile, String accion) {
		StringBuilder sentence = new StringBuilder(mongoimportPath + " --db " + database);
		ServerAddress masterAddress = mongoDbConnector.getReplicaSetMaster();
		sentence.append(" -h " + masterAddress.getHost());
		sentence.append(" --port " + masterAddress.getPort());
		sentence.append(" --collection " + collection);
		sentence.append(" " + buildAuthenticationArgs());
		sentence.append(" --file " + getShellArgDelimiter() + inputFile + getShellArgDelimiter());
		
		if(null!=accion && !accion.isEmpty()){
			sentence.append( accion );
		}
			
		return sentence.toString();
	}
}
