package com.example.team211programmingtechniques;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.team211programmingtechniques.database.DBCallback;
import com.example.team211programmingtechniques.database.DBObject;
import com.google.android.material.button.MaterialButton;

public class AccountFragment extends Fragment {

    private String username;
    private TextView userNameTV, firstName, lastName, email, phoneNum, location;
    private Button logOutBtn;

    public AccountFragment() {
    }

    @Nullable
    @Override
    public View onCreateView (@NonNull LayoutInflater inflater,
                              @Nullable ViewGroup container,
                              @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_account, container, false);

        // Find all elements relevant for user info
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

        // Button logic
        logOutBtn = v.findViewById(R.id.btnSignOut);

        logOutBtn.setOnClickListener(view -> {
            logout();
        });

        // Edit Profile Button
        MaterialButton btnEditProfile = v.findViewById(R.id.btnEditProfile);

        btnEditProfile.setOnClickListener(view -> {
            EditProfileFragment editFragment = new EditProfileFragment();
            // Show as overlay by adding to fragment container or to this fragment’s parent
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .add(android.R.id.content, editFragment, "edit_profile_fragment")
                    .addToBackStack(null)
                    .commit();
        });


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
                        phoneNum.setText(result[3]);
                    }
                    if (!result[4].equals("null")) {
                        location.setText(result[4]);
                        String locationText = location.getText().toString();
                        requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE).edit().putString("Location",locationText).apply();

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

    private void logout() {
        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Confirm Logout")
                .setMessage("Are you sure you want to log out?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    // User confirmed logout

                    // Retrieve sharedPreferences editor
                    SharedPreferences sp1 = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
                    SharedPreferences sp2 = requireActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
                    SharedPreferences.Editor spEditor1 = sp1.edit();
                    SharedPreferences.Editor spEditor2 = sp2.edit();

                    spEditor1.putBoolean("IsLoggedIn", false);
                    spEditor2.remove("username");

                    spEditor1.apply();
                    spEditor2.apply();

                    // Show toast message
                    Toast.makeText(requireContext(), "Logout successful!", Toast.LENGTH_SHORT).show();

                    // Return to login page
                    Intent intent = new Intent(requireActivity(), LoginPage.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    requireActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    requireActivity().finish();
                })
                .setNegativeButton("No", (dialog, which) -> {
                    // User cancelled logout, just dismiss dialog
                    dialog.dismiss();
                })
                .create()
                .show();
    }
}
