/*******************************************************************************
 * Â© Indra Sistemas, S.A.
 * 2013 - 2018  SPAIN
 * 
 * All rights reserved
 ******************************************************************************/
package com.indracompany.sofia2.persistence.mongodb.gridfs;

import java.io.InputStream;

import javax.persistence.PersistenceException;

import org.bson.types.ObjectId;

import com.mongodb.client.gridfs.GridFSDownloadStream;

public interface GridFSConnector {
	/**
	 * Uploads a file to the GridFS filesystem.
	 * 
	 * @param database
	 * @param stream
	 * @param metadata
	 * @return The unique identifier of the file
	 */
	public ObjectId uploadGridFsFile(String database, InputStream stream, String metadata) throws PersistenceException;

	/**
	 * Updates a file stored in the GridFS filesystem.
	 * 
	 * @param database
	 * @param fileId
	 * @param stream
	 * @param metadata
	 * @throws PersistenceException
	 */
	public void updateGridFsFile(String database, ObjectId fileId, InputStream stream, String metadata)
			throws PersistenceException;

	/**
	 * Deletes a file stored in the GridFS filesystem.
	 * 
	 * @param database
	 * @param fileId
	 * @throws PersistenceException
	 */
	public void removeGridFsFile(String database, ObjectId fileId) throws PersistenceException;

	/**
	 * Reads a file stored in the GridFS filesystem.
	 * 
	 * @param database
	 * @param fileId
	 * @return
	 * @throws PersistenceException
	 */
	public GridFSDownloadStream readGridFsFile(String database, ObjectId fileId) throws PersistenceException;
}
