package com.example.team211programmingtechniques;

import androidx.fragment.app.Fragment;
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


public class RentFragment extends Fragment {

    RecyclerView recyclerView;
    RentItemAdapter rentAdapter;
    List<RentItem> rentItemList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rent, container, false);

        recyclerView = view.findViewById(R.id.rentRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        rentAdapter = new RentItemAdapter(requireContext(), rentItemList);
        recyclerView.setAdapter(rentAdapter);

        // Load items from DB
        DBObject db = new DBObject(requireContext());
        db.GetItemDetails(new DBCallback<List<RentItem>>() {
            @Override
            public void onSuccessDB(List<RentItem> items) {
                rentAdapter.setRentItems(items);
            }

            @Override
            public void onErrorDB(String error) {
                Toast.makeText(requireContext(), "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

}

