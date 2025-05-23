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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class OfferDetailsFragment extends Fragment{
    private String itemName, myUsername;
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
        myUsername = requireActivity()
                .getSharedPreferences("user_prefs",requireActivity().MODE_PRIVATE)
                .getString("username", null);

        // Set argument data to views
        photoIV.setImageBitmap(photo);
        itemNameTV.setText(itemName);

        // Logic for button color
        if (status) { // Status = 1 means it is rented out
            reConvyBtn.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.dark_green));
            reConvyBtn.setFocusable(true);
        }
        else {
            reConvyBtn.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.gray));
            reConvyBtn.setClickable(false);
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
                    acceptBtn.setText("Loading...");
                    acceptBtn.setTextColor(ContextCompat.getColor(context, android.R.color.white));
                    acceptBtn.setLayoutParams(new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
                    ));
                    acceptBtn.setOnClickListener(v -> acceptOffer(username, acceptBtn));

                    // Add views to offerRow
                    offerRow.addView(usernameTV);
                    offerRow.addView(acceptBtn);

                    // Add offerRow to container
                    offersContainer.addView(offerRow);

                    // Check offer status and configure button
                    db.returnOfferStatus(itemID, username, new DBCallback<Integer>() {
                        @Override
                        public void onSuccessDB(Integer offerStatus) {
                            if (offerStatus == 0) {
                                // Offer has not been accepted
                                acceptBtn.setText("Accept");
                                acceptBtn.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.dark_green));
                                acceptBtn.setClickable(true);
                            } else {
                                // Offer already accepted
                                acceptBtn.setText("Accepted");
                                acceptBtn.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.gray));
                                acceptBtn.setClickable(false);
                            }
                        }

                        @Override
                        public void onErrorDB(String error) {
                            Toast.makeText(context, "Error loading status for " + username, Toast.LENGTH_SHORT).show();
                            Log.e("OfferStatus", "Status fetch failed: " + error);
                            // Fallback: disable the button if status fails
                            acceptBtn.setText("Unavailable");
                            acceptBtn.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.gray));
                            acceptBtn.setClickable(false);
                        }
                    });
                }
            }

            @Override
            public void onErrorDB(String error) {
                Toast.makeText(context, "Error loading offers: " + error, Toast.LENGTH_SHORT).show();
                Log.e("OfferDetails", "Offer fetch failed: " + error);
            }
        });

        // Set click listener calls the method
        reConvyBtn.setOnClickListener(v -> reConveyItem());

        return view;
    };

    // Method for button when accepting offers
    private void acceptOffer(String username, Button acceptBtn) {
        Log.d("OfferDetails", "acceptOffer called for username: " + username);

        acceptBtn.setEnabled(false);
        DBObject db = new DBObject(requireContext());

        db.updateOfferStatus(1, itemID, username, new DBCallback<Void>() {
            @Override
            public void onSuccessDB(Void unused) {
                Log.d("OfferDetails", "updateOfferStatus success");
                db.updateItemStatus(1, itemID, new DBCallback<String>() {
                    @Override
                    public void onSuccessDB(String message) {
                        Log.d("OfferDetails", "updateItemStatus success");
                        db.deleteOtherOffers(itemID, username, new DBCallback<String>() {
                            @Override
                            public void onSuccessDB(String message) {
                                Log.d("OfferDetails", "deleteOtherOffers success");
                                Toast.makeText(requireContext(), "Offer accepted!", Toast.LENGTH_SHORT).show();

                                acceptBtn.setText("Accepted");
                                acceptBtn.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.gray));
                                acceptBtn.setClickable(false);
                            }

                            @Override
                            public void onErrorDB(String error) {
                                Log.e("OfferDetails", "deleteOtherOffers error: " + error);
                                Toast.makeText(requireContext(), "Failed to delete other offers: " + error, Toast.LENGTH_SHORT).show();
                                acceptBtn.setEnabled(true);
                            }
                        });
                    }

                    @Override
                    public void onErrorDB(String error) {
                        Log.e("OfferDetails", "updateItemStatus error: " + error);
                        Toast.makeText(requireContext(), "Failed to update item status: " + error, Toast.LENGTH_SHORT).show();
                        acceptBtn.setEnabled(true);
                    }
                });
            }

            @Override
            public void onErrorDB(String error) {
                Log.e("OfferDetails", "updateOfferStatus error: " + error);
                Toast.makeText(requireContext(), "Failed to update offer status: " + error, Toast.LENGTH_SHORT).show();
                acceptBtn.setEnabled(true);
            }
        });
    }
    // Method for re-uploading an item into the market

    private void reConveyItem() {
        if (!status) {
            Toast.makeText(requireContext(), "Item is not rented out.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Use array to hold renter username for inner class access
        final String[] renterUsernameHolder = new String[1];
        renterUsernameHolder[0] = null;

        for (int i = 0; i < offersContainer.getChildCount(); i++) {
            View row = offersContainer.getChildAt(i);
            if (row instanceof LinearLayout) {
                LinearLayout ll = (LinearLayout) row;
                TextView usernameTV = (TextView) ll.getChildAt(0);
                Button acceptBtn = (Button) ll.getChildAt(1);

                if (acceptBtn.getText().toString().equals("Accepted")) {
                    renterUsernameHolder[0] = usernameTV.getText().toString();
                    break;
                }
            }
        }

        if (renterUsernameHolder[0] == null) {
            Toast.makeText(requireContext(), "No accepted renter found.", Toast.LENGTH_SHORT).show();
            return;
        }

        DBObject dbObj = new DBObject(requireContext());
        String lenderUsername = myUsername;

        // Get today's date with SimpleDateFormat (API 24 compatible)
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String todayDate = sdf.format(new Date());

        reConvyBtn.setEnabled(false);

        dbObj.deleteCertainOffer(renterUsernameHolder[0], itemID, new DBCallback<String>() {
            @Override
            public void onSuccessDB(String message) {
                dbObj.addTransaction(itemID, renterUsernameHolder[0], lenderUsername, todayDate, new DBCallback<String>() {
                    @Override
                    public void onSuccessDB(String message) {
                        dbObj.updateItemStatus(0, itemID, new DBCallback<String>() {
                            @Override
                            public void onSuccessDB(String message) {
                                Toast.makeText(requireContext(), "Item reconveyed successfully!", Toast.LENGTH_SHORT).show();
                                reConvyBtn.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.gray));
                                reConvyBtn.setClickable(false);
                            }

                            @Override
                            public void onErrorDB(String error) {
                                Toast.makeText(requireContext(), "Failed to update item status: " + error, Toast.LENGTH_SHORT).show();
                                reConvyBtn.setEnabled(true);
                            }
                        });
                    }

                    @Override
                    public void onErrorDB(String error) {
                        Toast.makeText(requireContext(), "Failed to add transaction: " + error, Toast.LENGTH_SHORT).show();
                        reConvyBtn.setEnabled(true);
                    }
                });
            }

            @Override
            public void onErrorDB(String error) {
                Toast.makeText(requireContext(), "Failed to delete certain offer: " + error, Toast.LENGTH_SHORT).show();
                reConvyBtn.setEnabled(true);
            }
        });
    }
}
