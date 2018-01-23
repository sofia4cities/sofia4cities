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
 * © Indra Sistemas, S.A.
 * 2013 - 2014  SPAIN
 * 
 * All rights reserved
 ******************************************************************************/
package com.indra.sofia2.support.util.json.export.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.indra.sofia2.support.util.json.export.Export;


@Component("exportToCsv")
public class ExportToCsv{
	
	@Autowired
	private Export exportarDatos;
	
	@Autowired 
	private ExportToExcel exportToExcel;
	
	private static final Logger LOG = LoggerFactory.getLogger(ExportToCsv.class);

	// Pasa de JSONARRAY a CSV
	public ByteArrayOutputStream extractJSONtoCSV(String resultQuery){
		List<List<Object>> values = exportarDatos.transfromJSON(resultQuery);
		return convertTableToCsv(values);	
	}
	
	// Escribe una lista de listas en un fichero CSV
	public ByteArrayOutputStream convertStringTableToCSV(List<List<String>> values){
		
		ByteArrayOutputStream out=new ByteArrayOutputStream();
		StringBuilder sb;
		StringBuilder sbresult=new StringBuilder();
		
		for (int fila = 0; fila < values.size(); fila++) {
			sb=new StringBuilder();
			for (int columna = 0; columna < values.get(fila).size(); columna++) {
				if (values.get(fila).get(columna)!=null){
					sb.append(values.get(fila).get(columna).toString()+";");
				}else {
					sb.append(" ;");
				}
			}
			sbresult.append(sb.substring(0, sb.lastIndexOf(";"))+"\n");
		}
		
		try {
			out.write(sbresult.toString().getBytes());
		} catch (IOException e) {
			LOG.error("Error al crear el fichero CSV a partir del Excel ",e);
			return null;
		}
		return out;	
	}
	
	public ByteArrayOutputStream convertBDHExcelToCsv (String resultQuery){
		
		HSSFSheet sheet;
		String cadena;
		ByteArrayOutputStream out=new ByteArrayOutputStream();  
		byte buf[];
		ByteArrayOutputStream byteExcel=exportToExcel.extractBDHJSONtoFile(resultQuery);
		InputStream input=new ByteArrayInputStream(byteExcel.toByteArray());
		List<List<HSSFCell>> cellGrid =  new ArrayList<List<HSSFCell>>();
		
		try {
			HSSFWorkbook wb = new HSSFWorkbook(input);
			wb.setMissingCellPolicy(Row.RETURN_NULL_AND_BLANK);
			sheet=wb.getSheetAt(0);
			Iterator<?> rowIter = sheet.rowIterator();
			LOG.debug("Se recorre el fichero excel para transformarlo en CSV");

			while(rowIter.hasNext()){
				HSSFRow row=(HSSFRow) rowIter.next();
				Iterator<?> cellIter = row.cellIterator();
				List<HSSFCell> cellRowList = new ArrayList<HSSFCell>();
				while(cellIter.hasNext()){
					HSSFCell cell=(HSSFCell)cellIter.next();
					cellRowList.add(cell);
					if(cellIter.hasNext())cadena=cell.toString()+", ";
					else cadena=cell.toString()+" ";
					buf=cadena.getBytes();
					out.write(buf);
				}
				cadena=";  \n";
				buf=cadena.getBytes();
				out.write(buf);
				cellGrid.add(cellRowList);
			}	
			LOG.debug("transformacion de fichero excel a csv terminada");
			out.flush();
		} catch (IOException e) {
			LOG.error("Error al crear el fichero CSV a partir del Excel ",e);
			return null;
		}
		return out;
	}	
	
	private ByteArrayOutputStream convertTableToCsv(List<List<Object>> values){
		
		if (values==null){
			return null;
		}
		ByteArrayOutputStream out=new ByteArrayOutputStream();
		StringBuilder sb;
		StringBuilder sbresult=new StringBuilder();
		for (int fila = 0; fila < values.size(); fila++) {
			sb=new StringBuilder();
			for (int columna = 0; columna < values.get(fila).size(); columna++) {
				if (values.get(fila).get(columna)!=null){
					sb.append(values.get(fila).get(columna).toString()+";");
				}else {
					sb.append(" ;");
				}
			}
			sbresult.append(sb.substring(0, sb.lastIndexOf(";"))+"\n");
		}
		try {
			out.write(sbresult.toString().getBytes());
		} catch (IOException e) {
			LOG.error("Error al crear el fichero CSV a partir del Excel ",e);
			return null;
		}
		return out;	
	}

}
