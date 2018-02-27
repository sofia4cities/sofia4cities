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

public final class MStringUtil{
	
	public static int parseInt( Object o, int defaultValue ) {
		return parseInt( o + "", defaultValue );
	}
	
	public static int parseInt( Object o ) {
		if( o instanceof Double )
			{
			return ( ( Double )o ).intValue();
			}
		else if( o instanceof Integer )
			{
			return ( ( Integer)o ).intValue();
			}
		return parseInt( o + "" );
	}
	
	public static int parseInt( String s )	{
		return parseInt( s, 0 );
	}
	
	public static int parseInt( String s, int defaultValue ) {
		int i;
		try 	{
			i = Integer.parseInt( s );
			return i;
		}
		catch( NumberFormatException e ) {
			return defaultValue;
		}
	}
	
	public static String replaceFirst( String target, String from, String to ){
		StringBuffer s = new StringBuffer( target.length() );
		int index = target.indexOf( from );
		if( index == -1 ){
			return target;
		}
		s.append( target.substring( 0, index ) );
		s.append( to );
		s.append( target.substring( index + from.length() ) );
		return s.toString();
	}
	
}
