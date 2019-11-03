package com.cc.parkx;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class RecyclerViewHorizontalListAdapter extends RecyclerView.Adapter<RecyclerViewHorizontalListAdapter.ParkingSpotViewHolder>{
    private List<ParkingSpot> parkingSpots;
    Context context;

    public RecyclerViewHorizontalListAdapter(List<ParkingSpot> parkingSpots, Context context){
        this.parkingSpots = parkingSpots;
        this.context = context;
    }

    @Override
    public ParkingSpotViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //inflate the layout file
        View parkingSpotView = LayoutInflater.from(parent.getContext()).inflate(R.layout.horizontal_list, parent, false);
        ParkingSpotViewHolder parkingSpotViewHolder = new ParkingSpotViewHolder(parkingSpotView);
        return parkingSpotViewHolder;
    }

    @Override
    public void onBindViewHolder(final ParkingSpotViewHolder holder, final int position) {
        holder.price.setText(String.format("$%.2f", parkingSpots.get(position).price));
        holder.address.setText(parkingSpots.get(position).address);
        holder.distance.setText(String.format("%.2f", parkingSpots.get(position).distance) + " miles");
        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "This", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return parkingSpots.size();
    }

    public class ParkingSpotViewHolder extends RecyclerView.ViewHolder {
        LinearLayout container;
        TextView price, address, distance;
        public ParkingSpotViewHolder(View view) {
            super(view);
            price = view.findViewById(R.id.price_recy);
            address = view.findViewById(R.id.add_recy);
            distance = view.findViewById(R.id.distance_recy);
            container = view.findViewById(R.id.container_recy);
        }
    }
}
