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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component("matrixFormatter")
@Slf4j
public class ConsoleFormatter implements IQuasarTableFormatter{
	
	private final static String KEY="CONSOLE";
	
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
		
		List<List<String>> list=new LinkedList<List<String>>(); 
		
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
		
		try {
			result.setData(serialize((Serializable) list));
		} catch (IOException e) {
			log.error("Serialization error.", e);
		}

		return result;
	}

	private static String serialize(Serializable obj) throws IOException {
        if (obj == null) return "";
        try {
            ByteArrayOutputStream serialObj = new ByteArrayOutputStream();
            ObjectOutputStream objStream = new ObjectOutputStream(serialObj);
            objStream.writeObject(obj);
            objStream.close();
            return encodeBytes(serialObj.toByteArray());
        } catch (Exception e) {
           log.error("Serialization error: " + e.getMessage(), e);
           return null;
        }
    }
	
	private static String encodeBytes(byte[] bytes) {
        StringBuffer strBuf = new StringBuffer();
    
        for (int i = 0; i < bytes.length; i++) {
            strBuf.append((char) (((bytes[i] >> 4) & 0xF) + ((int) 'a')));
            strBuf.append((char) (((bytes[i]) & 0xF) + ((int) 'a')));
        }
        
        return strBuf.toString();
    }

}
