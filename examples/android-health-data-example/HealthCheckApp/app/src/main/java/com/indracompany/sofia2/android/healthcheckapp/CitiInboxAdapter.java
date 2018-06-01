package com.indracompany.sofia2.android.healthcheckapp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by mbriceno on 18/05/2018.
 */

public class CitiInboxAdapter extends RecyclerView.Adapter<CitiInboxAdapter.RequestItemViewHolder>{

    Context mContext;
    private static final String TAG = MainMenuAdapter.class.getSimpleName();
    private static int viewHolderCount;
    private int mNumberItems;
    private ArrayList<CitiInboxData> mRequests;

    private ListItemClickListener mOnClickListener;

    public interface ListItemClickListener{
        void onListItemClick(int clickedItemId);
    }

    public CitiInboxAdapter(ArrayList<CitiInboxData> itemArray, ListItemClickListener itemClickListener){
        viewHolderCount = 0;
        mNumberItems = itemArray.size();
        mRequests = new ArrayList<>(mNumberItems);
        mRequests = itemArray;
        mOnClickListener = itemClickListener;
    }

    @Override
    public RequestItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        mContext = parent.getContext();
        int layoutIdForItems = R.layout.citi_inbox_item;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForItems, parent, shouldAttachToParentImmediately);
        RequestItemViewHolder viewHolder = new RequestItemViewHolder(view);
        viewHolderCount++;
        Log.d(TAG, "onCreateViewHolder: number of ViewHolders created: "
                + viewHolderCount);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RequestItemViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder");
        holder.mFeedbackTextView.setText(mRequests.get(position).getFeedback());
        holder.mTimestampTextView.setText(mRequests.get(position).getTimestamp());
    }

    @Override
    public int getItemCount() {
        return mNumberItems;
    }

    class RequestItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView mFeedbackTextView;
        TextView mTimestampTextView;

        public RequestItemViewHolder(View itemView) {
            super(itemView);
            mFeedbackTextView = (TextView) itemView.findViewById(R.id.tv_feedback_item);
            mTimestampTextView = (TextView) itemView.findViewById(R.id.tv_timestamp_item);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mOnClickListener.onListItemClick(getAdapterPosition());
        }
    }

}
