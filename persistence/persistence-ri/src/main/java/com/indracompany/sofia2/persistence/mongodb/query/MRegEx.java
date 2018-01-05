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
