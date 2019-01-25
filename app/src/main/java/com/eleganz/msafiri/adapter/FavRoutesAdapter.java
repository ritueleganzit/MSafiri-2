package com.eleganz.msafiri.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.eleganz.msafiri.R;
import com.eleganz.msafiri.lib.RobotoMediumTextView;

import java.util.ArrayList;

/**
 * Created by eleganz on 10/1/19.
 */

public class FavRoutesAdapter extends RecyclerView.Adapter<FavRoutesAdapter.FavRoutesViewHolder> {


    ArrayList<String> arrayList;
    Context context;

    public FavRoutesAdapter(ArrayList<String> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @Override
    public FavRoutesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.favrouterow,parent,false);
        FavRoutesViewHolder myViewHolder=new FavRoutesViewHolder(v);

        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(FavRoutesViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 4;
    }

    public class FavRoutesViewHolder extends RecyclerView.ViewHolder {
        RobotoMediumTextView fav_des,fav_pickup;
        public FavRoutesViewHolder(View itemView) {
            super(itemView);
            fav_des=itemView.findViewById(R.id.fav_des);
            fav_pickup=itemView.findViewById(R.id.fav_pickup);
        }
    }
}
