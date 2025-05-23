package com.example.team211programmingtechniques;

import androidx.fragment.app.Fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.team211programmingtechniques.database.DBCallback;
import com.example.team211programmingtechniques.database.DBObject;

import java.util.ArrayList;
import java.util.List;

public class RentFragment extends Fragment {

    RecyclerView recyclerView;
    RentItemAdapter rentAdapter;
    List<RentItem> rentItemList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rent, container, false);
        Context context = requireContext();

        recyclerView = view.findViewById(R.id.rentRecyclerView);
        ImageButton searchButton = view.findViewById(R.id.searchButton);
        ImageButton filterButton = view.findViewById(R.id.filterButton);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        rentAdapter = new RentItemAdapter(requireContext(), rentItemList);
        recyclerView.setAdapter(rentAdapter);

        String[] categories = {
                "Default","Electronics", "Tools & Equipment", "Vehicles & Transport",
                "Clothing & Wearables", "Books & Stationary", "Outdoor & Camping",
                "Home & Kitchen", "Sports & Fitness", "Toys & Games", "Event Supplies"
        };

        boolean[] checkedCategories = new boolean[categories.length]; // Keeps track of checked items
        List<String> selectedCategories = new ArrayList<>(); // Stores selected category names

        filterButton.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setTitle("Select Categories");

            builder.setMultiChoiceItems(categories, checkedCategories, (dialog, index, isChecked) -> {
                String category = categories[index];
                if (isChecked) {
                    if (!selectedCategories.contains(category)) selectedCategories.add(category);
                } else {
                    selectedCategories.remove(category);
                }
            });

            builder.setPositiveButton("Filter", (dialog, which) -> {
                rentAdapter.filterByCategoryStrings(selectedCategories); // ← filters the items
            });

            builder.setNegativeButton("Cancel", null);
            builder.show();
        });




        searchButton.setOnClickListener(v -> {
            Log.d("SearchDebug", "Search button clicked");
            Toast.makeText(requireContext(), "Search Ongoing", Toast.LENGTH_SHORT).show();
            EditText search = view.findViewById(R.id.searchBar);
            String input = search.getText().toString().trim();
            Log.d("SearchDebug", "User input: " + input);

            DBObject dbObject = new DBObject(requireContext());

            if (input.isEmpty()) {
                Log.d("SearchDebug", "Input empty – showing all items");
                Toast.makeText(requireContext(), "No such items available", Toast.LENGTH_SHORT).show();

                rentAdapter.filterByItemIds(null);
            } else {
                dbObject.SearchQuery(input, new DBCallback<List<Integer>>() {
                    @Override
                    public void onSuccessDB(List<Integer> itemIds) {
                        Log.d("SearchQuery", "Received item IDs: " + itemIds);
                        Toast.makeText(requireContext(), "Search completed", Toast.LENGTH_SHORT).show();
                        rentAdapter.filterByItemIds(itemIds);
                    }

                    @Override
                    public void onErrorDB(String error) {
                        Toast.makeText(getContext(), "Search failed: " + error, Toast.LENGTH_SHORT).show();
                        Log.e("SearchQuery", "Error: " + error);
                    }
                });
            }
        });

        DBObject db = new DBObject(requireContext());
        db.GetItemDetails(new DBCallback<List<RentItem>>() {
            @Override
            public void onSuccessDB(List<RentItem> items) {
                Log.d("ItemLoad", "Items loaded: " + items.size());
                rentAdapter.setRentItems(items);
            }

            @Override
            public void onErrorDB(String error) {
                Toast.makeText(requireContext(), "Error: " + error, Toast.LENGTH_SHORT).show();
                Log.e("ItemLoad", "Error loading items: " + error);
            }
        });

        return view;
    }
}
