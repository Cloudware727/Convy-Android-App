package com.example.team211programmingtechniques;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.team211programmingtechniques.database.DBCallback;
import com.example.team211programmingtechniques.database.DBObject;
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EditProfileFragment extends Fragment {

    private EditText etFirstName, etLastName, etEmail, etPhone, etLocation;
    private Button btnSubmitChanges;

    private String username;
    private String apiKey;
    private ActivityResultLauncher<Intent> autocompleteLauncher;
    private final boolean[] updateSuccess = {true};
    private List<Runnable> updateTasks;


    public EditProfileFragment() {
        this.apiKey = "AIzaSyAs8FEIso6r09Vy6MlVcyMaSOqY1b0dQts";
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {


        // Inflate the view
        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);
        // Retrieve all elements within fragment
        etFirstName = view.findViewById(R.id.edit_first_name);
        etLastName = view.findViewById(R.id.edit_last_name);
        etEmail = view.findViewById(R.id.edit_email);
        etPhone = view.findViewById(R.id.edit_phone);
        etLocation = view.findViewById(R.id.edit_location);
        etLocation.setFocusable(false); // Disable keyboard
        etLocation.setOnClickListener(v -> launchPlaceAutocomplete()); // Uses Places API
        btnSubmitChanges = view.findViewById(R.id.btn_submit_changes);
        // Username will always be set => Logged in = username saved
        username = requireActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE).getString("username", null);
        // Add on click listener to button
        btnSubmitChanges.setOnClickListener(v -> submitChanges());
        // Load Google Places API
        if (!Places.isInitialized()) {
            Places.initialize(requireContext().getApplicationContext(), apiKey);
        }
        // ActivityResultLauncher for auto-completion
        autocompleteLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            Place place = Autocomplete.getPlaceFromIntent(data);
                            // Handle the selected place here
                            Log.i("PlaceAutocomplete", "Place: " + place.getName() + ", " + place.getId());
                            etLocation.setText(place.getName());
                        }
                    } else if (result.getResultCode() == AutocompleteActivity.RESULT_ERROR) {
                        Intent data = result.getData();
                        Status status = Autocomplete.getStatusFromIntent(data);
                        Log.e("PlaceAutocomplete", "Error: " + status.getStatusMessage());
                    } else if (result.getResultCode() == Activity.RESULT_CANCELED) {
                        // The user canceled the operation.
                    }
                }
        );
        return view;
    }

    private void launchPlaceAutocomplete() {
        List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS);

        Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
                .build(requireContext());

        autocompleteLauncher.launch(intent);
    }

    private void submitChanges() {
        // Grab inputs trimmed (no spaces replacement yet, add if needed)
        String firstName = etFirstName.getText().toString().trim();
        String lastName = etLastName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String location = etLocation.getText().toString().trim();

        // If all empty, show message and return early
        if (firstName.isEmpty() && lastName.isEmpty() && email.isEmpty()
                && phone.isEmpty() && location.isEmpty()) {
            Toast.makeText(requireContext(), "Please fill at least one field to update.", Toast.LENGTH_SHORT).show();
            return;
        }

        DBObject db = new DBObject(requireContext());

        // Track success for all updates
        updateSuccess[0] = true;

        // Count how many updates to do
        int updatesCount = 0;

        // We'll chain updates dynamically based on which fields are non-empty
        // Build a Runnable queue for the updates:
        updateTasks = new ArrayList<>();

        if (!firstName.isEmpty()) {
            updatesCount++;
            updateTasks.add(() -> db.changeFirstName(replaceSpacesWithPlus(firstName), username, new DBCallback<Boolean>() {
                @Override
                public void onSuccessDB(Boolean success) {
                    if (!success) updateSuccess[0] = false;
                    // Placing this here allows for continuous updates, as the attribute checked is updateTasks.isEmpty()
                    runNextUpdate();
                }
                @Override
                public void onErrorDB(String error) {
                    updateSuccess[0] = false;
                    runNextUpdate();
                }
            }));
        }

        if (!lastName.isEmpty()) {
            updatesCount++;
            updateTasks.add(() -> db.changeLastName(replaceSpacesWithPlus(lastName), username, new DBCallback<Boolean>() {
                @Override
                public void onSuccessDB(Boolean success) {
                    if (!success) updateSuccess[0] = false;
                    runNextUpdate();
                }
                @Override
                public void onErrorDB(String error) {
                    updateSuccess[0] = false;
                    runNextUpdate();
                }
            }));
        }

        if (!email.isEmpty()) {
            updatesCount++;
            updateTasks.add(() -> db.changeEmail(replaceSpacesWithPlus(email), username, new DBCallback<Boolean>() {
                @Override
                public void onSuccessDB(Boolean success) {
                    if (!success) updateSuccess[0] = false;
                    runNextUpdate();
                }
                @Override
                public void onErrorDB(String error) {
                    updateSuccess[0] = false;
                    runNextUpdate();
                }
            }));
        }

        if (!phone.isEmpty()) {
            updatesCount++;
            updateTasks.add(() -> db.changePhone(replaceSpacesWithPlus(phone), username, new DBCallback<Boolean>() {
                @Override
                public void onSuccessDB(Boolean success) {
                    if (!success) updateSuccess[0] = false;
                    runNextUpdate();
                }
                @Override
                public void onErrorDB(String error) {
                    updateSuccess[0] = false;
                    runNextUpdate();
                }
            }));
        }

        if (!location.isEmpty()) {
            updatesCount++;
            updateTasks.add(() -> db.changeLocation(replaceSpacesWithPlus(location), username, new DBCallback<Boolean>() {
                @Override
                public void onSuccessDB(Boolean success) {
                    if (!success) updateSuccess[0] = false;
                    runNextUpdate();
                }
                @Override
                public void onErrorDB(String error) {
                    updateSuccess[0] = false;
                    runNextUpdate();
                }
            }));
        }

        if (updatesCount == 0) {
            // No updates to perform (should not reach here because of earlier check)
            return;
        }

        // Start the chain of updates
        updateTasks.remove(0).run();

    }

    // Needed for formatting according to RESTFul API
    private String replaceSpacesWithPlus(String input) {
        if (input == null) return null;
        return input.replace(" ", "+");
    }

    // Helper to run the next update or finish
    private void runNextUpdate() {
        if (updateTasks.isEmpty()) {
            requireActivity().runOnUiThread(() -> {
                if (updateSuccess[0]) {
                    Toast.makeText(requireContext(), "Profile updated!", Toast.LENGTH_SHORT).show();
                    requireActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .remove(EditProfileFragment.this)
                            .commit();
                } else {
                    Toast.makeText(requireContext(), "Failed to update profile", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            updateTasks.remove(0).run();
        }
    }

}

