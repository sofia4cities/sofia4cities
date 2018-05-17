package com.indracompany.sofia2.android.healthcheckapp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FormFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FormFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FormFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private MainActivity mActivity;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private String mAccessToken = "";

    EditText mHeightField;
    EditText mWeightField;
    EditText mSysPressureField;
    EditText mDiaPressureField;
    EditText mCommentField;

    PostToS4CAsyncTask mPostTask;

    private OnFragmentInteractionListener mListener;

    public FormFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FormFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FormFragment newInstance(String param1, String param2) {
        FormFragment fragment = new FormFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_form,
                container, false);

        mActivity = (MainActivity) getActivity();

        mHeightField = (EditText) view.findViewById(R.id.height);
        mWeightField = (EditText) view.findViewById(R.id.weight);
        mSysPressureField = (EditText) view.findViewById(R.id.sys_pressure);
        mDiaPressureField = (EditText) view.findViewById(R.id.dia_pressure);
        mCommentField = (EditText) view.findViewById(R.id.comments);


        Button mButton = (Button) view.findViewById(R.id.store_form_button);
        mButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(mHeightField.getText().length()!=0 &&
                        mWeightField.getText().length()!=0 &&
                        mSysPressureField.getText().length()!=0 &&
                        mDiaPressureField.getText().length()!=0 &&
                        mCommentField.getText().length()!=0){

                    mPostTask = new PostToS4CAsyncTask(mActivity.mAccessToken,
                            mHeightField.getText().toString(),
                            mWeightField.getText().toString(),
                            mSysPressureField.getText().toString(),
                            mDiaPressureField.getText().toString(),
                            mCommentField.getText().toString()
                    );
                    mPostTask.execute((Void) null);
                }
                else{
                    Toast.makeText(getActivity(),"Please fill-in all fields",Toast.LENGTH_SHORT).show();
                }
            }
        });
        return view;
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
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
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public class PostToS4CAsyncTask extends AsyncTask<Void, Void, Integer> {

        private String mAccessToken = "";
        private String mHeight = "";
        private String mWeight = "";
        private String mSys = "";
        private String mDia = "";
        private String mComments = "";

        PostToS4CAsyncTask(String accessToken,
                           String height,
                           String weight,
                           String sys_press,
                           String dia_press,
                           String comments) {
            mAccessToken = accessToken;
            mHeight = height;
            mWeight = weight;
            mSys = sys_press;
            mDia = dia_press;
            mComments = comments;
        }
        @Override
        protected Integer doInBackground(Void... voids) {

            String urlS ="http://s4citiespro.westeurope.cloudapp.azure.com/api-manager/server/api/v1/citizenInterface";
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
                JSONObject citizenHealthData =  new JSONObject();
                citizenHealthData.put("height",Integer.parseInt(mHeight));
                citizenHealthData.put("weight",Integer.parseInt(mWeight));
                citizenHealthData.put("sys_pressure",Integer.parseInt(mSys));
                citizenHealthData.put("dia_pressure",Integer.parseInt(mDia));
                citizenHealthData.put("comments",mComments);
                healthFrame.put("citizenHealthData",citizenHealthData);

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
                mHeightField.setText("");
                mWeightField.setText("");
                mSysPressureField.setText("");
                mDiaPressureField.setText("");
                mCommentField.setText("");
                mHeightField.requestFocus();
                Toast.makeText(getActivity(),"Form stored successfully",Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(getActivity(),"ERROR: "+responseCode,Toast.LENGTH_SHORT).show();
            }
        }
    }


}
