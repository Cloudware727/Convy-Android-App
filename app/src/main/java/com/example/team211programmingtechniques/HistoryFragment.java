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

    private MaterialButton btnMyItems, btnRentedItems;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        // Initialize buttons using the inflated view
        btnMyItems = view.findViewById(R.id.btnMyItems);
        btnRentedItems = view.findViewById(R.id.btnRentedItems);

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetButtonStyles();

                MaterialButton selected = (MaterialButton) v;
                selected.setStrokeColorResource(android.R.color.darker_gray); // Gray border
                selected.setStrokeWidth(4);
            }
        };

        btnMyItems.setOnClickListener(listener);
        btnRentedItems.setOnClickListener(listener);

        return view;  // Return the inflated view
    }

    private void resetButtonStyles() {
        btnMyItems.setStrokeColorResource(R.color.dark_green);
        btnMyItems.setStrokeWidth(2);

        btnRentedItems.setStrokeColorResource(R.color.dark_green);
        btnRentedItems.setStrokeWidth(2);
    }
}
