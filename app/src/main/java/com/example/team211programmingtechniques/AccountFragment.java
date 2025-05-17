package com.example.team211programmingtechniques;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.team211programmingtechniques.database.DBCallback;
import com.example.team211programmingtechniques.database.DBObject;

public class AccountFragment extends Fragment {

    private String username;
    private TextView userNameTV, firstName, lastName, email, phoneNum, location;

    public AccountFragment() {
    }

    @Nullable
    @Override
    public View onCreateView (@NonNull LayoutInflater inflater,
                              @Nullable ViewGroup container,
                              @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_account, container, false);

        // Find all elements
        username = requireActivity()
                .getSharedPreferences("user_prefs",requireActivity().MODE_PRIVATE)
                .getString("username", null);
        userNameTV = v.findViewById(R.id.tvWelcome);
        firstName = v.findViewById(R.id.tvFirstName);
        lastName = v.findViewById(R.id.tvLastName);
        email = v.findViewById(R.id.tvEmail);
        phoneNum = v.findViewById(R.id.tvPhone);
        location = v.findViewById(R.id.tvLocation);
        // Set default text
        userNameTV.setText("Hello, " + username);
        firstName.setText("...");
        lastName.setText("...");
        email.setText("...");
        phoneNum.setText("...");
        location.setText("...");
        displayUserInfo(username, userNameTV, firstName, lastName, email, phoneNum, location);
        // Return View
        return v;
    }

    private void displayUserInfo(String username,
                                 TextView usernameTV,
                                 TextView firstName,
                                 TextView lastName,
                                 TextView email,
                                 TextView phoneNum,
                                 TextView location) {

        DBObject db = new DBObject(requireContext());

        db.retrieveUserInfo(username, new DBCallback<String[]>() {
            @Override
            public void onSuccessDB(String[] result) {
                if (result != null && result.length == 5) {
                    for (int i = 0; i < result.length; i++) {
                        result[i] = result[i] != null ? result[i] : "null";
                    }

                    // Update UI

                    if (!result[0].equals("null")) {
                        firstName.setText(result[0]);
                    }
                    if (!result[1].equals("null")) {
                        lastName.setText(result[1]);
                    }
                    if (!result[2].equals("null")) {
                        email.setText(result[2]);
                    }
                    if (!result[3].equals("null")) {
                        phoneNum.setText(result[4]);
                    }
                    if (!result[4].equals("null")) {
                        location.setText(result[5]);
                    }

                } else {
                    Log.e("displayUserInfo", "Mismatched or null result array");
                }
            }

            @Override
            public void onErrorDB(String error) {
                Log.e("displayUserInfo", "Database Error: " + error);
                Toast.makeText(requireContext(), "Could not fetch user info", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
