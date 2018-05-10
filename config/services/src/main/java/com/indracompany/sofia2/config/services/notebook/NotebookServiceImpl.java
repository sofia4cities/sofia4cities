/**
 * Copyright Indra Sistemas, S.A.
 * 2013-2018 SPAIN
 *
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
package com.indracompany.sofia2.config.services.notebook;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.indracompany.sofia2.config.model.Notebook;
import com.indracompany.sofia2.config.model.Role;
import com.indracompany.sofia2.config.model.User;
import com.indracompany.sofia2.config.repository.NotebookRepository;
import com.indracompany.sofia2.config.repository.UserRepository;
import com.indracompany.sofia2.config.services.exceptions.NotebookServiceException;
import com.indracompany.sofia2.config.services.notebook.configuration.NotebookServiceConfiguration;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class NotebookServiceImpl implements NotebookService {
	
	@Autowired
	private NotebookServiceConfiguration configuration;
	
	@Autowired
	private NotebookRepository notebookRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	private String encryptRestUserpass() {
		String key = configuration.getRestUsername() + ":" + configuration.getRestPass();
		String encryptedKey = new String(Base64.encode(key.getBytes()), Charset.forName("UTF-8"));
		key = "Basic " + encryptedKey;
		return key;
	}
	
	private Notebook sendZeppelinCreatePost(String path, String body, String name, User user) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		String idzep;
		ResponseEntity<String> responseEntity;
		
		log.info("Creating notebook for user: " + user.getUserId() + " with name: " + name);
		
		try {
			responseEntity = sendHttp(path, HttpMethod.POST, body, headers);
		} catch (URISyntaxException e) {
			log.error("The URI of the endpoint is invalid in creation POST");
			throw new NotebookServiceException("The URI of the endpoint is invalid in creation POST: " + e);
		} catch	(IOException e) {
			log.error("Exception in POST in creation POST");
			throw new NotebookServiceException("Exception in POST in creation POST: ", e);
		}
		
		int statusCode = responseEntity.getStatusCodeValue();
		/*200 zeppelin 8, 201 zeppelin 7*/
		if (statusCode/100 != 2) {
			log.error("Exception executing creation POST, status code: " + statusCode);
			throw new NotebookServiceException("Exception executing creation POST, status code: " + statusCode);
		}
		
		try {	
			JSONObject createResponseObj = new JSONObject(responseEntity.getBody());
			idzep = createResponseObj.getString("body");
		} catch (JSONException e) {
			log.error("Exception parsing answer in create post");
			throw new NotebookServiceException("Exception parsing answer in create post: ", e);
		}	
		
		Notebook nt = saveDBNotebook(name, idzep, user);
		log.info("Notebook for user: " + user.getUserId() + " with name: " + name + ", successfully created");
		return nt;
	}
	
	public Notebook saveDBNotebook(String name, String idzep, User user) {
		Notebook nt = new Notebook();
		nt.setIdentification(name);
		nt.setIdzep(idzep);
		nt.setUser(user);
		notebookRepository.save(nt);
		return nt;
	}
	
	public Notebook createEmptyNotebook(String name, String userId) {
		User user = userRepository.findByUserId(userId);
		return sendZeppelinCreatePost("/api/notebook", "{'name': '" + name + "'}", name, user);
	}
	
	public Notebook importNotebook(String name, String data, String userId) {
		User user = userRepository.findByUserId(userId);
		return sendZeppelinCreatePost("/api/notebook/import", data, name, user);
	}
	
	public Notebook cloneNotebook(String name, String idzep, String userId) {
		Notebook nt = notebookRepository.findByIdzep(idzep);
		User user = userRepository.findByUserId(userId);
		if (hasUserPermissionInNotebook(nt,user)) {
			return sendZeppelinCreatePost("/api/notebook/" + idzep, "{'name': '" + name + "'}", name, user);
		} else {
			return null;
		}
	}
	
	public ResponseEntity<byte[]> exportNotebook(String id, String user) {
		Notebook nt = notebookRepository.findByIdentification(id);
		ResponseEntity<String> responseEntity;
		JSONObject notebookJSONObject;
		
		if (hasUserPermissionInNotebook(nt,user)) {
			try {
				responseEntity = sendHttp("/api/notebook/export/" + nt.getIdzep(), HttpMethod.GET, "");
			} catch (URISyntaxException e) {
				log.error("The URI of the endpoint is invalid in creation POST");
				throw new NotebookServiceException("The URI of the endpoint is invalid in creation POST: " + e);
			} catch	(IOException e) {
				log.error("Exception in POST in creation POST");
				throw new NotebookServiceException("Exception in POST in creation POST: ", e);
			}
			
			int statusCode = responseEntity.getStatusCodeValue();
			
			if (statusCode != 200) {
				log.error("Exception executing export notebook, status code: " + statusCode);
				throw new NotebookServiceException("Exception executing export notebook, status code: " + statusCode);
			}
			
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.TEXT_PLAIN);
			headers.set("Content-Disposition", "attachment; filename=\"" + nt.getIdentification() + ".json\"");
			try {
				JSONObject responseJSONObject = new JSONObject(responseEntity.getBody());
				notebookJSONObject = new JSONObject(responseJSONObject.getString("body"));
			} catch (JSONException e) {
				log.error("Exception parsing answer in download notebook");
				throw new NotebookServiceException("Exception parsing answer in download notebook: ", e);
			}
			return new ResponseEntity<byte[]>(
					notebookJSONObject.toString().getBytes(Charset.forName("UTF-8")), headers,
					HttpStatus.OK);
			
		} else {
			log.error("Exception executing export notebook, permission denied");
			throw new NotebookServiceException("Error export notebook, permission denied");
		}
	}
	
	public void removeNotebook(String id, String user) {
		ResponseEntity<String> responseEntity;
		Notebook nt = notebookRepository.findByIdentification(id);
		String name = nt.getIdentification();
		
		log.info("Delete notebook for user: " + user + " with name: " + name);
		
		if (hasUserPermissionInNotebook(nt,user)) {
			
			try {
				responseEntity = sendHttp("/api/notebook/" + nt.getIdzep(), HttpMethod.DELETE, "");
			} catch (URISyntaxException e) {
				log.error("The URI of the endpoint is invalid in delete notebook");
				throw new NotebookServiceException("The URI of the endpoint is invalid in delete notebook: " + e);
			} catch	(IOException e) {
				log.error("Exception in POST in creation POST");
				throw new NotebookServiceException("Exception in POST in delete notebook: ", e);
			}
			
			int statusCode = responseEntity.getStatusCodeValue();
			
			if (statusCode != 200) {
				log.error("Exception executing delete notebook, status code: " + statusCode);
				throw new NotebookServiceException("Exception executing delete notebook, status code: " + statusCode);
			}
			
			notebookRepository.delete(nt);
			log.info("Notebook for user: " + user + " with name: " + name + ", successfully deleted");
		} else {
			log.error("Exception executing delete notebook, permission denied");
			throw new NotebookServiceException("Error delete notebook, permission denied");
		}
	}
	
	public String loginOrGetWSToken() {
		return loginOrGetWSTokenWithUserPass(configuration.getZeppelinShiroUsername(), configuration.getZeppelinShiroPass());
	}
	
	public String loginOrGetWSTokenAdmin() {
		return loginOrGetWSTokenWithUserPass(configuration.getZeppelinShiroAdminUsername(), configuration.getZeppelinShiroAdminPass()) ;
	}
	
	private String loginOrGetWSTokenWithUserPass(String username, String password) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		ResponseEntity<String> responseEntity;
		
		try {
			responseEntity = sendHttp("api/login", HttpMethod.POST, "userName=" + username + "&password=" + password, headers);
		} catch (URISyntaxException e) {
			log.error("The URI of the endpoint is invalid in authentication POST");
			throw new NotebookServiceException("The URI of the endpoint is invalid in authentication POST: " + e);
		} catch	(IOException e) {
			log.error("Exception in POST in authentication POST");
			throw new NotebookServiceException("Exception in POST in authentication POST: ", e);
		}
		
		int statusCode = responseEntity.getStatusCodeValue();
		
		if (statusCode != 200) {
			log.error("Exception executing authentication POST, status code: " + statusCode);
			throw new NotebookServiceException("Exception executing authentication POST, status code: " + statusCode);
		}
		
		return responseEntity.getBody();
		
	}
	
	public ResponseEntity<String> sendHttp(HttpServletRequest requestServlet, HttpMethod httpMethod, String body)
			throws URISyntaxException, ClientProtocolException, IOException {
		return sendHttp(requestServlet.getServletPath(), httpMethod, body);
	}
	
	public ResponseEntity<String> sendHttp(String url, HttpMethod httpMethod, String body)
			throws URISyntaxException, ClientProtocolException, IOException {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		return sendHttp(url, httpMethod, body, headers);
	}
	
	public ResponseEntity<String> sendHttp(String url, HttpMethod httpMethod, String body, HttpHeaders headers)
			throws URISyntaxException, ClientProtocolException, IOException {
		RestTemplate restTemplate = new RestTemplate();
		headers.add("Authorization", encryptRestUserpass());
		org.springframework.http.HttpEntity<String> request = new org.springframework.http.HttpEntity<String>(body,
				headers);
		log.debug("Sending method " + httpMethod.toString() + " Notebook");
		ResponseEntity<String> response = new ResponseEntity<String>(HttpStatus.ACCEPTED);
		try {
			response = restTemplate.exchange(
					new URI(configuration.getBaseURL() + url.substring(url.toLowerCase().indexOf("api"))), httpMethod,
					request, String.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		log.debug("Execute method " + httpMethod.toString() + " '" + url + "' Notebook");
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.set("Content-Type", response.getHeaders().getContentType().toString());
		return new ResponseEntity<String>(response.getBody(), responseHeaders,
				HttpStatus.valueOf(response.getStatusCode().value()));
	}
	
	public Notebook getNotebook(String identification, String userId) {
		Notebook nt = notebookRepository.findByIdentification(identification);
		if (hasUserPermissionInNotebook(nt,userId)) {
			return nt;
		} else {
			return null;
		}
	}
	public List<Notebook> getNotebooks(String userId) {
		User user = userRepository.findByUserId(userId);
		if (!user.getRole().getId().equals(Role.Type.ROLE_ADMINISTRATOR.toString())) {
			return notebookRepository.findByUser(user);
		} else {
			return notebookRepository.findAll();
		}
	}

	private boolean hasUserPermissionInNotebook(Notebook nt, String userId) {
		User user = userRepository.findByUserId(userId);
		return hasUserPermissionInNotebook( nt,user); 
	}
	
	private boolean hasUserPermissionInNotebook(Notebook nt, User user) {
		return user.getRole().getId().equals(Role.Type.ROLE_ADMINISTRATOR.toString()) || nt.getUser().getUserId().equals(user.getUserId()); 
	}

}
