package com.indracompany.sofia2.android.healthcheckapp;

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

public class SpecialistActivity extends AppCompatActivity implements RequestAdapter.ListItemClickListener  {

    private RequestAdapter mAdapter;
    private RecyclerView mItemsRV;
    private ArrayList<RequestData> mRequestArray = new ArrayList<>();
    List<RequestData> mRequestData;

    String mAccessToken = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_specialist);

        mAccessToken = getIntent().getStringExtra("accessToken");

        mItemsRV = (RecyclerView) findViewById(R.id.list_main);
        mItemsRV.setLayoutManager(new LinearLayoutManager(this));
        mItemsRV.setHasFixedSize(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        new GetFromS4CAsyncTask().execute((Void) null);
    }

    @Override
    public void onListItemClick(int clickedItemId) {

    }

    public class GetFromS4CAsyncTask extends AsyncTask<Void, Void, Integer> {

        JSONArray ja = null;

        @Override
        protected Integer doInBackground(Void... voids) {

            String urlS ="http://s4citiespro.westeurope.cloudapp.azure.com/api-manager/server/api/v1/specialistInterface/\\PendingRequests";
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
                    mRequestData = loadRequestDataFromJson(ja);


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
                loadRequestData();
            }
            else{
                Toast.makeText(SpecialistActivity.this,"ERROR: "+responseCode,Toast.LENGTH_SHORT).show();
                new GetFromS4CAsyncTask().execute((Void) null);
            }
        }
    }
    public List<RequestData> loadRequestDataFromJson(JSONArray arrayFromS4c){
        List<RequestData> mRequestData = new ArrayList<>(arrayFromS4c.length()+1);
        JSONObject data = new JSONObject();
        JSONObject contextData = new JSONObject();

        for(int i=0; i<arrayFromS4c.length();i++){
            RequestData dummyRequestData = new RequestData();
            try{
                data = arrayFromS4c.getJSONObject(i).getJSONObject("value").getJSONObject("specialistInbox");
                contextData = arrayFromS4c.getJSONObject(i).getJSONObject("value").getJSONObject("contextData");

                dummyRequestData.setUsername(data.getString("username"));
                dummyRequestData.setPending(data.getString("pending"));

            }
            catch (JSONException e){
                e.printStackTrace();
            }
            mRequestData.add(dummyRequestData);
        }

        return mRequestData;
    }

    public void loadRequestData(){
        mItemsRV.setAdapter(mAdapter);
    }
}
