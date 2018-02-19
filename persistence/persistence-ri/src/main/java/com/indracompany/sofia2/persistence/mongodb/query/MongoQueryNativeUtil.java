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
/*******************************************************************************
 * Â© Indra Sistemas, S.A.
 * 2013 - 2018  SPAIN
 * 
 * All rights reserved
 ******************************************************************************/
package com.indracompany.sofia2.persistence.mongodb.query;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import javax.persistence.PersistenceException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.indracompany.sofia2.persistence.mongodb.template.MongoDbTemplate;
import com.mongodb.BasicDBObject;

@Component
@Lazy
public class MongoQueryNativeUtil {

	private static final int BUFSIZE = 1024;

	@Autowired
	private MongoDbTemplate mongoDbConnector;
	
	public MFindQuery parseFindQuery(String database, String findQueryStr) throws PersistenceException {
		String myFindQueryStr = findQueryStr;
		String collName = getCollNameFromAction( myFindQueryStr, "find" );
		myFindQueryStr = myFindQueryStr.replaceFirst( "db." + collName, "a" );

		try {
			//FIXME -- Ver como resuelve para la inyección de código y 
			//volver a sacar esto a una variable statica para que no se haga el acceso al fichero cada query 
			String jsStr = streamToString( getResourceStream( "/nativedb/mongodb/parseFindQuery.txt" ) );
			jsStr = MStringUtil.replaceFirst( jsStr, "//_QUERY_", myFindQueryStr );
			BasicDBObject result = (BasicDBObject) mongoDbConnector.eval(database, jsStr, null);

			result.remove( "find" );
			result.remove( "limit" );
			result.remove( "skip" );
			result.remove( "sort" );

			MFindQuery fq = new MFindQuery( findQueryStr, result.toMap() );
			fq.setCollName( collName );
			return fq;
		} catch (IOException e) {
			throw new PersistenceException(e);
		} 
	}
	
	public static String getCollNameFromAction(String actionStr, String actionName) {
		if (actionStr.matches("^db\\[\\s*'.*")) {
			String s = MRegEx.getMatch( "^[^']+'([^']+)'", actionStr );
			return s;
		} else {
			//db.service.find() -> service
			return MRegEx.getMatchIgnoreCase( "^db\\.([^\\(]+)\\." + actionName + "\\(", actionStr );
		}
	}
	
	public InputStream getResourceStream(String resource, ClassLoader classLoader) throws IOException {
		Class<?> targetClass = MongoQueryNativeUtil.class;
		URL url = targetClass.getResource(resource);
		return url.openStream();
	}

	public InputStream getResourceStream( String resource ) throws IOException {
		return  getResourceStream( resource, null);
	}
	
	public String streamToString( InputStream in ) throws IOException {
		String str = null;
		try{
			str = new String( streamToBytes( in ), MCharset.CS_ISO_8859_1 );
		}catch( UnsupportedEncodingException e ){
			e.printStackTrace();
		}
		return str;
	}
	
	public byte[] streamToBytes( InputStream in ) throws IOException	{
		ByteArrayOutputStream out = new ByteArrayOutputStream( BUFSIZE );
		connectStream( in, out );
		return out.toByteArray();
	}
	
	public int connectStream( InputStream in, OutputStream out, boolean closeOut )throws IOException {
		try{
			int totalSize = 0;
			int readSize;
			byte[] buffer = new byte[ BUFSIZE ];
			while( true )
				{
				readSize = in.read( buffer );
				if( readSize <= 0 )
					{
					break;
					}
				totalSize += readSize;
				out.write( buffer, 0, readSize );
				}
			return totalSize;
		} finally {
			closeStream( in );
			if( closeOut ) {
				closeStream( out );
			}
		}
	}
	
	public int connectStream( InputStream in, OutputStream out ) throws IOException {
		return connectStream( in, out, false );
	}
	
	public void closeStream( InputStream in ){
		try	{
			if( in != null ){
				in.close();	
			}
		}catch( IOException e )	{
			e.printStackTrace();
		}
	}
	
	public void closeStream( OutputStream out ){
		if( out == null ){
			return;
		}
		try{
			out.flush();
		}catch( IOException e )	{
			e.printStackTrace();
		}
		try	{
			out.close();
		}catch( IOException e )	{
			e.printStackTrace();
		}
	}
	
	public java.util.List<String> getNameListFromDataList(java.util.List<?> dataList) {
		java.util.List<String> nameList = new ArrayList<String>();
		for (int i = 0; i < dataList.size(); ++i) {
			Map<?, ?> data = ((BasicDBObject) dataList.get(i)).toMap();
			Iterator<?> p = data.keySet().iterator();
			while (p.hasNext()) {
				String key = (String) p.next();
				if (!nameList.contains(key)) {
					nameList.add(key);
				}
			}
		}
		return nameList;
	}
}
