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
package com.indra.sofia2.support.util.pipeline;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

import org.apache.commons.httpclient.HttpClientError;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.codehaus.jackson.map.ObjectMapper;

public final class PipelineService {
	
	public static String getMetric(String url) throws Exception{
		
		HttpGet httpGet=null;
		CloseableHttpClient httpClient = new DefaultHttpClient();
		HttpResponse httpResponse = null;
		String resultJson=null;
	
		try{
			httpGet = new HttpGet(new URI(url));
			httpGet.addHeader("Accept", "*/*");
			httpGet.addHeader("Content-Type", "application/json; charset=UTF-8");
			httpGet.addHeader("X-Requested-By", "Data Collector");
			httpGet.addHeader("Connection", "close");
		} catch (URISyntaxException e1) {
			httpClient.close();
			throw new IllegalArgumentException("The URI of the endpoint is invalid.");
		}
		
		try{
			httpResponse = httpClient.execute(httpGet);			
			HttpEntity en=httpResponse.getEntity();
			if (httpResponse.getEntity()!=null){
				resultJson=getResponse(httpResponse.getEntity().getContent());
				EntityUtils.consume(en);
			}
		} catch(Exception e){
	
		}finally{			
			httpGet.releaseConnection();
			httpClient.close();
		}
		
		if (httpResponse != null && httpResponse.getStatusLine() != null){
			if (httpResponse.getStatusLine().getStatusCode()/100 != 2){
				throw new HttpClientError("Error: Status: "+httpResponse.getStatusLine().getStatusCode()+". Message: "+resultJson);
			}
		} 
		return resultJson;
	}
	
	public static String executeService(String url, String pipeline, String operation) throws Exception{
		
		HttpPost httpPost;
		HttpResponse httpResponse=null;
		String response=null;
		CloseableHttpClient httpClient= new DefaultHttpClient();
		
		try {
			httpPost = new HttpPost(new URI(url+"/pipeline/"+pipeline+"/"+operation));
			httpPost.addHeader("Accept", "*/*");
			httpPost.addHeader("Content-Type", "application/json; charset=UTF-8");
			httpPost.addHeader("X-Requested-By", "Data Collector");
			httpPost.addHeader("Connection", "close");
		} catch (URISyntaxException e1) {
			httpClient.close();
			throw new IllegalArgumentException("The URI of the endpoint is invalid."+e1.getMessage());
		}
		
		try{
			httpResponse = httpClient.execute(httpPost);
			HttpEntity en=httpResponse.getEntity();
			response=getResponse(httpResponse.getEntity().getContent());
			EntityUtils.consume(en);
		} catch(Exception e){
			
		}finally{			
			httpPost.releaseConnection();
			httpClient.close();
		}
		
		if (httpResponse != null && httpResponse.getStatusLine() != null){
			if (httpResponse.getStatusLine().getStatusCode()/100 != 2){
				throw new HttpClientError("Error: Status: "+httpResponse.getStatusLine().getStatusCode()+". Message: "+response);
			}
		} 
		
		return response;
	}
	
	@SuppressWarnings("unchecked")
	public static long getTimeOfLastReceivedRecord(String jsonRespuesta) throws Exception{
		
		Map<String,Map<String,Map<String,Map<String,?>>>> obj = new ObjectMapper().readValue(jsonRespuesta,Map.class);
		return (Long) obj.get("gauges").get("RuntimeStatsGauge.gauge").get("value").get("timeOfLastReceivedRecord");
	}

	@SuppressWarnings("unchecked")
	public static long getProcessedMessage(String jsonRespuesta) throws Exception{
		
		Map<String,Map<String,Map<String,Map<String,String>>>> obj = new ObjectMapper().readValue(jsonRespuesta,Map.class);
		return Long.parseLong(obj.get("gauges").get("RuntimeStatsGauge.gauge").get("value").get("currentSourceOffset"));
	}
   
	private static String getResponse(InputStream input){
		
		StringBuilder mensaje = new StringBuilder();

		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(input));
			String linea;
			while ((linea=br.readLine())!=null){
				mensaje.append(linea); 
			}
			input.close();
			br.close();
		} catch (IOException e) {
			mensaje.append(e.getMessage());
		}		
		return mensaje.toString();
	}
}
