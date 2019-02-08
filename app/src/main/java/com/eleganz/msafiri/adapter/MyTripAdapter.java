package com.eleganz.msafiri.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.eleganz.msafiri.ConfirmationActivity;
import com.eleganz.msafiri.R;
import com.eleganz.msafiri.TripActivity;
import com.eleganz.msafiri.lib.RobotoMediumTextView;
import com.eleganz.msafiri.model.TripData;
import com.eleganz.msafiri.utils.HistoryData;
import com.eleganz.msafiri.utils.SquareImageView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static com.google.android.gms.plus.PlusOneDummyView.TAG;

public class MyTripAdapter extends RecyclerView.Adapter<MyTripAdapter.MyViewHolder>
{
    ArrayList<HistoryData> arrayList;
    Context context;

    public MyTripAdapter(ArrayList<HistoryData> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.yourtriprow,parent,false);

        MyViewHolder myViewHolder=new MyViewHolder(v);

        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        final HistoryData historyData=arrayList.get(position);
        if (historyData.getUser_trip_status().equalsIgnoreCase("booked")){
            holder.tvongoing.setVisibility(View.VISIBLE);
            blink(holder.tvongoing);
        }

        else {
            holder.tvongoing.setVisibility(View.GONE);
        }

        holder.desloc.setText(""+historyData.getTo_title());
        holder.pickuploc.setText(""+historyData.getFrom_title());
        holder.from_date.setText(""+parseDateToddMMyyyy2(historyData.getDate()));
        holder.to_date.setText(""+parseDateToddMMyyyy2(historyData.getEnd_datetime()));
holder.fullname.setText(""+historyData.getFullname());
holder.vehicle_name.setText("Vehicle Name: "+historyData.getVehicle_name());
        Glide.with(context)
                .load(historyData.getTrip_screenshot())
                .into(holder.squareImageView);

holder.trip_price.setText(""+historyData.getTrip_price());

        holder.main_card.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View view) {
                Activity activity=(Activity) context;
                context.startActivity(new Intent(context, TripActivity.class)
                        .putExtra("historyData",historyData)






                );

            }
        });

    }
    public void blink(final View v) {
        final Handler handler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                int timeToBlink = 600;    //in milissegunds
                try{Thread.sleep(timeToBlink);}catch (Exception e) {}
                handler.post(new Runnable() {
                    @Override
                    public void run() {

                        if(v.getVisibility() == View.VISIBLE){
                            v.setVisibility(View.INVISIBLE);
                        }else{
                            v.setVisibility(View.VISIBLE);
                        }
                        blink(v);
                    }
                });
            }
        }).start();
    }
    public String parseDateToddMMyyyy2(String time)   {
        String inputPattern = "yyyy-MM-dd HH:mm:ss";
        String outputPattern = "dd/MM/yy 'at' hh:mm a";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

        Date date = null;
        String str = null;

        try {
            date = inputFormat.parse(time);
            str = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }
    @Override
    public int getItemCount() {
        return arrayList.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {


        CardView main_card;
        TextView pickuploc,desloc,to_date,from_date;
TextView tvongoing;
        RobotoMediumTextView trip_price,fullname,vehicle_name;
SquareImageView squareImageView;
        public MyViewHolder(View itemView) {
            super(itemView);

            main_card=itemView.findViewById(R.id.main_card);
            tvongoing=itemView.findViewById(R.id.tvongoing);
            squareImageView=itemView.findViewById(R.id.squareImageView);
            pickuploc=itemView.findViewById(R.id.pickuploc);
            desloc=itemView.findViewById(R.id.desloc);
            to_date=itemView.findViewById(R.id.to_date);
            from_date=itemView.findViewById(R.id.from_date);
            trip_price=itemView.findViewById(R.id.trip_price);
            fullname=itemView.findViewById(R.id.fullname_dr);
            vehicle_name=itemView.findViewById(R.id.vehicle_name_dr);


        }

    }
}
