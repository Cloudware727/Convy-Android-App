package com.example.team211programmingtechniques;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.team211programmingtechniques.database.DBCallback;
import com.example.team211programmingtechniques.database.DBObject;

import java.util.ArrayList;
import java.util.List;

public class RentingFragment extends Fragment {

    private RecyclerView recyclerView;
    private RentingItemAdapter rentingItemAdapter;
    private List<RentItem> rentingItemList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_renting, container, false);
        Context context = requireContext();

        recyclerView = view.findViewById(R.id.rentingRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        rentingItemAdapter = new RentingItemAdapter(context, rentingItemList);
        recyclerView.setAdapter(rentingItemAdapter);

        // Get username from SharedPreferences
        SharedPreferences prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        String username = prefs.getString("username", null);

        // Fetch items for renting
        DBObject dbObject = new DBObject(context);
        dbObject.getItemsForRenting(username, new DBCallback<List<RentItem>>() {
            @Override
            public void onSuccessDB(List<RentItem> items) {
                rentingItemAdapter.setItems(items);
            }

            @Override
            public void onErrorDB(String error) {
                Toast.makeText(context, "Error loading renting items: " + error, Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
}

