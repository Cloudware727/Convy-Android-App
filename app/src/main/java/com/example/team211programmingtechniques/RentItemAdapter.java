package com.example.team211programmingtechniques;

import static androidx.core.content.ContentProviderCompat.requireContext;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.team211programmingtechniques.database.DBCallback;
import com.example.team211programmingtechniques.database.DBObject;

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
        TextView itemTitle, itemPrice, itemPhone, itemLocation,itemCategory,itemDistance;
        View coverNumber, coverLocation;

        public RentViewHolder(View itemView) {
            super(itemView);
            itemImage = itemView.findViewById(R.id.itemImage);
            itemTitle = itemView.findViewById(R.id.itemTitle);
            itemPrice = itemView.findViewById(R.id.itemPrice);
            itemPhone = itemView.findViewById(R.id.ownerNumber);
            itemLocation = itemView.findViewById(R.id.itemLocation);
            itemCategory = itemView.findViewById(R.id.itemCategory);
            itemDistance = itemView.findViewById(R.id.itemDistance);
            coverLocation = itemView.findViewById(R.id.coverLocation);
            coverNumber = itemView.findViewById(R.id.coverNumber);
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
        int itemId = item.getItem_id(); // ID from your RentItem model

        DBObject dbObject = new DBObject(context);

        SharedPreferences prefs = context.getSharedPreferences("user_prefs",Context.MODE_PRIVATE);
        String userLocation = prefs.getString("Location", null); // e.g., "Brussels"
        String itemLocation = item.getLocation(); // from RentItem

        dbObject.getDistanceBetween(userLocation, itemLocation, new DBCallback<String>() {
            @Override
            public void onSuccessDB(String result) {
                item.setDistance(result);
                holder.itemDistance.setText(result);
            }

            @Override
            public void onErrorDB(String error) {
                holder.itemDistance.setText("Distance unavailable");
                Log.e("Distance", "Error: " + error);
            }
        });


        dbObject.CheckIfUserIsAccepted(new DBCallback<Boolean>() {
            @Override
            public void onSuccessDB(Boolean isAccepted) {
                item.setIsAccepted(isAccepted);
                if (isAccepted) {
                    holder.coverNumber.setVisibility(View.GONE);
                    holder.coverLocation.setVisibility(View.GONE);
                } else {
                    holder.coverNumber.setVisibility(View.VISIBLE);
                    holder.coverLocation.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onErrorDB(String error) {
                // Optional: show an error or default to hidden
                holder.coverNumber.setVisibility(View.VISIBLE);
                holder.coverLocation.setVisibility(View.VISIBLE);
                Log.e("Adapter", "Error checking acceptance: " + error);
            }
        }, itemId);

        holder.itemTitle.setText(item.getItemTitle());
        holder.itemPrice.setText("€" + item.getPrice());
        holder.itemPhone.setText(item.getNumber());
        holder.itemLocation.setText(item.getLocation());
        holder.itemImage.setImageBitmap(item.getPhoto());
        holder.itemCategory.setText(item.getCategory());

        holder.itemView.setOnClickListener(v ->{
            RentDetailsFragment detailsFragment = new RentDetailsFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("item_id",item.getItem_id());
            bundle.putString("item_name",item.getItemTitle());
            bundle.putInt("price",item.getPrice());
            bundle.putString("number",item.getNumber());
            bundle.putString("Location",item.getLocation());
            bundle.putParcelable("photo",item.getPhoto());
            bundle.putString("category", item.getCategory());
            bundle.putString("description",item.getDescription());
            bundle.putBoolean("isAccepted",item.getIsAccepted());
            bundle.putString("DestinationLocation",item.getLocation());
            detailsFragment.setArguments(bundle);

            ((FragmentActivity) context).getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, detailsFragment).addToBackStack(null).commit();

        });
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
