package com.eleganz.msafiri.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;

import com.eleganz.msafiri.Confirmation2Activity;
import com.eleganz.msafiri.ConfirmationActivity;
import com.eleganz.msafiri.R;
import com.eleganz.msafiri.model.GetPickup;

import java.util.ArrayList;

public class ChangePickupAdapter extends RecyclerView.Adapter<ChangePickupAdapter.MyViewHolder>
{

    private int lastCheckedPosition = -1;
    ArrayList<GetPickup> arrayList;
    Context context;

    public ChangePickupAdapter(ArrayList<GetPickup> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.change_pickup_layout,parent,false);

        MyViewHolder myViewHolder=new MyViewHolder(v);

        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.select_radioButton.setChecked(position == lastCheckedPosition);
    }

    @Override
    public int getItemCount() {
        return 3;
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

    RadioButton select_radioButton;
    LinearLayout radio;

        public MyViewHolder(View itemView) {
            super(itemView);

            select_radioButton=itemView.findViewById(R.id.select_radioButton);
            radio=itemView.findViewById(R.id.radio);
            select_radioButton.setClickable(false);
            radio.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    lastCheckedPosition = getAdapterPosition();
                     notifyDataSetChanged();
                     context.startActivity(new Intent(context, Confirmation2Activity.class));

                }
                });
        }

    }

}
