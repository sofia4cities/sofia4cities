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

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.http.client.ClientProtocolException;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import com.indracompany.sofia2.config.model.Notebook;

public interface NotebookService {

	public Notebook createEmptyNotebook(String name, String userId);
	
	public Notebook importNotebook(String name, String data, String userId);
	
	public Notebook cloneNotebook(String name, String idzep, String userId);
	
	public ResponseEntity<byte[]> exportNotebook(String id, String ususerIder);
	
	public void removeNotebook(String id, String userId);
	
	public String loginOrGetWSTokenAdmin();
	
	public String loginOrGetWSToken();
	
	public ResponseEntity<String> sendHttp(HttpServletRequest requestServlet, HttpMethod httpMethod, String body)
			throws URISyntaxException, ClientProtocolException, IOException;
	
	public Notebook getNotebook(String identification, String userId);
	
	public List<Notebook> getNotebooks(String userId);
}
