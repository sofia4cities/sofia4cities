package com.indracompany.sofia2.android.healthcheckapp;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class HistActivity extends AppCompatActivity {

    String mAccessToken;
    private RecyclerView mRV;
    List<HealthData> mHealthData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hist);

        mAccessToken = getIntent().getStringExtra("accessToken");

        Context context = HistActivity.this;
        mRV = (RecyclerView) findViewById(R.id.list);
        mRV.setLayoutManager(new LinearLayoutManager(context));

        new GetFromS4CAsyncTask().execute((Void) null);

    }

    public class GetFromS4CAsyncTask extends AsyncTask<Void, Void, Integer> {

        JSONArray ja = null;

        @Override
        protected Integer doInBackground(Void... voids) {

            String urlS ="http://s4citiespro.westeurope.cloudapp.azure.com/api-manager/server/api/v1/citizenInterface/\\HistoricalData";
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
                    mHealthData = loadHealthDataFromJson(ja);


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

            // TODO: register the new account here.
            return responseCode;
        }

        @Override
        protected void onPostExecute(Integer responseCode) {
            super.onPostExecute(responseCode);
            if(responseCode == HttpURLConnection.HTTP_OK){
                loadHealthData();
            }
            else{
                Toast.makeText(HistActivity.this,"ERROR: "+responseCode,Toast.LENGTH_SHORT).show();
                new GetFromS4CAsyncTask().execute((Void) null);
            }
        }
    }
    public List<HealthData> loadHealthDataFromJson(JSONArray arrayFromS4c){
        List<HealthData> mHealthData = new ArrayList<>(arrayFromS4c.length()+1);
        JSONObject data = new JSONObject();
        JSONObject contextData = new JSONObject();

        for(int i=0; i<arrayFromS4c.length();i++){
            HealthData dummyHealthData = new HealthData();
            try{
                data = arrayFromS4c.getJSONObject(i).getJSONObject("value").getJSONObject("citizenHealthData");
                contextData = arrayFromS4c.getJSONObject(i).getJSONObject("value").getJSONObject("contextData");

                dummyHealthData.setComments(data.getString("comments"));
                dummyHealthData.setDiaPressure(data.getInt("dia_pressure"));
                dummyHealthData.setSysPressure(data.getInt("sys_pressure"));
                dummyHealthData.setHeight(data.getInt("height"));
                dummyHealthData.setWeight(data.getInt("weight"));
                dummyHealthData.setTimestamp(contextData.getString("timestamp"));
            }
            catch (JSONException e){
                e.printStackTrace();
            }
            mHealthData.add(dummyHealthData);
        }

        return mHealthData;
    }

    public void loadHealthData(){
        mRV.setAdapter(new MyHealthFrameRecyclerViewAdapter(mHealthData));
    }
}
