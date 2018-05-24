package com.indracompany.sofia2.android.healthcheckapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

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

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

public class FeedbackActivity extends AppCompatActivity {

    String mAccessToken = "";
    String mUsername = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        mAccessToken = getIntent().getStringExtra("accessToken");
        mUsername = getIntent().getStringExtra("username");

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.s4c_logo);
        getSupportActionBar().setDisplayUseLogoEnabled(true);


        Button mIB = (Button) findViewById(R.id.ib_feedback);
        mIB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createAndShowAlertDialog();

            }
        });

        Button mIB2 = (Button) findViewById(R.id.ib_inbox);
        mIB2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mIntent = new Intent(FeedbackActivity.this,CitiInboxActivity.class);
                mIntent.putExtra("accessToken",mAccessToken);
                mIntent.putExtra("username",mUsername);
                startActivity(mIntent);
            }
        });

    }

    private void createAndShowAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("GDPR Warning");
        builder.setMessage("By proceeding you are accepting sharing your data with the selected specialist. You can revoke this at anytime");
        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                /*Intent firstIntent = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(firstIntent);*/
                new PostToInboxAsyncTask().execute((Void) null);

                dialog.dismiss();
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    class PostToInboxAsyncTask extends AsyncTask<Void, Void, Integer> {


        @Override
        protected Integer doInBackground(Void... voids) {

            String urlS ="http://s4citiespro.westeurope.cloudapp.azure.com/api-manager/server/api/v1/specialistInterface";
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
                JSONObject specialistInbox =  new JSONObject();
                specialistInbox.put("patient","9ad97800-0992-485c-ad8c-fb573649d7cb");
                specialistInbox.put("pending","true");
                healthFrame.put("specialistInbox",specialistInbox);

                //{"citizenHealthData":{ "height":1,"weight":28.6,"sys_pressure":1,"dia_pressure":1,"comments":"string"}}

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
            } catch (JSONException e) {
                e.printStackTrace();
            }

            // TODO: register the new account here.
            return responseCode;
        }

        @Override
        protected void onPostExecute(Integer responseCode) {
            super.onPostExecute(responseCode);
            if(responseCode == HttpURLConnection.HTTP_OK){
                new PostAuthApi().execute("authorize");
            }
            else{
                Toast.makeText(FeedbackActivity.this,"Could not connect to S4C Platform",Toast.LENGTH_SHORT).show();
            }
        }
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
                Toast.makeText(FeedbackActivity.this,"Request sent & data access granted",Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(FeedbackActivity.this,"Could not connect to S4C Platform"+responseCode,Toast.LENGTH_SHORT).show();
            }
        }
    }

    final static HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    };
}
