package com.indracompany.sofia2.android.healthcheckapp;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.indracompany.sofia2.android.healthcheckapp.HealthFrameFragment.OnListFragmentInteractionListener;


import java.util.List;


public class MyHealthFrameRecyclerViewAdapter extends RecyclerView.Adapter<MyHealthFrameRecyclerViewAdapter.ViewHolder> {

    private final List<HealthData> mValues;
    //private final OnListFragmentInteractionListener mListener;

    public MyHealthFrameRecyclerViewAdapter(List<HealthData> items) {
        mValues = items;
        //mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_healthframe, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mWeightView.setText(String.valueOf(mValues.get(position).getWeight()));
        holder.mCommentsView.setText(mValues.get(position).getComments());
        holder.mHeightView.setText(String.valueOf(mValues.get(position).getHeight()));
        holder.mTimestampView.setText(mValues.get(position).getTimestamp());
        holder.mSysPressureView.setText(String.valueOf(mValues.get(position).getSysPressure()));
        holder.mDiaPressureView.setText(String.valueOf(mValues.get(position).getDiaPressure()));

        /*holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });*/
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mHeightView;
        public final TextView mWeightView;
        public final TextView mSysPressureView;
        public final TextView mDiaPressureView;
        public final TextView mTimestampView;
        public final TextView mCommentsView;
        public HealthData mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mWeightView = (TextView) view.findViewById(R.id.weight_tv);
            mHeightView = (TextView) view.findViewById(R.id.height_tv);
            mSysPressureView = (TextView) view.findViewById(R.id.sys_pressure_tv);
            mDiaPressureView = (TextView) view.findViewById(R.id.dia_pressure_tv);
            mCommentsView = (TextView) view.findViewById(R.id.comments_tv);
            mTimestampView= (TextView) view.findViewById(R.id.timestamp_tv);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mCommentsView.getText() + "'";
        }
    }

}
