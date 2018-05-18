package com.indracompany.sofia2.android.healthcheckapp;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.indracompany.sofia2.android.healthcheckapp.HealthFrameFragment.OnListFragmentInteractionListener;
import com.indracompany.sofia2.android.healthcheckapp.dummy.DummyContent.DummyItem;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyHealthFrameRecyclerViewAdapter extends RecyclerView.Adapter<MyHealthFrameRecyclerViewAdapter.ViewHolder> {

    private final List<HealthData> mValues;
    private final OnListFragmentInteractionListener mListener;

    public MyHealthFrameRecyclerViewAdapter(List<HealthData> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
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
        holder.mIdView.setText(String.valueOf(mValues.get(position).getWeight()));
        holder.mContentView.setText(mValues.get(position).getComments());
        holder.mHeightView.setText(String.valueOf(mValues.get(position).getHeight()));
        holder.mTimestampView.setText(mValues.get(position).getTimestamp());

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;
        public final TextView mHeightView;
        public final TextView mTimestampView;
        public final TextView mContentView;
        public HealthData mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.id_tv);
            mContentView = (TextView) view.findViewById(R.id.content_tv);
            mHeightView = (TextView) view.findViewById(R.id.height_tv);
            mTimestampView= (TextView) view.findViewById(R.id.timestamp_tv);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }

}
