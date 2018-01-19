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
package com.indracompany.sofia2.persistence.mongodb.quasar.formatter;

import java.util.LinkedList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("htmlFormatter")
public class HtmlFormatter implements IQuasarTableFormatter{
	
	private final static String KEY="HTML";
	
	@Autowired
	private IQuasarFormatterRegistry registry;
	
	
	@PostConstruct
	public void init(){
		this.registry.addFormatter(KEY, this);
	}


	@Override
	public FormatResult format(String input, String columnDelimiter,
			String rowDelimiter, String quoteChar, String escapeChar,
			String charset) {
		
		FormatResult result=new FormatResult();
		
		result.setContentType("text/html");
		result.setData(buildHtml(input, columnDelimiter, rowDelimiter, quoteChar, escapeChar, charset));
		
		return result;
	}
	
	private String buildHtml(String input, String columnDelimiter,
			String rowDelimiter, String quoteChar, String escapeChar,
			String charset){
		
		List<List<String>> list=new LinkedList<List<String>>(); 
		String html = "<html><body><table><tr>";
		
		//Obtenemos las filas
		String rows[] = input.split(rowDelimiter.replace("\\\\", "\\"));
		
		List<String> fila=new LinkedList<String>();
		
		for (int i = 0; i < rows.length; i++) {
			fila=new LinkedList<String>();
			
			String colum[]= rows[i].split(columnDelimiter);
			for (int j = 0; j < colum.length; j++) {
				fila.add(colum[j]);
			}
			list.add(fila);
		}
		
		//Metemos la cabecera de la tabla
		List<String> cabecera = list.get(0);
		
		for (String string : cabecera) {
			html +="<th>" + string + "</th>";
		}
		//Cerramos la cabecera
		html += "</tr>";
		
		//Metemos elementos de la tabla
		
		for (int i=1; i< list.size(); i++) {
			html += "<tr>";
			List<String> subList = list.get(i);
			for (String string : subList) {
				html +="<td>" + string + "</td>";
			}
			html += "</tr>";
		}
		
		html += "</table></body></html>";
		return html;
	}

	
	

}
