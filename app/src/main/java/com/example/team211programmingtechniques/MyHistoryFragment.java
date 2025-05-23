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

public class MyHistoryFragment extends Fragment {
    RecyclerView recyclerView;
    HistoryMyItemAdapter adapter;
    List<HistoryMyItem> historyMyItemList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_history, container, false);

        recyclerView = view.findViewById(R.id.myItemsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new HistoryMyItemAdapter(requireContext(), historyMyItemList);
        recyclerView.setAdapter(adapter);

        // Load user name from sharedPreferences
        String username = requireActivity()
                .getSharedPreferences("user_prefs",requireActivity().MODE_PRIVATE)
                .getString("username", null);

        // Fetch items from DB
        DBObject db = new DBObject(requireContext());
        db.getAllUserItems(username, new DBCallback<List<HistoryMyItem>>() {
            @Override
            public void onSuccessDB(List<HistoryMyItem> result) {
                adapter.setHistoryMyItems(result);
            }

            @Override
            public void onErrorDB(String error) {
                Toast.makeText(requireContext(), "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }
}
