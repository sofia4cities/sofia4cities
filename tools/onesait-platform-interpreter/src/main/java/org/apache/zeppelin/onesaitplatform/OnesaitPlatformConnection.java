package org.apache.zeppelin.onesaitplatform;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OnesaitPlatformConnection {
    private String host;
    private int port;
    private String path;
    private String clientPlatform;
    private String token;
    private Long timeout;
    private boolean connected = false;

    private String sessionKey;

    private static final Logger log = LoggerFactory.getLogger(OnesaitPlatformConnection.class);

    private static final String joinTemplate = "/rest/client/join?token=%s&clientPlatform=%s&clientPlatformId=%s";
    private static final String leaveTemplate = "/rest/client/leave";
    private static final String insertTemplate = "/rest/ontology/%s";
    private static final String queryTemplate = "/rest/ontology/%s?query=%s&queryType=%s";
    private static final String clientPlatformId = "NotebookClient";


    public OnesaitPlatformConnection(String host, int port, String path, Long timeout) {
        this.host = host;
        this.port = port;
        this.path = path;
        this.timeout = timeout;
        log.info("Connection Params: " + this.host + ":" + this.port + ":" + this.path + ":" + ":" + this.timeout);
    }
    
    public boolean isConnected() {
    	return this.connected;
    }

    public boolean doJoin(String client, String token) {
        try{
        	this.token = token;
        	this.clientPlatform = client;
            log.info("Doing Join");
            String join = String.format(joinTemplate, this.token , this.clientPlatform, this.clientPlatform + ":Notebook");
            log.info("Join sentence: " + join);
            String resultStr = callRestAPI(join, "GET");
            log.info("Join response: " + resultStr);
            JsonParser jsonParser = new JsonParser();
            JsonObject jsonTree = jsonParser.parse(resultStr).getAsJsonObject();
            
            this.sessionKey = jsonTree.get("sessionKey").getAsString();

            log.info("SessionKey is " + this.sessionKey);
            log.info("Join Ok");
            this.connected = true;
        }
        catch(Exception e){
            log.error("Error en Join: " + e.getMessage());
            return false;
        }
        return true;
    }

    public boolean doLeave() {
        try{
            log.info("Doing Leave");
            String leave = String.format(leaveTemplate);
            log.info("Leave sentence: " + leave);
            callRestAPI(leave, "GET");
            log.info("Leave Ok");
        }
        catch(Exception e){
            log.error("Error en Leave: " + e.getMessage());
            return false;
        }
        return true;
    }

    public String generateURLQuery(String ontology, String query, String queryType) throws UnsupportedEncodingException {
        log.info("Generating Query");
        query = String.format(queryTemplate, ontology, URLEncoder.encode(query, "UTF-8"), queryType);
        log.info("Query sentence: " + query);
        return query;
    }

    public ArrayList<String> doQuery(String query){
        try{
            log.info("Doing Query: " + query);
            String resultResponse = callRestAPI(query, "GET");
            log.info("Response: " + resultResponse);
            return parseSSAPDataArray(resultResponse);
        }
        catch(Exception e){
            log.error("Error en query: " + e.getMessage());
            return null;
        }
        
    }

    public ArrayList<String> doInsert(String ontology, String instances) {
        try{    
            log.info("Doing Insert in " + ontology + " ,instances: " + instances);
            String resultStr = callRestAPI(generateURLInsert(ontology,instances), "POST", instances);
            log.info("Response: " + resultStr);
            ArrayList<String> l = new ArrayList<String>();
            l.add(resultStr);
            return l;
        }
        catch(Exception e){
            log.error("Error en query: " + e.getMessage());
            return null;
        }
    }

    private ArrayList<String> parseSSAPDataArray(String ssapResponse){
        JsonParser jsonParser = new JsonParser();
        JsonArray jsonArray = jsonParser.parse(ssapResponse).getAsJsonArray();
        ArrayList<String> arrayResponse = new ArrayList<String>();
        for(int i=0;i < jsonArray.size(); i++){
            arrayResponse.add(cleanOutput(jsonArray.get(i).toString()));
        }
        return arrayResponse;
    }
    
    private static String cleanOutput(String response) {
    	response = response.replaceAll("\\\"\\{", "\\{");
    	response = response.replaceAll("\\}\\\"", "\\}");
    	response = response.replaceAll("\\\\\"", "\"");
    	return response;
    }

    private String generateURLInsert(String ontology, String instances){
        return String.format(insertTemplate, ontology);
    }

    private String callRestAPI(String targetURL, String method) throws Exception {
        return callRestAPI(targetURL, method, "");
    }
    
    private void addAuthorizationHeader(HttpRequest http) {
    	http.addHeader("Authorization",this.sessionKey);
    }

    private String callRestAPI(String targetURL, String method, String jsonData) throws Exception {
        log.info("Call rest api in {}, method: {}, jsonData: {}", targetURL, method, jsonData);
        HttpClient httpClient = new DefaultHttpClient();
        StringBuffer stb = new StringBuffer();
        try {
            HttpRequest http = null;
            HttpHost httpHost = new HttpHost(this.host, this.port);
            StringEntity entity = new StringEntity(jsonData);
            //targetURL = URLEncoder.encode(targetURL, "UTF-8");
            if("".equals(targetURL)){
                targetURL = this.path;
            }
            else{
                targetURL = this.path + targetURL;
            }
            log.info("Enconded url:" + targetURL);
            switch (method) {
            case "GET":
                http = new HttpGet(targetURL);
                break;
            case "POST":
                http = new HttpPost(targetURL);

                ((HttpPost) http).setEntity(entity);
                http.setHeader("Accept", "application/json");
                http.setHeader("Content-type", "application/json");
                break;
            case "PUT":
                http = new HttpPut(targetURL);
                ((HttpPut) http).setEntity(entity);
                http.setHeader("Accept", "application/json");
                http.setHeader("Content-type", "application/json");
                break;
            case "DELETE":
                http = new HttpDelete(targetURL);
                break;

            }
            
            addAuthorizationHeader(http);

            // Execute HTTP request
            HttpResponse httpResponse = httpClient.execute(httpHost, http);

            log.info("----------------------------------------");
            log.info(httpResponse.getStatusLine().toString());
            log.info("----------------------------------------");

            // Get hold of the response entity
            HttpEntity entityResponse = httpResponse.getEntity();

            // If the response does not enclose an entity, there is no need
            // to bother about connection release
            byte[] buffer = new byte[1024];
            if (entityResponse != null) {
                InputStream inputStream = entityResponse.getContent();
                try {
                    int bytesRead = 0;
                    BufferedInputStream bis = new BufferedInputStream(inputStream);
                    
                    while ((bytesRead = bis.read(buffer)) != -1) {
                        String chunk = new String(buffer, 0, bytesRead);
                        stb.append(chunk);
                    }
                } catch (IOException ioException) {
                    // In case of an IOException the connection will be released
                    // back to the connection manager automatically
                    ioException.printStackTrace();
                } catch (RuntimeException runtimeException) {
                    // In case of an unexpected exception you may want to abort
                    // the HTTP request in order to shut down the underlying
                    // connection immediately.
                    //http.abort();
                    runtimeException.printStackTrace();
                } finally {
                    // Closing the input stream will trigger connection release
                    try {
                        inputStream.close();
                    } catch (Exception ignore) {
                    }
                }
            }
        } catch (ClientProtocolException e) {
            // thrown by httpClient.execute(httpGetRequest)
            e.printStackTrace();
        } catch (IOException e) {
            // thrown by entity.getContent();
            e.printStackTrace();
        } finally {
            httpClient.getConnectionManager().shutdown();
            String stringResponse = stb.toString();
            log.info("Response: " + stringResponse);
            return stringResponse;
        }
    }
}