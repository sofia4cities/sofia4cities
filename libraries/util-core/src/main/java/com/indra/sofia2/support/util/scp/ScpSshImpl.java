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
package com.indra.sofia2.support.util.scp;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;


@Component
public class ScpSshImpl implements ScpSsh {

	private static final Logger logger = LoggerFactory.getLogger(ScpSshImpl.class);
	
	private ConnectionsDual cd = new ConnectionsDual();
	
	
	/**
	 * PROPIEDADES de conexion
	 */
	String user;
	String password;
	String host;
	int port;
	int timeoutConnection;
	String privateKey;
	boolean privateKeyAuth;
	private String rootRemoteProcessFolder;
	
	@Override
	public void initialize(String user,String password,String host,int port, int timeoutConnection, boolean privateKeyAuth, String rootRemoteProcessFolder){
		
		this.user=user;
		this.password=password;
		this.host=host;
		this.port=port;
		this.timeoutConnection=timeoutConnection;
		this.privateKeyAuth=privateKeyAuth;
		this.rootRemoteProcessFolder=rootRemoteProcessFolder;
	}
	
	/**
	 * 
	 * @param file
	 * @param bytes
	 * @throws Exception
	 */
	@Override
	public void uploadFileRemote(String file, byte[] byteFile) throws Exception {
		
		
		logger.info("Upload file start. File ={} , host ={} , port= {} , ruta = {} ", file , host, port , rootRemoteProcessFolder);
		
		
		/**
		 * check connection 
		 */
		if(cd.getcShell()==null || !cd.getcShell().isConnected() || !cd.getcShell().getSession().isConnected()){
			logger.info("info.uploadFileRemote start");
			cd = openRemoteDualChannel();
		}
			
		
		if(!host.isEmpty()){
			
			/**
			 * Upload File 
			 */
			InputStream is = new ByteArrayInputStream(byteFile);
			cd.getcSFTP().put(is, rootRemoteProcessFolder + "/" + file, ChannelSftp.OVERWRITE);
			logger.info("info.uploadFile", rootRemoteProcessFolder + "/" + file, cd.getcSFTP());
		}
	
		/**
		 * Cierra connection
		 */
		this.closeRemoteSSHChannel(cd.getcShell(), cd.getcSFTP());
		
		logger.info("Upload file end. File ={} ", file );
	}
	
	
	/**
	 *Crea y devuelve dos canales abiertos SSH y SFTP con la máquina remota 
	 *
	 * @return
	 * @throws Exception
	 */
	private ConnectionsDual openRemoteDualChannel() throws Exception{
		
		logger.info("Open remote dual channel start. Host ={} , port= {}", host, port );
		
		JSch jsch = new JSch();
		
		if(host.isEmpty()){
			return new ConnectionsDual();
		}else{
			
			/**
			 * Start sftp process
			 */
			
			/***
			 * CONFIG
			 * 
			 */
			/*if(privateKeyAuth){
				logger.info("info.openRemoteDualChannel.tryidadd");
				jsch.addIdentity(privateKey, password);
				logger.info("info.openRemoteDualChannel.idadd");
			}*/
			
			logger.info("Open remote dual channel.trysescre");
			
			Session session = jsch.getSession(user, host, port);
			logger.info("Open remote dual channel.sescre");
			
			if(!privateKeyAuth){
				logger.info("Open remote dual channel.passadd");
				session.setPassword(password);
			}
			
			session.setConfig("StrictHostKeyChecking", "no");
			session.setTimeout(timeoutConnection);
			
			/**
			 * CONNECT
			 * 
			 */
			
			logger.info("Open remote dual channel.trysescon");
			session.connect();
			logger.info("Open remote dual channel.sescon");
			
			/**
			 * CHANNEL SHELL
			 * 
			 */
			
			Channel channelShell = session.openChannel("shell");
			channelShell.connect(timeoutConnection);
			ConnectionsDual cd = new ConnectionsDual();
			cd.setcShell((ChannelShell) channelShell);
			logger.info("Open remote dual channel.remshell");
			
			/**
			 * CHANNEL SFTP
			 * 
			 */
			Channel channelSFTP = session.openChannel("sftp");
			channelSFTP.connect(timeoutConnection);
			cd.setcSFTP((ChannelSftp) channelSFTP);
			logger.info("Open remote dual channel.nowremshell");
			
			logger.info("Open remote dual channel end.   host ={} , port= {} ", host, port);
			
			return cd;
		}
	}
	
	/**
	 * Cierra un canal abierto con la máquina remota
	 * 
	 * @param cshell
	 * @param csftp
	 * @throws Exception
	 */
	private void closeRemoteSSHChannel(ChannelShell cshell, ChannelSftp csftp) throws Exception{
		
		logger.info("Close remote dual channel start. Host ={} , port= {} ", host, port);
		
		if(!host.isEmpty()){
			logger.info("Close remote dual channel.tryclocha");
			cshell.disconnect();
			csftp.exit();
			csftp.getSession().disconnect();
			logger.info("Close remote dual channel.clocha");
		}
		
		logger.info("Close remote dual channel end.  Host ={} , port= {} ", host, port);
		
	}

}
