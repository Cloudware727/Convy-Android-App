package com.example.team211programmingtechniques;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
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
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;

import java.util.List;

public class RentDetailsFragment extends Fragment {

    private String itemName, number, location,description,category;
    private Bitmap photo;
    private int price;
    private Boolean isAccepted;
    private ImageView imageView;
    private TextView nameText, priceText, numberText, locationText, descriptionText,categoryText;
    private View coverNumberView,coverLocationView;
    private MapView mapView;
    private GoogleMap googleMap;

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rent_details, container, false);
        Context context =  requireContext();

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
        mapView = view.findViewById(R.id.detailMapView);
        



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

        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(gMap -> {
            googleMap = gMap;

            // Load and display the route here
            DBObject dbObject = new DBObject(context);

            SharedPreferences prefs = context.getSharedPreferences("user_prefs",Context.MODE_PRIVATE);
            String userLocation = prefs.getString("Location", null); // e.g., "Brussels"


            dbObject.getMapRoute(userLocation, location, new DBCallback<RouteInfo>() {
                @Override
                public void onSuccessDB(RouteInfo route) {
                    if (route != null) {
                        List<LatLng> points = PolyUtil.decode(route.polyline);
                        googleMap.addPolyline(new PolylineOptions().addAll(points).width(8).color(Color.BLUE));

                        LatLngBounds.Builder builder = new LatLngBounds.Builder();
                        for (LatLng p : points) builder.include(p);
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 100));
                    }
                }

                @Override
                public void onErrorDB(String error) {
                    Toast.makeText(getContext(), "Map error: " + error, Toast.LENGTH_SHORT).show();
                }
            });
        });


    return view;

    }
}
