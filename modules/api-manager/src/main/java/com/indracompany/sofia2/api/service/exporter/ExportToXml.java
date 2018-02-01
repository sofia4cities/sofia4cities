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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.NotImplementedException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.json.XML;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;







@Component("exportToXml")
public class ExportToXml {
	
	private static final Logger log = LoggerFactory.getLogger(ExportToXml.class);
	
	public ByteArrayOutputStream extractBDHJSONtoXML(String ontology,String resultQuery) {
		
		String contextData="contextData";
		ByteArrayOutputStream out;  
		byte buf[];
		
		if (ontology==null){
			ontology="ontology";
		}
		
		buf = null;
		out = new ByteArrayOutputStream();
		
		escribirSalida("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n",out,buf);
		escribirSalida("<!DOCTYPE html>\n",out,buf);
		log.debug("Se extrae el nombre de la ontologia sobre la que se ha consultado");
		
		escribirSalida("<"+ontology+"s"+">\n",out,buf);
		
		try {
			log.debug("Se comprueba que la informacion pasada como parametro es JSON");
			
			JSONObject json = (JSONObject) new JSONParser().parse(resultQuery);										
			org.json.simple.JSONArray instanciaColums = (org.json.simple.JSONArray) json.get("columns");
			org.json.simple.JSONArray instanciaValues = (org.json.simple.JSONArray) json.get("values");
			
			//Contenido con datos			
			for (int j = 0; j < instanciaValues.size(); j++) {				
				
				log.debug("Creamos elemento " +(j+1 )+" de " + ontology);
				escribirSalida("<"+ontology+">\n",out,buf);
				
				org.json.simple.JSONArray valueSimple = (org.json.simple.JSONArray) instanciaValues.get(j);
				
				for (int l = 0; l < valueSimple.size(); l++) {
					
					if (l < 6) {
												
						if (l==0) {
							log.debug("Creamos el id");  //Ejemlo: <_id><![CDATA[<$oid>57c854c573d779f4eb479608</$oid>]]></_id>
							org.json.simple.JSONObject instanciaSimpleColums = (org.json.simple.JSONObject) instanciaColums.get(l);
							String name=instanciaSimpleColums.get("name").toString();
							
							escribirSalida("<"+name+">",out,buf);
							escribirSalida("<![CDATA[<$oid>"+valueSimple.get(l).toString()+"</$oid>]]>",out,buf);
							escribirSalida("</"+name+">",out,buf);
							log.debug("Fin del id");
							
							log.debug("Creamos el contextData");  //<contextData>
							escribirSalida("<"+contextData+">",out,buf);
							
						} else if (l>0){
							//Elementos ContextData							
							org.json.simple.JSONObject instanciaSimpleColums = (org.json.simple.JSONObject) instanciaColums.get(l);
							String name=instanciaSimpleColums.get("name").toString().replaceFirst("contextdata.","");
							
							escribirSalida("<"+name+">",out,buf);
							if (l == 5){
								escribirSalida("<![CDATA[<$date>"+valueSimple.get(l).toString()+"</$date>]]>",out,buf);
								escribirSalida("</"+name+">",out,buf);
								escribirSalida("</"+contextData+">",out,buf);						
								log.debug("Fin de la creación del contextData",out,buf);
							} else {
								escribirSalida(valueSimple.get(l).toString(),out,buf);
								escribirSalida("</"+name+">",out,buf);
							}							
							
						}						
							
					} else {												
						//Elemento con datos
						if (l==6){
							escribirSalida("<"+ontology+">",out,buf);
						}
						
						if (l > 6){
							org.json.simple.JSONObject instanciaSimpleColums = (org.json.simple.JSONObject) instanciaColums.get(l);
							String name=instanciaSimpleColums.get("name").toString();
							escribirSalida("<"+name+">",out,buf);
							escribirSalida(valueSimple.get(l).toString(),out,buf);
							escribirSalida("</"+name.toString()+">",out,buf);
					    } 
					   					   
					}
					
			  }
							 
				escribirSalida("</"+ontology+">",out,buf);
				escribirSalida("\n</"+ontology+">\n",out,buf);
			}
			
			escribirSalida("</"+ontology+"s"+">",out,buf);
			
		} catch (Exception e) {
			log.error("Error extractBDHJSONtoFile: Error al crear el fichero XML"+e);
			return null;
		}	
				
		log.debug("Se envia el ByteArrayOutputStream correspondiente al fichero XML ");
		return out; 
	}
	
	public ByteArrayOutputStream extractJSONtoXML(String ontology,String resultQuery) throws NotImplementedException {
		
		ByteArrayOutputStream out=new ByteArrayOutputStream();;  
		byte buf[]=null;
		JSONObject jObject;
		String cadena = null;
		boolean bandera=false;
		
		if (ontology==null){
			ontology="ontology";
		}

		escribirSalida("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n",out,buf);
		escribirSalida("<!DOCTYPE html>\n",out,buf);
		log.debug("Se extrae el nombre de la ontologia sobre la que se ha consultado");
		escribirSalida("<"+ontology+"s"+">\n",out,buf);
	
		try {
			log.debug("Se comprueba que la informacion pasada como parametro es JSON");

			JSONArray jArray = (JSONArray) new JSONTokener(resultQuery).nextValue();

			for (int j = 0; j < jArray.length(); j++) {

				escribirSalida("<"+ontology+">\n",out,buf);
				jObject = jArray.getJSONObject(j);
				cadena = XML.toString(jObject);
				bandera=false;

				ArrayList<Integer> indicesDollar=buscarDollar(cadena);
				LinkedHashMap<Integer, String> etiquetas=buscarEtiquetas(indicesDollar,cadena);
				//comparacion
				Iterator<Entry<Integer, String>> iter = etiquetas.entrySet().iterator();
				String constructor =cadena;
				int var = 0;
				
				while (iter.hasNext()) {
					
					    Map.Entry<Integer, String> entry = (Entry<Integer, String>) iter.next();
						Iterator<Entry<Integer, String>> iter2 =  etiquetas.entrySet().iterator();
						iter2 =  iter;
						
						while (iter2.hasNext()) {
							Map.Entry<Integer, String> entry2=(Map.Entry<Integer, String>) iter2.next();
							
							if (entry2.getValue().equals("/"+entry.getValue())){
								
								if (constructor.equals(cadena)){
								 	constructor=cadena.substring(0,(Integer)entry.getKey())+"<![CDATA["+cadena.substring((Integer)entry.getKey(),(Integer)entry2.getKey()+entry2.getValue().toString().length()+2)+"]]>";
								}else {
									constructor=constructor+cadena.substring(var,(Integer)entry.getKey())+"<![CDATA["+cadena.substring((Integer)entry.getKey(),(Integer)entry2.getKey()+entry2.getValue().toString().length()+2)+"]]>";
								}
								   
								var=(Integer)entry2.getKey()+entry2.getValue().toString().length()+2;
								bandera=true;
								break;
							}	
						}
						iter=iter2;
				}
				
				if (bandera){
					cadena=constructor+cadena.substring(var);
				} else {
					cadena=constructor;
				}
				
				escribirSalida(cadena,out,buf);
			 	escribirSalida("<"+"/"+ontology+">\n",out,buf);
			}
				
		} catch (JSONException e) {
			log.error("Error al escribir en el fichero XML ",e);
			return null;
		}
		
		escribirSalida("<"+"/"+ontology+"s"+">\n",out,buf);
		log.debug("Se envia el ByteArrayOutputStream correspondiente al fichero XML ");

		return out;
	}
	
	private ArrayList<Integer> buscarDollar(String prueba){
		
		ArrayList<Integer> indicesDollar= new ArrayList<Integer>();
		
		for (int i=0;i<prueba.length();i++){
			if (prueba.charAt(i)=='$'){
				indicesDollar.add(i);
			}
		}
		return indicesDollar;
	}

	private LinkedHashMap<Integer, String> buscarEtiquetas(ArrayList<Integer> indicesDollar,String cadena){
		
		String temp = "";
		int inicio;
		LinkedHashMap<Integer, String>etiqueta=new LinkedHashMap<Integer, String>();
		for (int i=0;i<indicesDollar.size();i++){
		
			//buscar hacia atras
			for (inicio=indicesDollar.get(i);cadena.charAt(inicio)!='<';inicio--){
			}
			//recoge etiqueta
			for (int j=inicio+1;cadena.charAt(j)!='>'; j++){
				temp=temp+cadena.charAt(j);
			}
			etiqueta.put((Integer)inicio, temp);
			temp="";
		}
		return etiqueta;	
	}
	
	private synchronized void escribirSalida(String cadena,ByteArrayOutputStream out,byte buf[]){

		try {
			buf=cadena.getBytes();
			out.write(buf);
		} catch (IOException e1) {
			log.error("Error al escribir en el fichero XML ",e1);
		}
	}
}