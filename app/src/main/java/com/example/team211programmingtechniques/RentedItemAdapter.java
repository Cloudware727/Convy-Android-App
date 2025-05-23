package com.example.team211programmingtechniques;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RentedItemAdapter extends RecyclerView.Adapter<RentedItemAdapter.RentedItemViewHolder> {

    private List<HistoryRentedItems> historyRentedItemsList;
    private Context context;

    // Constructor
    public RentedItemAdapter(Context context, List<HistoryRentedItems> historyRentedItemsList) {
        this.context = context;
        this.historyRentedItemsList = historyRentedItemsList;
    }

    // ViewHolder class
    public static class RentedItemViewHolder extends RecyclerView.ViewHolder {
        TextView itemName, itemCategory, dateReturned, itemOwner, itemPrice;

        public RentedItemViewHolder(View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.itemName);
            itemCategory = itemView.findViewById(R.id.itemCategory);
            dateReturned = itemView.findViewById(R.id.dateListed); // Reusing this ID
            itemOwner = itemView.findViewById(R.id.itemOwner);     // Make sure this exists in your XML
            itemPrice = itemView.findViewById(R.id.itemPrice);
        }
    }

    @NonNull
    @Override
    public RentedItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.rented_items_card, parent, false);
        return new RentedItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RentedItemViewHolder holder, int position) {
        HistoryRentedItems item = historyRentedItemsList.get(position);

        holder.itemName.setText(item.getItemName());
        holder.itemCategory.setText(item.getCategory());
        holder.dateReturned.setText("Returned on: " + item.getDateOfReturn());
        holder.itemOwner.setText("Owner: " + item.getLenderUsername());
        holder.itemPrice.setText("€" + item.getPrice());
    }

    @Override
    public int getItemCount() {
        return historyRentedItemsList.size();
    }

    public void setHistoryRentedItemsList(List<HistoryRentedItems> historyRentedItemsList) {
        this.historyRentedItemsList = historyRentedItemsList;
        notifyDataSetChanged();
    }
}

