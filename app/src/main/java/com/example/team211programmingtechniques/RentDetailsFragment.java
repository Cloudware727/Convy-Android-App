package com.example.team211programmingtechniques;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
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
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class RentDetailsFragment extends Fragment {

    private String itemName, number, location,description,category;
    private Bitmap photo;
    private int price,itemiD;
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
        DBObject dbObject = new DBObject(context);

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
        ImageButton expandBtn = view.findViewById(R.id.btnExpandMap);
        Button contactButton = view.findViewById(R.id.btn_contact);




        // Get arguments and check if they are empty
        if (getArguments() != null) {
            itemiD = getArguments().getInt("item_id");
            itemName = getArguments().getString("item_name");
            photo = getArguments().getParcelable("photo");
            price = getArguments().getInt("price");
            number = getArguments().getString("number");
            location = getArguments().getString("Location");
            description = getArguments().getString("description");
            category = getArguments().getString("category") ;
            isAccepted = getArguments().getBoolean("isAccepted");
        }

        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(gMap -> {
            googleMap = gMap;


            SharedPreferences prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
            String userLocation = prefs.getString("Location", null);
            String username = prefs.getString("username","default");

            contactButton.setOnClickListener(v -> {
                dbObject.AddAnOffer(itemiD,username,new DBCallback<Boolean>() {
                    @Override
                    public void onSuccessDB(Boolean result) {
                        if(result) {
                            Toast.makeText(requireContext(), "Offer has been sent", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Toast.makeText(requireContext(), "Failed to send offer, please try again later", Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onErrorDB(String error) {
                        Toast.makeText(getContext(), "You can only send a maximum of one request per item!!!", Toast.LENGTH_SHORT).show();
                    }
                });
            });

            if (!isAccepted) {
                // Just show static map centered on item location
                Geocoder geocoder = new Geocoder(context, Locale.getDefault());
                try {
                    List<Address> destAddresses = geocoder.getFromLocationName(location, 1);
                    if (!destAddresses.isEmpty()) {
                        LatLng destLatLng = new LatLng(
                                destAddresses.get(0).getLatitude(),
                                destAddresses.get(0).getLongitude()
                        );
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(destLatLng, 13));
                    }
                } catch (IOException e) {
                    Toast.makeText(context, "Map error", Toast.LENGTH_SHORT).show();
                }
                return;
            }

            // If accepted: show pins and route

            Geocoder geocoder = new Geocoder(context, Locale.getDefault());
            LatLng userLatLng = null;
            LatLng destLatLng = null;

            try {
                List<Address> userAddresses = geocoder.getFromLocationName(userLocation, 1);
                List<Address> destAddresses = geocoder.getFromLocationName(location, 1);

                if (!userAddresses.isEmpty() && !destAddresses.isEmpty()) {
                    userLatLng = new LatLng(
                            userAddresses.get(0).getLatitude(),
                            userAddresses.get(0).getLongitude()
                    );

                    destLatLng = new LatLng(
                            destAddresses.get(0).getLatitude(),
                            destAddresses.get(0).getLongitude()
                    );
                }
            } catch (IOException e) {
                Toast.makeText(context, "Geocoding failed", Toast.LENGTH_SHORT).show();
                return;
            }

            LatLng finalUserLatLng = userLatLng;
            LatLng finalDestLatLng = destLatLng;


            dbObject.getMapRoute(userLocation, location, new DBCallback<RouteInfo>() {
                @Override
                public void onSuccessDB(RouteInfo route) {
                    if (route != null) {
                        googleMap.addMarker(new MarkerOptions().position(finalUserLatLng).title("Your Location"));
                        googleMap.addMarker(new MarkerOptions().position(finalDestLatLng).title("Destination"));

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

            expandBtn.setOnClickListener(v -> {
                if (userLocation != null && location != null && !userLocation.isEmpty() && !location.isEmpty()) {
                    Uri gmmIntentUri = Uri.parse("https://www.google.com/maps/dir/?api=1" +
                            "&origin=" + Uri.encode(userLocation) +
                            "&destination=" + Uri.encode(location));
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                    mapIntent.setPackage("com.google.android.apps.maps");
                    if (mapIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
                        startActivity(mapIntent);
                    } else {
                        Toast.makeText(requireContext(), "Google Maps is not installed", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(requireContext(), "Missing origin or destination", Toast.LENGTH_SHORT).show();
                }
            });
        });




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
