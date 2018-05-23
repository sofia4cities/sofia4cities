package com.indracompany.sofia2.android.healthcheckapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
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
import java.util.List;

public class AdviseActivity extends AppCompatActivity {

    String mAccessToken;
    private RecyclerView mRV;
    List<HealthData> mHealthData;
    Button mButton;
    String mInput;
    String mUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advise);

        mAccessToken = getIntent().getStringExtra("accessToken");
        mUsername = getIntent().getStringExtra("username");

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.s4c_logo);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        mButton = (Button) findViewById(R.id.specialist_advise_button);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createAndShowAlertDialog();
            }
        });


        Context context = AdviseActivity.this;
        mRV = (RecyclerView) findViewById(R.id.list_specialist_advise);
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
            else if(responseCode == HttpURLConnection.HTTP_FORBIDDEN){
                Toast.makeText(AdviseActivity.this,"Access revoked by patient",Toast.LENGTH_SHORT).show();
                finish();
            }
            else{
                Toast.makeText(AdviseActivity.this,"Could not connect with S4C Platform",Toast.LENGTH_SHORT).show();
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

    private void createAndShowAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Please input your recommendation");
        // Set up the input
        final TextInputEditText input = new TextInputEditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);
        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                mInput = input.getText().toString().trim();
                new PostToInboxAsyncTask().execute((Void) null);
                dialog.dismiss();
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    class PostToInboxAsyncTask extends AsyncTask<Void, Void, Integer> {


        @Override
        protected Integer doInBackground(Void... voids) {

            String urlS ="http://s4citiespro.westeurope.cloudapp.azure.com/api-manager/server/api/v1/citizenInboxInterface";
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
                connection.setDoOutput(true);
                connection.setRequestMethod("POST");
                connection.setConnectTimeout(10000);
                connection.setReadTimeout(10000);
                connection.setAllowUserInteraction(false);
                connection.setUseCaches(false);
                connection.setRequestProperty("Authorization", "Bearer "+mAccessToken);
                connection.setRequestProperty("Content-Type","application/json");

                JSONObject healthFrame = new JSONObject();
                JSONObject citizenInbox =  new JSONObject();
                citizenInbox.put("feedback",mInput);
                citizenInbox.put("specialist",mUsername);
                healthFrame.put("citizenInbox",citizenInbox);


                OutputStream os = connection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(healthFrame.toString());
                writer.flush();
                writer.close();
                os.close();

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
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return responseCode;
        }

        @Override
        protected void onPostExecute(Integer responseCode) {
            super.onPostExecute(responseCode);
            if(responseCode == HttpURLConnection.HTTP_OK){
                Toast.makeText(AdviseActivity.this,"Feedback sent",Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(AdviseActivity.this,"ERROR: "+responseCode,Toast.LENGTH_SHORT).show();
            }
        }
    }
}
