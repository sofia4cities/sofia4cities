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
package com.indracompany.sofia2.persistence.mongodb.quasar.connector.dto;

import org.apache.http.Header;
import org.apache.http.HeaderElement;

import lombok.Getter;
import lombok.Setter;

public class QuasarResponseDTO {
	
	private final static String COLUMN_DELIMITER="columnDelimiter";
	private final static String ROW_DELIMITER="rowDelimiter";
	private final static String QUOTE_CHAR="quoteChar";
	private final static String ESCAPE_CHAR="escapeChar";
	private final static String CHARSET="charset";
	
	@Getter @Setter private String data;
	@Getter @Setter private String contentType;
	@Getter @Setter private String columnDelimiter;
	@Getter @Setter private String rowDelimiter;
	@Getter @Setter private String quoteChar;
	@Getter @Setter private String escapeChar;
	@Getter @Setter private String charset;
	@Getter @Setter private Header[] rawContentTypeHeader;
	@Getter @Setter private boolean formatted=false;
	

	public void setHeaders(Header[] headers){
		if(headers.length>0){
			Header header=headers[0];
		
			this.rawContentTypeHeader=headers;
			
			HeaderElement[] elements=header.getElements();
			for(HeaderElement element:elements){
				this.contentType=element.getName();
				this.columnDelimiter=element.getParameterByName(COLUMN_DELIMITER)!=null ? element.getParameterByName(COLUMN_DELIMITER).getValue() : null;
				this.rowDelimiter=element.getParameterByName(ROW_DELIMITER)!=null ? element.getParameterByName(ROW_DELIMITER).getValue() : null;
				this.quoteChar=element.getParameterByName(QUOTE_CHAR)!=null ? element.getParameterByName(QUOTE_CHAR).getValue() : null;
				this.escapeChar=element.getParameterByName(ESCAPE_CHAR)!=null ? element.getParameterByName(ESCAPE_CHAR).getValue() : null;
				this.charset=element.getParameterByName(CHARSET)!=null ? element.getParameterByName(CHARSET).getValue() : null;
			}
		}
	}
	

}
