package com.eleganz.msafiri.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.eleganz.msafiri.LocationSearch;
import com.eleganz.msafiri.R;
import com.eleganz.msafiri.model.LocationData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by eleganz on 7/2/19.
 */

public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.MyViewHolder>
        implements Filterable {


    private Context context;
    private List<LocationData> locationList;
    private List<LocationData> locationListFiltered;
    private LocationAdapterListener listener;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name, phone;

        public MyViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.location_name);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // send selected contact in callback
                    listener.onContactSelected(locationListFiltered.get(getAdapterPosition()));
                }
            });
        }
    }


    public LocationAdapter(Context context, List<LocationData> contactList, LocationAdapterListener listener) {
        this.context = context;
        this.listener = listener;
        this.locationList = contactList;
        this.locationListFiltered = contactList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.loc_row_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final LocationData contact = locationListFiltered.get(position);
        holder.name.setText(contact.getAddress());



    }

    @Override
    public int getItemCount() {
        return locationListFiltered.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    locationListFiltered = locationList;
                } else {
                    List<LocationData> filteredList = new ArrayList<>();
                    for (LocationData row : locationList) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.getAddress().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }

                    locationListFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = locationListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                locationListFiltered = (ArrayList<LocationData>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public interface LocationAdapterListener {
        void onContactSelected(LocationData contact);
    }


}
