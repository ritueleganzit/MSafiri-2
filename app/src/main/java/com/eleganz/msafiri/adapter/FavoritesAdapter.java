package com.eleganz.msafiri.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Toast;

import com.eleganz.msafiri.Confirmation2Activity;
import com.eleganz.msafiri.EditPlaceActivity;
import com.eleganz.msafiri.R;
import com.eleganz.msafiri.lib.RobotoMediumTextView;
import com.eleganz.msafiri.model.Addressdata;
import com.eleganz.msafiri.utils.ApiInterface;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

import static com.eleganz.msafiri.utils.Constant.BASEURL;

public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.MyViewHolder>
{
    ArrayList<Addressdata> arrayList;
    Context context;
    RecyclerView recyclerView;

    public FavoritesAdapter(RecyclerView fav,ArrayList<Addressdata> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
        recyclerView=fav;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.fav_layout,parent,false);
        MyViewHolder myViewHolder=new MyViewHolder(v);

        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
final Addressdata addressdata=arrayList.get(position);

holder.title1.setText(addressdata.getTitle());
holder.address1.setText(addressdata.getAddress());
holder.favlinear.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        context.startActivity(new Intent(context, EditPlaceActivity.class)
        .putExtra("id",addressdata.getId())
        .putExtra("title",addressdata.getTitle()));
    }
});

        holder.delete1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // Toast.makeText(context, ""+addressdata.getId(), Toast.LENGTH_SHORT).show();

                final StringBuilder stringBuilder=new StringBuilder();
                RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(BASEURL).build();
                final ApiInterface apiInterface = restAdapter.create(ApiInterface.class);
                apiInterface.deletemyAddress(addressdata.getId(), new Callback<Response>() {
                    @Override
                    public void success(Response response, Response response2) {
                        try {
                            BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(response.getBody().in()));
                            String line;
                            while ((line=bufferedReader.readLine())!=null)
                            {
                                stringBuilder.append(line);
                            }


                            arrayList.remove(position);
                            recyclerView.removeViewAt(position);
                            notifyItemRemoved(position);
                            notifyItemRangeChanged(position, arrayList.size());
                            //notifyItemChanged(position);

                            //notifyDataSetChanged();
                            Log.d("adapterData",""+stringBuilder);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void failure(RetrofitError error) {

                    }
                });




            }
        });

    }

    @Override
    public int getItemCount() {
        return arrayList.size();

    }

    public class MyViewHolder extends RecyclerView.ViewHolder {


        RobotoMediumTextView title1,address1,delete1;
        LinearLayout favlinear;
        public MyViewHolder(View itemView) {
            super(itemView);

            favlinear=itemView.findViewById(R.id.favlinear);
title1=itemView.findViewById(R.id.title1);
            address1=itemView.findViewById(R.id.address1);
            delete1=itemView.findViewById(R.id.delete1);
        }

    }
}
