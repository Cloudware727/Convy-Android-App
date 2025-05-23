package com.example.team211programmingtechniques;

import android.os.Bundle;
import android.util.Log;
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

public class RentedHistoryFragment extends Fragment {
    RecyclerView recyclerView;
    RentedItemAdapter adapter;
    List<HistoryRentedItems> historyRentedItemsList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_renting_history, container, false);

        recyclerView = view.findViewById(R.id.myRentedItemsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new RentedItemAdapter(requireContext(), historyRentedItemsList);
        recyclerView.setAdapter(adapter);

        String username = requireActivity()
                .getSharedPreferences("user_prefs", requireActivity().MODE_PRIVATE)
                .getString("username", null);

        if (username == null) {
            Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_SHORT).show();
            return view;
        }

        DBObject db = new DBObject(requireContext());

        db.getAllRentedItems(username, new DBCallback<List<HistoryRentedItems>>() {
            @Override
            public void onSuccessDB(List<HistoryRentedItems> result) {
                adapter.setHistoryRentedItemsList(result);
            }

            @Override
            public void onErrorDB(String error) {
                Toast.makeText(requireContext(), "Error: " + error, Toast.LENGTH_SHORT).show();
                Log.e("RentedHistoryFragment", "DB error: " + error);
            }
        });

        return view;
    }
}
