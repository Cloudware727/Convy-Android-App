package com.example.team211programmingtechniques;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;

public class HistoryFragment extends Fragment {

    private MaterialButton btnMyItems, btnRentedItems, btnRentingItems;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        btnMyItems = view.findViewById(R.id.btnMyItems);
        btnRentedItems = view.findViewById(R.id.btnPreviouslyRented);
        btnRentingItems = view.findViewById(R.id.btnRenting);

        // Load MyHistoryFragment by default
        if (savedInstanceState == null) {
            getChildFragmentManager().beginTransaction()
                    .replace(R.id.history_fragment_container, new MyHistoryFragment())
                    .commit();

            btnMyItems.setStrokeColorResource(android.R.color.darker_gray);
            btnMyItems.setStrokeWidth(4);
        }

        btnMyItems.setOnClickListener(v -> {
            resetButtonStyles();
            MaterialButton selected = (MaterialButton) v;
            selected.setStrokeColorResource(android.R.color.darker_gray);
            selected.setStrokeWidth(4);

            getChildFragmentManager().beginTransaction()
                    .replace(R.id.history_fragment_container, new MyHistoryFragment())
                    .commit();
        });

        btnRentingItems.setOnClickListener(v -> {
            resetButtonStyles();
            MaterialButton selected = (MaterialButton) v;
            selected.setStrokeColorResource(android.R.color.darker_gray);
            selected.setStrokeWidth(4);

            getChildFragmentManager().beginTransaction()
                    .replace(R.id.history_fragment_container, new RentingFragment())
                    .commit();
        });

        btnRentedItems.setOnClickListener(v -> {
            resetButtonStyles();
            MaterialButton selected = (MaterialButton) v;
            selected.setStrokeColorResource(android.R.color.darker_gray);
            selected.setStrokeWidth(4);

            getChildFragmentManager().beginTransaction()
                    .replace(R.id.history_fragment_container, new RentedHistoryFragment())
                    .commit();
        });

        return view;
    }

    private void resetButtonStyles() {
        btnMyItems.setStrokeColorResource(R.color.dark_green);
        btnMyItems.setStrokeWidth(2);

        btnRentedItems.setStrokeColorResource(R.color.dark_green);
        btnRentedItems.setStrokeWidth(2);

        btnRentingItems.setStrokeColorResource(R.color.dark_green);
        btnRentingItems.setStrokeWidth(2);
    }
}
