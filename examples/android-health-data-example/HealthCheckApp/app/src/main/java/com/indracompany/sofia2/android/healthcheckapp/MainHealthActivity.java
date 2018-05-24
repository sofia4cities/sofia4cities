package com.indracompany.sofia2.android.healthcheckapp;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;

public class MainHealthActivity extends AppCompatActivity implements MainMenuAdapter.ListItemClickListener {

    private MainMenuAdapter mAdapter;
    private RecyclerView mItemsRV;
    private ArrayList<MainItem> mMainItemsArray = new ArrayList<>();
    protected String mAccessToken = "";
    protected String mUsername = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_health);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.s4c_logo);
        getSupportActionBar().setDisplayUseLogoEnabled(true);


        mAccessToken = getIntent().getStringExtra("accessToken");
        mUsername = getIntent().getStringExtra("username");

        loadMenuItems();
        mItemsRV = (RecyclerView) findViewById(R.id.list_main);
        LinearLayoutManager layoutManager = new LinearLayoutManager(MainHealthActivity.this);
        mItemsRV.setLayoutManager(layoutManager);
        mItemsRV.setHasFixedSize(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mAdapter = new MainMenuAdapter(mMainItemsArray,this);
        mItemsRV.setAdapter(mAdapter);
    }

    private void loadMenuItems() {
        for(int i=0; i<3; i++){
            mMainItemsArray.add(new MainItem());
        }

       // mMainItemsArray.get(0).setDescription("Sensor");
        //mMainItemsArray.get(0).setImageId(R.drawable.ic_bluetooth);
        mMainItemsArray.get(0).setDescription("FILL-IN FORM");
        mMainItemsArray.get(0).setImageId(R.drawable.ic_003_forms);
        mMainItemsArray.get(1).setDescription("HISTORICAL DATA");
        mMainItemsArray.get(1).setImageId(R.drawable.ic_002_graph);
        mMainItemsArray.get(2).setDescription("SPECIALIST'S FEEDBACK");
        mMainItemsArray.get(2).setImageId(R.drawable.ic_syringe);




    }

    @Override
    public void onListItemClick(int clickedItemId) {
        Intent mIntent = null;
        switch(clickedItemId){
            case -1:
                Toast.makeText(this, mAccessToken,Toast.LENGTH_SHORT).show();
                break;
            case 0:
                mIntent = new Intent(MainHealthActivity.this,FormActivity.class);
                mIntent.putExtra("accessToken",mAccessToken);
                startActivity(mIntent);
                break;
            case 1:
                mIntent = new Intent(MainHealthActivity.this,HistActivity.class);
                mIntent.putExtra("accessToken",mAccessToken);
                startActivity(mIntent);
                break;
            case 2:
                mIntent = new Intent(MainHealthActivity.this,FeedbackActivity.class);
                mIntent.putExtra("accessToken",mAccessToken);
                mIntent.putExtra("username",mUsername);
                startActivity(mIntent);
                break;
        }


    }
}
