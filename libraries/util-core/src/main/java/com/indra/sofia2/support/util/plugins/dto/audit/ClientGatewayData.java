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

package com.indra.sofia2.support.util.plugins.dto.audit;

public class ClientGatewayData {

	private String adress;
	private int port;
	private ClientGatewayType platform;
	
	
	public ClientGatewayData(String adress, int port, ClientGatewayType platform){
		this.adress=adress;
		this.port=port;
		this.platform=platform;
	}
	
	public String getAdress() {
		return adress;
	}
	public void setAdress(String adress) {
		this.adress = adress;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public ClientGatewayType getPlatform() {
		return platform;
	}
	public void setPlatform(ClientGatewayType platform) {
		this.platform = platform;
	}
	
	
	
}
