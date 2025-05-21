package com.example.team211programmingtechniques;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.team211programmingtechniques.database.DBCallback;
import com.example.team211programmingtechniques.database.DBObject;

import java.util.List;

public class RentDetailsFragment extends Fragment {

    private String itemName, number, location,description,category;
    private Bitmap photo;
    private int price;
    private Boolean isAccepted;
    private ImageView imageView;
    private TextView nameText, priceText, numberText, locationText, descriptionText,categoryText;
    private View coverNumberView,coverLocationView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rent_details, container, false);

        // Find views
        imageView = view.findViewById(R.id.detailImage);
        nameText = view.findViewById(R.id.detailTitle);
        priceText = view.findViewById(R.id.detailPrice);
        numberText = view.findViewById(R.id.detailNumber);
        locationText = view.findViewById(R.id.detailLocation);
        descriptionText = view.findViewById(R.id.detailDescription);
        categoryText = view.findViewById(R.id.detailCategory);
        coverNumberView = view.findViewById(R.id.coverDetailNumber);
        coverLocationView = view.findViewById(R.id.coverDetailLocation);
        



        // Get arguments and check if they are empty
        if (getArguments() != null) {
            itemName = getArguments().getString("item_name");
            photo = getArguments().getParcelable("photo");
            price = getArguments().getInt("price");
            number = getArguments().getString("number");
            location = getArguments().getString("Location");
            description = getArguments().getString("description");
            category = getArguments().getString("category") ;
            isAccepted = getArguments().getBoolean("isAccepted");
        }

        if(isAccepted){
            coverLocationView.setVisibility(view.GONE);
            coverNumberView.setVisibility(view.GONE);
        }
        else{
            coverLocationView.setVisibility(view.VISIBLE);
            coverNumberView.setVisibility(view.VISIBLE);
        }

        // Set Argument data to the Text/Image views
        nameText.setText(itemName);
        priceText.setText("€" + price);
        numberText.setText(number);
        locationText.setText(location);
        imageView.setImageBitmap(photo);
        descriptionText.setText(description);
        categoryText.setText(category);


    return view;

    }
}
