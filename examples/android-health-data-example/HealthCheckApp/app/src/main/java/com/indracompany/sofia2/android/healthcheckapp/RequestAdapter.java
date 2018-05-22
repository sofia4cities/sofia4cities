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

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.RequestItemViewHolder>{

    Context mContext;
    private static final String TAG = MainMenuAdapter.class.getSimpleName();
    private static int viewHolderCount;
    private int mNumberItems;
    private ArrayList<RequestData> mRequests;

    private ListItemClickListener mOnClickListener;

    public interface ListItemClickListener{
        void onListItemClick(int clickedItemId);
    }

    public RequestAdapter(ArrayList<RequestData> itemArray, ListItemClickListener itemClickListener){
        viewHolderCount = 0;
        mNumberItems = itemArray.size();
        mRequests = new ArrayList<>(mNumberItems);
        mRequests = itemArray;
        mOnClickListener = itemClickListener;
    }

    @Override
    public RequestItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        mContext = parent.getContext();
        int layoutIdForItems = R.layout.request_item;
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
        /*Picasso
                .with(mContext)
                .load(NetworkUtils.POSTERS_BASE_URL+mMoviesArray.get(position).getPosterURL())
                .into(holder.mPosterImageView);*/
        holder.mItemTextView.setText(mRequests.get(position).getUsername());
    }

    @Override
    public int getItemCount() {
        return mNumberItems;
    }

    class RequestItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView mItemTextView;

        public RequestItemViewHolder(View itemView) {
            super(itemView);
            mItemTextView = (TextView) itemView.findViewById(R.id.tv_request_item);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mOnClickListener.onListItemClick(getAdapterPosition());
        }
    }

}
