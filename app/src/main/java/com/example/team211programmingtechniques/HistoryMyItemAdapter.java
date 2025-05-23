package com.example.team211programmingtechniques;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.team211programmingtechniques.database.DBObject;

import java.util.List;

public class HistoryMyItemAdapter extends RecyclerView.Adapter<HistoryMyItemAdapter.HistoryViewHolder>{
    private List<HistoryMyItem> historyMyItemList;
    private Context context;

    // Constructor to receive data
    public HistoryMyItemAdapter(Context context, List<HistoryMyItem> historyMyItemList) {
        this.context = context;
        this.historyMyItemList = historyMyItemList;
    }

    // 1. ViewHolder: holds view references for recycling
    // Parent class is kept inside child => Only one reference
    public static class HistoryViewHolder extends RecyclerView.ViewHolder {
        TextView itemName, itemCategory, itemDateListed, itemPrice;

        public HistoryViewHolder(View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.itemName);
            itemCategory = itemView.findViewById(R.id.itemCategory);
            itemDateListed = itemView.findViewById(R.id.dateListed);
            itemPrice = itemView.findViewById(R.id.itemPrice);
        }
    }

    // 2. Inflate layout for each card
    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.history_items_card, parent, false);
        return new HistoryViewHolder(view);
    }

    // 3. Bind data to each card
    @Override
    public void onBindViewHolder(HistoryViewHolder holder, int position) {
        HistoryMyItem item = historyMyItemList.get(position);
        int itemId = item.getItem_id();
        // Instantiate for future use
        DBObject db = new DBObject(context);
        // Set the text fields
        holder.itemName.setText(item.getItemName());
        holder.itemCategory.setText(item.getCategory());
        holder.itemDateListed.setText("Date Listed: " + item.getDateListed());
        holder.itemPrice.setText("€" + item.getPrice());

        // Place here the bundle to tie data back to the detailed section
        holder.itemView.setOnClickListener(v -> {
            OfferDetailsFragment newFragment = new OfferDetailsFragment();
            Bundle bundle = new Bundle();
            bundle.putString("itemName", item.getItemName());
            bundle.putParcelable("photo", item.getPhoto());
            bundle.putBoolean("status", item.getStatus());
            newFragment.setArguments(bundle);
            ((FragmentActivity) context).getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, newFragment).addToBackStack(null).commit();
        });
    }

    // Get total items in the list
    @Override
    public int getItemCount() {return historyMyItemList.size();}

    // Set the historyItemList
    public void setHistoryMyItems (List<HistoryMyItem> historyMyItemList) {
        this.historyMyItemList = historyMyItemList;
        notifyDataSetChanged();
    }


}
