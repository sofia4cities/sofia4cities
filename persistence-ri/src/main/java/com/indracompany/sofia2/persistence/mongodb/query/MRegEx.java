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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MRegEx {

	public static String getMatch( String patternStr, String target )	{
		Pattern pattern = Pattern.compile( patternStr, Pattern.DOTALL );
		Matcher matcher = pattern.matcher( target );
		if( matcher.find() ) {
			if( matcher.groupCount() > 0 )
				{
				return matcher.group( 1 );
				}
			else
				{
				return target.substring( matcher.start(), matcher.end() );
				}
			}
		else {
			return "";
			}
	}
	
	public static String getMatchIgnoreCase( String patternStr, String target )	{
		Pattern pattern = Pattern.compile( patternStr, Pattern.CASE_INSENSITIVE | Pattern.DOTALL );
		Matcher matcher = pattern.matcher( target );
		if( matcher.find() ) 	{
			if( matcher.groupCount() > 0 )	{
				return matcher.group( 1 );
				}
			else 	{
				return target.substring( matcher.start(), matcher.end() );
				}
			}
		else {
			return "";
			}
	}
	
}
