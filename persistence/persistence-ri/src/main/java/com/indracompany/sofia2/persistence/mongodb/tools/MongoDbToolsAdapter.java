/*******************************************************************************
 * Â© Indra Sistemas, S.A.
 * 2013 - 2018  SPAIN
 * 
 * All rights reserved
 ******************************************************************************/
package com.indracompany.sofia2.persistence.mongodb.tools;

import com.indracompany.sofia2.persistence.util.commands.CommandExecutionException;

/**
 * A component that interacts with the MongoTools stack (mongoexport, mongoimport,
 * mongoshell) using the Java 1.6 process API.
 * 
 * @see MongoDbConnectorQueryTests to view some usage examples
 */
public abstract class MongoDbToolsAdapter {

	/**
	 * Runs a MongoDB script on the given database
	 * 
	 * @param database
	 * @param path
	 * @throws CommandExecutionException
	 */
	public abstract String runMongoDbScript(String database, String path, String args) throws CommandExecutionException;
	
	/**
	 * Runs a mongoshell command on the given database
	 * @param database
	 * @param args
	 * @return
	 * @throws CommandExecutionException
	 */
	public abstract String runMongoshellCommand(String database, String args) throws CommandExecutionException;

	/**
	 * Runs a mongoexport command on the given database
	 * 
	 * @param collection
	 * @param query
	 * @param outputFile
	 * @return
	 * @throws CommandExecutionException
	 */
	public abstract String runMongoExportCommand(String database, String collection, String query, String outputFile)
			throws CommandExecutionException;
	
	/**
	 * Runs a mongoexport command on the given database
	 * 
	 * @param database
	 * @param collection
	 * @param query
	 * @param outputFile
	 * @param flagLimit
	 * @param limit
	 * @return
	 * @throws CommandExecutionException
	 */
	public abstract String runMongoExportCommand(String database, String collection, String query, String outputFile, Boolean flagLimit, Integer limit)
			throws CommandExecutionException;
	
	

	/**
	 * Runs a mongoimport command on the given database
	 * @param database
	 * @param collection
	 * @param inputFile
	 * @return
	 * @throws CommandExecutionException
	 */
	public abstract String runMongoImportCommand(String database, String collection, String inputFile) throws CommandExecutionException;
	
	/**
	 * Runs a mongoimport command on the given database
	 * @param database
	 * @param collection
	 * @param inputFile
	 * @return
	 * @throws CommandExecutionException
	 */
	public abstract String runMongoImportCommand(String database, String collection, String inputFile, String accion) throws CommandExecutionException;
	
	
	public static String getInnerArgDelimiter() {
		if (System.getProperty("os.name").contains("Windows")) {
			return "'";
		} else {
			return "\"";
		}
	}

	public static String getShellArgDelimiter() {
		if (System.getProperty("os.name").contains("Windows")) {
			return "\"";
		} else {
			return "";
		}
	}

	
}
