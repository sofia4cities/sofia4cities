package com.indracompany.sofia2.android.healthcheckapp;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.indracompany.sofia2.android.healthcheckapp.dummy.DummyContent;
import com.indracompany.sofia2.android.healthcheckapp.dummy.DummyContent.DummyItem;

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

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class HealthFrameFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;
    private MainActivity mainActivity;
    private RecyclerView mRV;
    List<HealthData> mHealthData;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public HealthFrameFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static HealthFrameFragment newInstance(int columnCount) {
        HealthFrameFragment fragment = new HealthFrameFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }

        mainActivity = (MainActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_healthframe_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            mRV = (RecyclerView) view;
            if (mColumnCount <= 1) {
                mRV.setLayoutManager(new LinearLayoutManager(context));
            } else {
                mRV.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            //recyclerView.setAdapter(new MyHealthFrameRecyclerViewAdapter(DummyContent.ITEMS, mListener));
        }

        /*Button mButton = (Button) view.findViewById(R.id.load_history_button);
        mButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                new GetFromS4CAsyncTask().execute((Void) null);
            }
        });*/
        new GetFromS4CAsyncTask().execute((Void) null);
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(HealthData item);
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
                connection.setRequestProperty("Authorization", "Bearer "+mainActivity.mAccessToken);
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
                Toast.makeText(getActivity(),"ERROR: "+responseCode,Toast.LENGTH_SHORT).show();
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
        mRV.setAdapter(new MyHealthFrameRecyclerViewAdapter(mHealthData,mListener));
    }
}
