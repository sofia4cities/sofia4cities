package com.indracompany.sofia2.android.healthcheckapp;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

public class CitiInboxActivity extends AppCompatActivity implements CitiInboxAdapter.ListItemClickListener{

    private RecyclerView mItemsRV;
    private ArrayList<CitiInboxData> mRequestArray = new ArrayList<>();

    String mAccessToken = "";
    String mUsername = "";
    String mInput = "";

    int clickedElement = 0;
    String clickedId = "";
    private final int MAX_RETRIES = 3;
    int mGetRetries = MAX_RETRIES;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_citi_inbox);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.s4c_logo);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        mAccessToken = getIntent().getStringExtra("accessToken");
        mUsername = getIntent().getStringExtra("username");

        mItemsRV = (RecyclerView) findViewById(R.id.list_citi_inbox);
        mItemsRV.setLayoutManager(new LinearLayoutManager(this));
        mItemsRV.setHasFixedSize(true);
    }

    @Override
    public void onListItemClick(int clickedItemId) {
        clickedElement = clickedItemId;
        Toast.makeText(CitiInboxActivity.this,"Revoke access",Toast.LENGTH_SHORT).show();
        new PostAuthApi().execute("deauthorize");
    }

    @Override
    protected void onResume() {
        super.onResume();
        new GetFromS4CAsyncTask().execute((Void) null);
    }

    class DeleteFromS4CAsyncTask extends AsyncTask<Void, Void, Integer> {

        JSONArray ja = null;

        @Override
        protected Integer doInBackground(Void... voids) {

            String urlS ="http://s4citiespro.westeurope.cloudapp.azure.com/api-manager/server/api/v1/citizenInboxInterface/\\PendingRequests";
            URL url = null;
            int responseCode = 500;
            try {
                url = new URL(urlS);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            try{
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(10000);
                connection.setReadTimeout(10000);
                connection.setAllowUserInteraction(false);
                connection.setUseCaches(false);
                connection.setRequestProperty("Authorization", "Bearer "+mAccessToken);
                connection.setRequestProperty("Accept","application/json");

                connection.connect();
                responseCode = connection.getResponseCode();

                if(connection.getResponseCode() == HttpURLConnection.HTTP_OK){
                    final StringBuilder output = new StringBuilder("Request URL " + url);
                    BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String line = "";
                    StringBuilder responseOutput = new StringBuilder();
                    while((line = br.readLine()) != null ) {
                        responseOutput.append(line);
                    }
                    br.close();
                    connection.disconnect();

                    ja =  new JSONArray(responseOutput.toString());
                    mRequestArray = loadRequestDataFromJson(ja);


                }
                else{
                    int code = connection.getResponseCode();
                    String msg = connection.getResponseMessage();
                    String dummy = connection.getRequestMethod();
                }

            }
            catch (ProtocolException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            catch (JSONException e) {
                e.printStackTrace();
            }

            return responseCode;
        }

        @Override
        protected void onPostExecute(Integer responseCode) {
            super.onPostExecute(responseCode);
            if(responseCode == HttpURLConnection.HTTP_OK){
                loadRequestData();
            }
            else{
                Toast.makeText(CitiInboxActivity.this,"ERROR: "+responseCode,Toast.LENGTH_SHORT).show();
                new GetFromS4CAsyncTask().execute((Void) null);
            }
        }
    }

    public class GetFromS4CAsyncTask extends AsyncTask<Void, Void, Integer> {

        JSONArray ja = null;

        @Override
        protected Integer doInBackground(Void... voids) {

            String urlS ="http://s4citiespro.westeurope.cloudapp.azure.com/api-manager/server/api/v1/citizenInboxInterface/Messages";
            URL url = null;
            int responseCode = 500;
            try {
                url = new URL(urlS);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            try{
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(10000);
                connection.setReadTimeout(10000);
                connection.setAllowUserInteraction(false);
                connection.setUseCaches(false);
                connection.setRequestProperty("Authorization", "Bearer "+mAccessToken);
                connection.setRequestProperty("Accept","application/json");

                connection.connect();
                responseCode = connection.getResponseCode();

                if(connection.getResponseCode() == HttpURLConnection.HTTP_OK){
                    final StringBuilder output = new StringBuilder("Request URL " + url);
                    BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String line = "";
                    StringBuilder responseOutput = new StringBuilder();
                    while((line = br.readLine()) != null ) {
                        responseOutput.append(line);
                    }
                    br.close();
                    connection.disconnect();

                    ja =  new JSONArray(responseOutput.toString());
                    mRequestArray = loadRequestDataFromJson(ja);


                }
                else{
                    int code = connection.getResponseCode();
                    String msg = connection.getResponseMessage();
                    String dummy = connection.getRequestMethod();
                }

            }
            catch (ProtocolException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            catch (JSONException e) {
                e.printStackTrace();
            }

            return responseCode;
        }

        @Override
        protected void onPostExecute(Integer responseCode) {
            super.onPostExecute(responseCode);
            if(responseCode == HttpURLConnection.HTTP_OK){
                loadRequestData();
            }
            else{
                mGetRetries--;
                if(mGetRetries == 0){
                    mGetRetries = MAX_RETRIES;
                    Toast.makeText(CitiInboxActivity.this,"Could not connect to S4C Platform",Toast.LENGTH_SHORT).show();
                    finish();
                }
                else {
                    new GetFromS4CAsyncTask().execute((Void) null);
                }
            }
        }
    }
    public ArrayList<CitiInboxData> loadRequestDataFromJson(JSONArray arrayFromS4c){
        ArrayList<CitiInboxData> mCitiInboxData = new ArrayList<>(arrayFromS4c.length()+1);
        JSONObject data = new JSONObject();
        JSONObject contextData = new JSONObject();

        for(int i=0; i<arrayFromS4c.length();i++){
            CitiInboxData dummyCitiInboxData = new CitiInboxData();
            try{
                data = arrayFromS4c.getJSONObject(i).getJSONObject("value").getJSONObject("citizenInbox");
                contextData = arrayFromS4c.getJSONObject(i).getJSONObject("value").getJSONObject("contextData");
                clickedId = arrayFromS4c.getJSONObject(i).getJSONObject("value").getString("_id");

                dummyCitiInboxData.setSpecialist(data.getString("specialist"));
                dummyCitiInboxData.setFeedback(data.getString("feedback"));
                dummyCitiInboxData.setTimestamp(contextData.getString("timestamp"));

            }
            catch (JSONException e){
                e.printStackTrace();
            }
            mCitiInboxData.add(dummyCitiInboxData);
        }

        return mCitiInboxData;
    }

    public void loadRequestData(){
        mItemsRV.setAdapter(new CitiInboxAdapter(mRequestArray,CitiInboxActivity.this));
    }

    class PostAuthApi extends AsyncTask<String, Void, Integer> {


        @Override
        protected Integer doInBackground(String... params) {

            String command = params[0];

            String data = "https://s4citiespro.westeurope.cloudapp.azure.com/controlpanel/management/"+command+"/api/9ad97800-0992-485c-ad8c-fb573649d7cb/user/specialistHealth";
            String inbox = "https://s4citiespro.westeurope.cloudapp.azure.com/controlpanel/management/"+command+"/api/5eb1559a-f7f3-4e78-8a08-c9da44d71f0e/user/specialistHealth";
            URL urlInbox = null;
            URL urlData = null;
            int responseCode = 500;
            try {
                urlInbox = new URL(inbox);
                urlData = new URL(data);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            try{
                HttpsURLConnection connection = (HttpsURLConnection) urlData.openConnection();
                connection.setDoInput(true);
                connection.setDoOutput(true);
                connection.setRequestMethod("POST");
                connection.setConnectTimeout(10000);
                connection.setReadTimeout(10000);
                connection.setAllowUserInteraction(false);
                connection.setUseCaches(false);
                connection.setHostnameVerifier(DO_NOT_VERIFY);

                connection.setRequestProperty("Authorization", "Bearer "+mAccessToken);
                connection.setRequestProperty("Content-Type","application/json");


                connection.connect();
                responseCode = connection.getResponseCode();

                if(connection.getResponseCode() == HttpURLConnection.HTTP_CREATED ||
                        connection.getResponseCode() ==  HttpURLConnection.HTTP_OK){
                    connection.disconnect();
                    HttpsURLConnection connection2 = (HttpsURLConnection) urlInbox.openConnection();
                    connection2.setDoInput(true);
                    connection2.setDoOutput(true);
                    connection2.setRequestMethod("POST");
                    connection2.setConnectTimeout(10000);
                    connection2.setReadTimeout(10000);
                    connection2.setAllowUserInteraction(false);
                    connection2.setUseCaches(false);
                    connection2.setHostnameVerifier(DO_NOT_VERIFY);

                    connection2.setRequestProperty("Authorization", "Bearer "+mAccessToken);
                    connection2.setRequestProperty("Content-Type","application/json");


                    connection2.connect();
                    responseCode = connection2.getResponseCode();
                    if(connection2.getResponseCode() == HttpURLConnection.HTTP_CREATED ||
                            connection2.getResponseCode() ==  HttpURLConnection.HTTP_OK){
                        return 200;
                    }
                    else{

                    }

                    //Toast.makeText(mActivity.getBaseContext(),"Form stored successfully",Toast.LENGTH_SHORT);
                }
                else{
                    int code = connection.getResponseCode();
                    String msg = connection.getResponseMessage();
                    String dummy = connection.getRequestMethod();
                    //Toast.makeText(mActivity.getBaseContext(),"Connection ERROR",Toast.LENGTH_SHORT);
                }

            }
            catch (ProtocolException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            // TODO: register the new account here.
            return responseCode;
        }

        @Override
        protected void onPostExecute(Integer responseCode) {
            super.onPostExecute(responseCode);
            if(responseCode == HttpURLConnection.HTTP_OK){
                Toast.makeText(CitiInboxActivity.this,"Access revoked",Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(CitiInboxActivity.this,"Could not connect to S4C Platform",Toast.LENGTH_SHORT).show();
            }
        }
    }

    final static HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    };
}
