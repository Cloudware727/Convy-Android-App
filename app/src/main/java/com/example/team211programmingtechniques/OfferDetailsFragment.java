package com.example.team211programmingtechniques;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.team211programmingtechniques.database.DBCallback;
import com.example.team211programmingtechniques.database.DBObject;

import java.util.List;

public class OfferDetailsFragment extends Fragment{
    private String itemName;
    private Bitmap photo;
    private boolean status;
    private int itemID;

    private TextView itemNameTV;
    private ImageView photoIV;
    private Button reConvyBtn;
    private LinearLayout offersContainer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_offer_details, container, false);
        Context context =  requireContext();

        // Find views
        photoIV = view.findViewById(R.id.itemImage);
        itemNameTV = view.findViewById(R.id.itemName);
        reConvyBtn = view.findViewById(R.id.btnReConvy);
        offersContainer = view.findViewById(R.id.offersContainer);

        // Get arguments
        if (getArguments() != null) {
            itemName = getArguments().getString("itemName");
            photo = getArguments().getParcelable("photo");
            status = getArguments().getBoolean("status");
            itemID = getArguments().getInt("itemID");
        }

        // Set argument data to views
        photoIV.setImageBitmap(photo);
        itemNameTV.setText(itemName);

        // Logic for button color
        if (status) { // Status = 1 means it is rented out
            reConvyBtn.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.gray));
            reConvyBtn.setFocusable(false);
        }
        else {
            reConvyBtn.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.dark_green));
            reConvyBtn.setFocusable(true);
        }

        // Load offers and populate views
        DBObject db = new DBObject(context);
        db.returnOffers(itemID, new DBCallback<List<String>>() {
            @Override
            public void onSuccessDB(List<String> usernames) {
                for (String username : usernames) {
                    // Create horizontal layout for each offer
                    LinearLayout offerRow = new LinearLayout(context);
                    offerRow.setOrientation(LinearLayout.HORIZONTAL);
                    offerRow.setPadding(0, 16, 0, 16);

                    // Username TextView
                    TextView usernameTV = new TextView(context);
                    usernameTV.setText(username);
                    usernameTV.setTextSize(16);
                    usernameTV.setTextColor(ContextCompat.getColor(context, android.R.color.black));
                    usernameTV.setLayoutParams(new LinearLayout.LayoutParams(
                            0, ViewGroup.LayoutParams.WRAP_CONTENT, 1
                    ));

                    // Accept Button
                    Button acceptBtn = new Button(context);
                    acceptBtn.setText("Accept");
                    acceptBtn.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.dark_green));
                    acceptBtn.setTextColor(ContextCompat.getColor(context, android.R.color.white));
                    acceptBtn.setOnClickListener(v -> {
                        Toast.makeText(context, "Accepted: " + username, Toast.LENGTH_SHORT).show();
                        // TODO: Implement accept logic
                    });

                    // Add views to offerRow
                    offerRow.addView(usernameTV);
                    offerRow.addView(acceptBtn);

                    // Add offerRow to container
                    offersContainer.addView(offerRow);
                }
            }

            @Override
            public void onErrorDB(String error) {
                Toast.makeText(context, "Error loading offers: " + error, Toast.LENGTH_SHORT).show();
                Log.e("OfferDetails", "Offer fetch failed: " + error);
            }
        });


        return view;
    };

}
