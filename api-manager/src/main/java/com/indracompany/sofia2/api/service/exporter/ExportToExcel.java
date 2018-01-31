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
package com.indracompany.sofia2.api.service.exporter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;






@Component("exportToExcel")
@Slf4j
public class ExportToExcel {

	@Autowired
	private Export exportarDatos;
	
	

	// Pasa de JSONARRAY A EXCEL
	public ByteArrayOutputStream extractJSONtoFile(String resultQuery){
		
		log.debug("Se recoge el JSON y se recogen los datos en una matriz");
		List<List<Object>> values = exportarDatos.transfromJSON(resultQuery);
		return convertTableToFile(values);
		
	}

	// Pasa de list de list de objectos A EXCEL
	@SuppressWarnings("static-access")
	public ByteArrayOutputStream convertTableToFile(List<List<Object>> values) {
		
		if (values==null){
			return null;
		}
		
		Workbook wb = new HSSFWorkbook();
		ByteArrayOutputStream out = new ByteArrayOutputStream();  
       
		
		HSSFCellStyle my_style = (HSSFCellStyle) wb.createCellStyle();
		HSSFCellStyle my_style2 = (HSSFCellStyle) wb.createCellStyle();
        HSSFFont my_font=(HSSFFont) wb.createFont();
       
       // my_font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        
        /* attach the font to the style created earlier */
        my_style.setFont(my_font);
        my_style.setAlignment(org.apache.poi.ss.usermodel.HorizontalAlignment.CENTER);
        my_style2.setAlignment(org.apache.poi.ss.usermodel.HorizontalAlignment.CENTER);
        
		try {
			
			Sheet hoja = wb.createSheet("ResultadoConsulta");
			Row fila;
			log.debug("Se transforma la matriz en Excel");

			for (int i = 0; i < values.size(); i++) {
				fila = hoja.createRow(i); // creamos fila
			
				for (int numCeldas = 0; numCeldas < values.get(i).size(); numCeldas++) {// celdas por fila
					    Cell celda = fila.createCell(numCeldas);

					    if ((values.get(i).get(numCeldas)) instanceof Double){
					    	celda.setCellValue((Double) values.get(i).get(numCeldas));
					    } else if ((values.get(i).get(numCeldas)) instanceof String){
					    	celda.setCellValue((String)values.get(i).get(numCeldas));
					    } else if ((values.get(i).get(numCeldas)) instanceof Boolean){
					    	celda.setCellValue((Boolean)values.get(i).get(numCeldas));
					    } else if ((values.get(i).get(numCeldas)) instanceof Integer){
					    	celda.setCellValue((Integer)values.get(i).get(numCeldas));
					    } else if((values.get(i).get(numCeldas)==null)){
					    	celda.setCellValue((String)"null");
					    } else {
					    	celda.setCellValue((String) values.get(i).get(numCeldas).toString());
					    }
					    
						if (i==0){
							celda.setCellStyle(my_style);
						}
						else{
							celda.setCellStyle(my_style2);
						}
						
						hoja.autoSizeColumn(numCeldas);
				}
			}
			log.debug("Se envia el ByteArrayOutputStream correspondiente al fichero Excel ");
			wb.write(out);		
		} catch (IOException e) {
			log.debug("Error al crear el fichero Excel ");
			return null;
		}
		
		return out;
	}
	
	// Pasa de list de list de string A EXCEL
	public ByteArrayOutputStream convertStringTableToFile(List<List<String>> values) {
		
		if (values==null){
			return null;
		}
		
		Workbook wb = new HSSFWorkbook();
		ByteArrayOutputStream out = new ByteArrayOutputStream();  
       
		
		HSSFCellStyle my_style = (HSSFCellStyle) wb.createCellStyle();
		HSSFCellStyle my_style2 = (HSSFCellStyle) wb.createCellStyle();
        HSSFFont my_font=(HSSFFont) wb.createFont();
       
       // my_font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        
        /* attach the font to the style created earlier */
        my_style.setFont(my_font);
        my_style.setAlignment(org.apache.poi.ss.usermodel.HorizontalAlignment.CENTER);
        my_style2.setAlignment(org.apache.poi.ss.usermodel.HorizontalAlignment.CENTER);
        
		try {
			
			Sheet hoja = wb.createSheet("ResultadoConsulta");
			Row fila;
			log.debug("Se transforma la matriz en Excel");

			for (int i = 0; i < values.size(); i++) {
				fila = hoja.createRow(i); // creamos fila
			
				for (int numCeldas = 0; numCeldas < values.get(i).size(); numCeldas++) {// celdas por fila
				    Cell celda = fila.createCell(numCeldas);
				    if((values.get(i).get(numCeldas)==null)){
				    	celda.setCellValue((String)"null");
				    } else {
				    	celda.setCellValue((String) values.get(i).get(numCeldas).toString());
				    }
				    
					if (i==0){
						celda.setCellStyle(my_style);
					} else{
						celda.setCellStyle(my_style2);
					}
					hoja.autoSizeColumn(numCeldas);
				}
			}
			log.debug("Se envia el ByteArrayOutputStream correspondiente al fichero Excel ");
			wb.write(out);		
		}
		
		catch (IOException e) {
			log.debug("Error al crear el fichero Excel ");
			return null;
		}
		return out;
	}

	//Pasa de JSON-deprecated a fichero Excel
	public ByteArrayOutputStream extractBDHJSONtoFile(String resultQuery) {
		
		String cabecera = null;
		Map<String, String> primerafila=new HashMap<String, String>();
		Workbook wb = new HSSFWorkbook();
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		
		log.debug("Se extrae el nombre de la ontologia sobre la que se ha consultado");
		
		//Recorremos el JSON para obtener el array de columns y el array de values
		try {
			log.debug("Se comprueba que la informacion pasada como parametro es JSON");
			
			JSONObject json =  (JSONObject) new JSONParser().parse(resultQuery);										
			JSONArray instanciaColums = (JSONArray) json.get("columns");
			JSONArray instanciaValues = (JSONArray) json.get("values");

			
			Row fila; 
	       			
			HSSFCellStyle my_style = (HSSFCellStyle) wb.createCellStyle();
			HSSFCellStyle my_style2 = (HSSFCellStyle) wb.createCellStyle();
	        HSSFFont my_font=(HSSFFont) wb.createFont();
	       
	      
	        
	        /* attach the font to the style created earlier */
	        my_style.setFont(my_font);
	        my_style.setAlignment(org.apache.poi.ss.usermodel.HorizontalAlignment.CENTER);
	        my_style2.setAlignment(org.apache.poi.ss.usermodel.HorizontalAlignment.CENTER);
			
				
			Sheet hoja = wb.createSheet("ResultadoConsulta");
			fila = hoja.createRow(0); // creamos la primera fila
		
			
			//Creamos la primera fila con la cabecera del Excel
			for (int i = 0; i < instanciaColums.length(); i++) {
				
				Cell celda = fila.createCell(i); //celdas por fila
				JSONObject instanciaSimple = (JSONObject) instanciaColums.get(i);
				String type=instanciaSimple.get("type").toString();
				String name;
				
				if (i > 5){
					name=cabecera+"."+instanciaSimple.get("name").toString();
					primerafila.put(Integer.toString(i), type);
					
				} else {
					name=instanciaSimple.get("name").toString();
					primerafila.put(Integer.toString(i), type);
				}
												
								
				celda.setCellValue((String)name);
				celda.setCellStyle(my_style);
				
			}
			
			//Contenido con datos			
			for (int j = 0; j < instanciaValues.length(); j++) {
				int k = j+1;
				
				log.debug("Creamos la fila " +k);
				fila = hoja.createRow(k); // creamos la segunda y las siguientes filas
				
				org.json.simple.JSONArray valueSimple = (org.json.simple.JSONArray) instanciaValues.get(j);
				
				for (int l = 0; l < valueSimple.size(); l++) {
					
				 log.debug("Creamos la celda " +l + "de la fila "+k);
					
				 Cell celda = fila.createCell(l); //celdas por fila	
					
				 log.debug("Clave: " + Integer.toString(l) + " -> Valor: " + primerafila.get(Integer.toString(l)));
				 log.debug("Valor a insertar:" +valueSimple.get(l) );
				 
				 if (primerafila.get(Integer.toString(l)).equals("INTEGER")){
					   if (valueSimple.get(l) instanceof Long){
						   celda.setCellValue((Long) valueSimple.get(l));
					   } else {
				    	celda.setCellValue((Integer) valueSimple.get(l));
					   }
				 }
				 else if (primerafila.get(Integer.toString(l)).equals("VARCHAR")){
				    	celda.setCellValue((String) valueSimple.get(l));
				 }
				 else if (primerafila.get(Integer.toString(l)).equals("BOOLEAN")){
				    	celda.setCellValue((Boolean)valueSimple.get(l));
				 }
				 else if (primerafila.get(Integer.toString(l)).equals("FLOAT")){
				    	celda.setCellValue((Float)valueSimple.get(l));
				 }
				 else if (primerafila.get(Integer.toString(l)).equals("DOUBLE")){
				    	celda.setCellValue((Double)valueSimple.get(l));
				 }
				 else if (primerafila.get(Integer.toString(l)).equals("FLOAT")){
				    	celda.setCellValue((Float)valueSimple.get(l));
				 }
				 else 
				    	celda.setCellValue((String) valueSimple.get(l).toString());
				
				 celda.setCellStyle(my_style2);
				 hoja.autoSizeColumn(l);
					
				}
								
				
				k=k+1;
			
			}
			
			log.debug("Se envia el ByteArrayOutputStream correspondiente al fichero EXCEL/XML ");
			wb.write(out);
			
	   } catch (Exception e) {
			log.error("Error ExtractBDHJSONto: Error al crear el fichero Excel"+e);
			return null;
	   }
		
		
		
		return out;
	}
	
}