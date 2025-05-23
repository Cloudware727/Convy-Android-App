package com.example.team211programmingtechniques;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.team211programmingtechniques.database.DBCallback;
import com.example.team211programmingtechniques.database.DBObject;

public class OfferDetailsFragment extends Fragment{
    private String itemName;
    private Bitmap photo;
    private boolean status;

    private TextView itemNameTV;
    private ImageView photoIV;
    private Button reConvyBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_offer_details, container, false);
        Context context =  requireContext();

        // Find views
        photoIV = view.findViewById(R.id.itemImage);
        itemNameTV = view.findViewById(R.id.itemName);
        reConvyBtn = view.findViewById(R.id.btnReConvy);

        // Get arguments
        if (getArguments() != null) {
            itemName = getArguments().getString("itemName");
            photo = getArguments().getParcelable("photo");
            status = getArguments().getBoolean("status");
        }

        // Logic for button color
        if (status) { // Status = 1 means it is rented out
            reConvyBtn.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.gray));
            reConvyBtn.setFocusable(false);
        }
        else {
            reConvyBtn.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.dark_green));
            reConvyBtn.setFocusable(true);
        }

        // Set argument data to views
        photoIV.setImageBitmap(photo);
        itemNameTV.setText(itemName);

        return view;
    };

}
