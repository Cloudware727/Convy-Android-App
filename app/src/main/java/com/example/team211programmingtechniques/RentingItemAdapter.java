package com.example.team211programmingtechniques;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RentingItemAdapter extends RecyclerView.Adapter<RentingItemAdapter.ViewHolder> {

    private List<RentItem> rentingItemList;
    private Context context;

    public RentingItemAdapter(Context context, List<RentItem> rentingItemList) {
        this.context = context;
        this.rentingItemList = rentingItemList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView itemImage;
        TextView itemTitle, itemPrice, itemLocation, itemCategory, itemDistance;
        View coverNumber;
        View coverLocation;

        public ViewHolder(View itemView) {
            super(itemView);
            itemImage = itemView.findViewById(R.id.itemImage);
            itemTitle = itemView.findViewById(R.id.itemTitle);
            itemPrice = itemView.findViewById(R.id.itemPrice);
            itemLocation = itemView.findViewById(R.id.itemLocation);
            itemCategory = itemView.findViewById(R.id.itemCategory);
            itemDistance = itemView.findViewById(R.id.itemDistance);
            coverLocation = itemView.findViewById(R.id.coverLocation);
            coverNumber = itemView.findViewById(R.id.coverNumber);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.rent_ad_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        RentItem item = rentingItemList.get(position);

        holder.itemTitle.setText(item.getItemTitle());
        holder.itemPrice.setText("€" + item.getPrice());
        holder.itemLocation.setText(item.getLocation());
        holder.itemCategory.setText(item.getCategory());
        holder.itemDistance.setText(item.getDistance());
        holder.itemImage.setImageBitmap(item.getPhoto());
        holder.coverLocation.setVisibility(View.GONE);
        holder.coverNumber.setVisibility(View.GONE);

        holder.itemView.setOnClickListener(v -> {
            RentingDetailsFragment detailsFragment = new RentingDetailsFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("item_id", item.getItem_id());
            bundle.putString("item_name", item.getItemTitle());
            bundle.putInt("price", item.getPrice());
            bundle.putString("number", item.getNumber());
            bundle.putString("Location", item.getLocation());
            bundle.putParcelable("photo", item.getPhoto());
            bundle.putString("category", item.getCategory());
            bundle.putString("description", item.getDescription());
            bundle.putBoolean("isAccepted", true); // Always true for renting view
            bundle.putString("DestinationLocation", item.getLocation());
            detailsFragment.setArguments(bundle);

            ((FragmentActivity) context).getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.history_fragment_container, detailsFragment)
                    .addToBackStack(null)
                    .commit();
        });
    }

    @Override
    public int getItemCount() {
        return rentingItemList.size();
    }

    public void setItems(List<RentItem> list) {
        this.rentingItemList = list;
        notifyDataSetChanged();
    }
}

