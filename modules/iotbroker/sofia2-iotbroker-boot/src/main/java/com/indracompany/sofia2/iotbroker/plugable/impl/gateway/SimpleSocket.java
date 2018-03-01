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
package com.indracompany.sofia2.iotbroker.plugable.impl.gateway;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Component;

import com.indracompany.sofia2.iotbroker.plugable.interfaces.gateway.Gateway;
import com.indracompany.sofia2.iotbroker.processor.MessageProcessor;
import com.indracompany.sofia2.ssap.SSAPMessage;
import com.indracompany.sofia2.ssap.body.SSAPBodyReturnMessage;
import com.indracompany.sofia2.ssap.json.SSAPJsonParser;
import com.indracompany.sofia2.ssap.json.Exception.SSAPParseException;

//@Component
public class SimpleSocket extends Gateway {
	ServerSocket serverSocket;
	ExecutorService executor;
	Future<?> futureServer;
	MyServer server;

	@Override
	public void startGateway(boolean clearState) {
		try {
			serverSocket = new ServerSocket(3000);
			executor = Executors.newFixedThreadPool(1);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}

	@Override
	public void listen(MessageProcessor processor) {
		MyServer s = new MyServer(processor);
		futureServer = executor.submit(s);
		
	}
	
	

	@Override
	public void stopGateway() {
		try {
			server.cancel();
			futureServer.cancel(true);
//			executor.shutdownNow();
			executor.shutdown();
			executor.awaitTermination(5, TimeUnit.SECONDS);
			serverSocket.close();
		}	
		catch (InterruptedException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
public class MyServer implements Runnable	 {
		
		MessageProcessor processor;
		private boolean bool = false;
		
		public MyServer(MessageProcessor processor) {
			this.processor = processor;
		}
		
		
	    public void cancel() {
	        bool = true;
	        // usually here you'd have inputStream.close() or connection.disconnect()
	    }

		@Override
		public void run() {
		   	
			try {
				while(true) {
					Socket clientSocket = serverSocket.accept();
					clientSocket.setSoTimeout(2000);
					BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
	                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
	                
	                StringBuilder sb = new StringBuilder();
	                String line;
	                while ((line = in.readLine()) != null)
	                    sb.append(line).append("\n");
	                
	                SSAPMessage ssapRequest = SSAPJsonParser.getInstance().deserialize(sb.toString());
	                SSAPMessage<SSAPBodyReturnMessage> response = processor.process(ssapRequest);
	                
	                out.println(SSAPJsonParser.getInstance().serialize(response));
				}
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SSAPParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		}
		
	}

}
