/*******************************************************************************
 * Copyright 2013-15 Indra Sistemas S.A.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 ******************************************************************************/

package com.indracompany.sofia2.android.s4candroidclient;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;

public class SIBProxyRest {

	//private final static String SERVICE_URL="http://sofia2.com/sib/services/api_ssap/v01/SSAPResource/";
	//private final static String SERVICE_URL="http://sofia2-pocdia.cloudapp.net/sib/services/api_ssap/v01/SSAPResource/";
	private final static String SERVICE_URL="http://52.232.105.18/sib/services/api_ssap/v01/SSAPResource/";
	String TAG = "DIAPoC";

	private int framesSend = 0;


	public synchronized  boolean send(Context context, String TOKEN, String KP_INSTANCE, String ONTOLOGY_NAME, LinkedList<String> frames ) {
		URL url = null;
		try {
			url = new URL(SERVICE_URL); //"http://sofia2.com/sib/services/api_ssap/v01/SSAPResource/"

			HttpURLConnection connection = (HttpURLConnection)url.openConnection();
			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.setRequestMethod("POST");
			connection.setConnectTimeout(10000);
			connection.setReadTimeout(10000);
			connection.setAllowUserInteraction(false);
			connection.setUseCaches(false);
			connection.setRequestProperty("Content-Type","application/json");

			connection.connect();

			DataOutputStream dStream = new DataOutputStream(connection.getOutputStream());
			dStream.writeBytes("{\"join\": true,\"instanceKP\": \"" + KP_INSTANCE + "\",\"token\": \"" + TOKEN + "\"}");

			dStream.flush();
			dStream.close();
			int responseCode = connection.getResponseCode();

			Log.d(TAG,"Sending 'POST' JOIN request to URL : " + url);

			if(responseCode!=200){
				Log.i(TAG,"Error Join " + responseCode);
				return false;
			}

			Log.d(TAG,"Join OK");

			final StringBuilder output = new StringBuilder("Request URL " + url);
			BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String line = "";
			StringBuilder responseOutput = new StringBuilder();
			while((line = br.readLine()) != null ) {
				responseOutput.append(line);
			}
			br.close();
			connection.disconnect();


			JSONObject jsonJOIN = new JSONObject(responseOutput.toString());
			String sessionKey = jsonJOIN.getString("sessionKey");

			Log.d(TAG,"Session key " + sessionKey);

			responseCode = 200;
			int initialSize=frames.size();
			while(frames.size()>0 && responseCode==200){
				connection = (HttpURLConnection)url.openConnection();
				connection.setDoInput(true);
				connection.setDoOutput(true);
				connection.setRequestMethod("POST");
				connection.setConnectTimeout(10000);
				connection.setReadTimeout(10000);
				connection.setAllowUserInteraction(false);
				connection.setUseCaches(false);
				connection.setRequestProperty("Content-Type","application/json");
				connection.connect();

				dStream = new DataOutputStream(connection.getOutputStream());
				String insertFrame = "{\"sessionKey\":\"" + sessionKey + "\", \"ontology\":\"" + ONTOLOGY_NAME + "\", \"data\":\"" + frames.getFirst().replaceAll("\\\"","\\\\\"") + "\"}";
				dStream.writeBytes(insertFrame);
				dStream.flush();
				dStream.close();
				Log.d(TAG,"Sending 'POST' INSERT request to URL : " + url + " Data: " + insertFrame);
				responseCode = connection.getResponseCode();
				if(responseCode==200){
					Log.d(TAG,"OK Sending 'POST' INSERT request to URL : " + url + " Data: " + insertFrame);
					frames.removeFirst();
				}
				else{
					Log.i(TAG,"Error insert " + responseCode + " - " + connection.getResponseMessage());
				}
				connection.disconnect();
			}

			Intent mIntent = new Intent(TrackingService.ACTION_FRAME_SEND);
			framesSend = framesSend + (initialSize-frames.size());
			mIntent.putExtra("frame_send", String.valueOf(framesSend));
			LocalBroadcastManager.getInstance(context).sendBroadcast(mIntent);

			if(frames.size()==0){
				Log.d(TAG,"All frames are successfully inserted");
				return true;
			}
			Log.d(TAG,"There are frames pending to send... " + frames.size());
			return false;

		} catch (MalformedURLException e) {
			Log.i(TAG,"Error MalformedURL");
		} catch (IOException e) {
			Log.i(TAG,"Error IOException");
		} catch (JSONException e) {
			Log.i(TAG,"Error JSONException");
		}
		return false;
	}

}
