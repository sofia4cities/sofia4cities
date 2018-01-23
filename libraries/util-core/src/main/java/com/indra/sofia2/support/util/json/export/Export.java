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
package com.indra.sofia2.support.util.json.export;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.stereotype.Component;

import com.indra.jee.arq.spring.core.infraestructura.log.I18nLog;
import com.indra.jee.arq.spring.core.infraestructura.log.I18nLogFactory;

@Component
public class Export {
	
	private static final I18nLog log = I18nLogFactory.getLogI18n(Export.class);

	
    @SuppressWarnings("unchecked")
	private void compruebaValorDeClaves(LinkedHashMap<String, Object> nodo,String nombreClave, Map<String,Object> dato){

		Iterator<Entry<String, Object>> recorre = nodo.entrySet().iterator();
			
		while (recorre.hasNext()){
			
			Map.Entry<String, Object> e = (Map.Entry<String, Object>) recorre.next();
			
			if (e.getValue() instanceof LinkedHashMap){
				
				compruebaValorDeClaves((LinkedHashMap<String, Object>)e.getValue(),nombreClave+":"+e.getKey().toString(),dato); // le paso el subgrupo
			
			} else if (e.getValue() instanceof ArrayList){
				
				List<Object> miarray=((ArrayList<Object>) e.getValue());
			    Iterator<Object>iterador = miarray.iterator();
			    
			    for (int cont=0;iterador.hasNext();cont++) {
			    	
			    	Object temp=(Object)iterador.next();
			    	if (temp instanceof LinkedHashMap){
			    		compruebaValorDeClaves((LinkedHashMap<String, Object>) temp,nombreClave+":"+e.getKey().toString()+"["+cont+"]",dato); // le paso el subgrupo
			    	} else {
						dato.put(nombreClave+":"+e.getKey().toString()+"["+cont+"]", temp);
			    	}
			    }
			    if (miarray.size()==0){
					dato.put(nombreClave+":"+e.getKey().toString()+"[0]", "");
			    }
			}else{
				dato.put(nombreClave+":"+e.getKey().toString(),e.getValue());
			}
		}
    }

   @SuppressWarnings("unchecked")
   public List<List<Object>> transfromJSON(String json) {
		
	    String nombreClave = "";
	    List<List<Object>> resultado=new LinkedList<List<Object>>();
	    List<List<Object>> resultadoaux=new LinkedList<List<Object>>();
	    Map<String,Object> dato=new LinkedHashMap<String, Object>();
	    List<Object> cabecera=new LinkedList<Object>();
	    
		try {
			Map<String, Object> obj[] = new ObjectMapper().readValue(json,Map[].class); // genera el mapa global
			
			for (int j = 0; j<obj.length; j++) {
				Iterator<Entry<String, Object>> it = obj[j].entrySet().iterator();
				dato=new LinkedHashMap<String,Object>();
				while (it.hasNext()) {
					Map.Entry<String, Object> e = (Map.Entry<String, Object>) it.next(); // lo parte en trozos
					nombreClave = e.getKey().toString();
					if (e.getValue() instanceof LinkedHashMap) {
						compruebaValorDeClaves((LinkedHashMap<String, Object>) e.getValue(),nombreClave,dato);// le paso el subgrupo
					} else {
						dato.put(nombreClave, e.getValue());
					}
				}
				// tengo en dato todos los registros 
				if (cabecera.isEmpty()){
					cabecera.addAll(dato.keySet());
				}
				for(String cab:dato.keySet()){
					if (!cabecera.contains(cab)){
						cabecera.add(cab);
					}
				}
				List<Object> listAux=new LinkedList<Object>();
				for(Object datosCabecera:cabecera){
					listAux.add(dato.get(datosCabecera));
				}
				resultadoaux.add(listAux);
			}
		} catch (Exception e) {
			log.error("Error al generar el archivo descargable a partir de la consulta a BDTR. Message: "+e.getMessage()+". Casue: "+ e.getCause());
			return null;
		}
		resultado.add(cabecera);
		resultado.addAll(resultadoaux);
		return resultado;
	}
}
