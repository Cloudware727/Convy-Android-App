package com.example.team211programmingtechniques;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RentItemAdapter extends RecyclerView.Adapter<RentItemAdapter.RentViewHolder> {

    private List<RentItem> rentItemList;
    private Context context;

    // Constructor to receive data
    public RentItemAdapter(Context context, List<RentItem> rentItemList) {
        this.context = context;
        this.rentItemList = rentItemList;
    }

    // 1. ViewHolder: holds view references for recycling
    public static class RentViewHolder extends RecyclerView.ViewHolder {
        ImageView itemImage;
        TextView itemTitle, itemPrice, itemPhone, itemLocation,itemCategory;

        public RentViewHolder(View itemView) {
            super(itemView);
            itemImage = itemView.findViewById(R.id.itemImage);
            itemTitle = itemView.findViewById(R.id.itemTitle);
            itemPrice = itemView.findViewById(R.id.itemPrice);
            itemPhone = itemView.findViewById(R.id.ownerNumber);
            itemLocation = itemView.findViewById(R.id.itemLocation);
            itemCategory = itemView.findViewById(R.id.itemCategory);
        }
    }

    // 2. Inflate layout for each card
    @Override
    public RentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.rent_ad_card, parent, false);
        return new RentViewHolder(view);
    }

    // 3. Bind data to each card
    @Override
    public void onBindViewHolder(RentViewHolder holder, int position) {
        RentItem item = rentItemList.get(position);
        holder.itemTitle.setText(item.getItemTitle());
        holder.itemPrice.setText("€" + item.getPrice());
        holder.itemPhone.setText(item.getNumber());
        holder.itemLocation.setText(item.getLocation());
        holder.itemImage.setImageBitmap(item.getPhoto());
    }

    // 4. Total items in the list
    @Override
    public int getItemCount() {
        return rentItemList.size();
    }

    public void setRentItems(List<RentItem> rentItemList) {
        this.rentItemList = rentItemList;
        notifyDataSetChanged();
    }
}
