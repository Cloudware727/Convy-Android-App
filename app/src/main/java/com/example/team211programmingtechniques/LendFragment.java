package com.example.team211programmingtechniques;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
// Imports for Spinner
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
// Imports for Upload Button
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.text.SimpleDateFormat;
import java.util.Date;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.team211programmingtechniques.database.DBCallback;
import com.example.team211programmingtechniques.database.DBObject;

public class LendFragment extends Fragment {
    // Spinner categories
    private String[] categories;
    // Strings for POST-Request
    private String itemName, itemDescription, itemCategory, formattedDate, base64Image;
    // Integer version of category
    private int itemCategoryIndex, statusOfItem, itemPrice;
    // Hashmap for categories
    private HashMap<String, Integer> categoryMap;
    // Image chosen by user
    private ImageView imageChosen;
    // Upload post button
    private Button uploadPostBtn;
    // Upload photo section
    private View uploadPhotoSection;
    // Bitmap of photo chosen
    private Bitmap bitmap;
    private ProgressBar progressBar;
    private ActivityResultLauncher<Intent> imagePickerLauncher; // Modern alternative for startActivityForResult()

    public LendFragment() {
        this.categories = new String[] {"Default",
                            "Electronics",
                            "Tools & Equipment",
                            "Vehicles & Transport",
                            "Clothing & Wearables",
                            "Books & Stationary",
                            "Outdoor & Camping",
                            "Home & Kitchen",
                            "Sports & Fitness",
                            "Toys & Games",
                            "Event Supplies"};
        // Access today's current date compatible with MySQL
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        this.formattedDate = sdf.format(new Date());

        // Format Hashmap
        this.categoryMap = new HashMap<>();
        for (int i = 0; i < categories.length; i++) {
            categoryMap.put(categories[i], i);
        }
        // Determine current status (0 = lent out, 1 = rented)
        statusOfItem = 0;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_lend, container, false);
        // Spinner Logic
        Spinner spinnerCategories = v.findViewById(R.id.spinner_category);
        ArrayAdapter<String> adapterCategories = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                categories
        );
        spinnerCategories.setAdapter(adapterCategories);
        spinnerCategories.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                itemCategory = parent.getItemAtPosition(position+1).toString();
                itemCategoryIndex = categoryMap.get(itemCategory);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Use later for uploading a file
            }
        });

        /*
         Upload images logic
         */

        // Trace back ID of image chosen element in XML
        imageChosen = v.findViewById(R.id.image_chosen);
        // Trace back ID of upload photo button element in XML
        uploadPhotoSection = v.findViewById(R.id.section_upload_photos);
        // onClickListener of Upload Photos Button
        uploadPhotoSection.setOnClickListener(view -> {
            onSectionPickClicked();
        });
        // Declare launcher
        imagePickerLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
            if (result.getResultCode() == MainActivity.RESULT_OK && result.getData() != null && result.getData().getData() != null) {
                Uri filePath = result.getData().getData();
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), filePath);
                    imageChosen.setImageBitmap(bitmap);

                    // Convert right after choosing
                    base64Image = convertBitmapToBase64(bitmap);
                    Log.d("LendFragment", "Image successfully converted to Base64");
                } catch (IOException e) {
                    Log.e("LendFragment", "Error - incompatible picture");
                }
            }
        });
        // Access username from SharedPreferences
        String username = requireActivity()
                .getSharedPreferences("user_prefs",requireActivity().MODE_PRIVATE)
                .getString("username", null);

        // Upload Post Button + progress bar
        uploadPostBtn = v.findViewById(R.id.button_final_upload);
        progressBar = v.findViewById(R.id.progress_bar);

        uploadPostBtn.setOnClickListener(view -> {
            // Access item name from XML
            EditText et_itemName = v.findViewById(R.id.edit_item_name);
            itemName = et_itemName.getText().toString().trim();
            // Access item description from XML
            EditText et_itemDesc = v.findViewById(R.id.edit_description);
            itemDescription = et_itemDesc.getText().toString().trim();
            // Access item price per day from XML
            EditText et_itemPrice = v.findViewById(R.id.edit_price_per_day);
            try {
                itemPrice = Integer.parseInt(et_itemPrice.getText().toString().trim());
            } catch (NumberFormatException e) {
                Toast.makeText(requireContext(), "Please enter a valid price", Toast.LENGTH_SHORT).show();
                return;
            }
            onBtnUploadClicked(v, progressBar, username, base64Image, itemName, formattedDate, itemPrice, itemDescription, itemCategoryIndex, statusOfItem);
        });
        // Return view
        return v;
    }

    // Purpose of button - image pick
    private void onSectionPickClicked() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        imagePickerLauncher.launch(Intent.createChooser(intent, "Select Image"));
    }
    // Purpose of button - upload
    private void onBtnUploadClicked(View view, ProgressBar progressBar, String username, String imageString, String itemName, String dateListed, int itemPrice, String itemDescription, int itemCategoryIndex, int itemStatus) {
        DBObject db = new DBObject(requireContext());
        progressBar.setVisibility(view.VISIBLE);

        db.postImage(new DBCallback<Boolean>() {
            @Override
            public void onSuccessDB(Boolean result) {
                if (result == true) {
                    progressBar.setVisibility(view.GONE);
                    Toast.makeText(requireContext(), "Item posted!", Toast.LENGTH_SHORT).show();
                }
                else {
                    progressBar.setVisibility(view.GONE);
                    Toast.makeText(requireContext(), "Failed to post item", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onErrorDB(String error) {
                progressBar.setVisibility(view.GONE);
                Toast.makeText(requireContext(), "Post request failed", Toast.LENGTH_LONG).show();
            }
        }, username, imageString, itemName, dateListed, itemPrice, itemDescription, itemCategoryIndex, itemStatus);
    }
    // Convert into base64
    private String convertBitmapToBase64(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        byte[] imageBytes = baos.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }
    // Not actually needed => ImageView + Fixed Size + centerCrop does the job
    private Bitmap getResizedBitmap(Bitmap bm, int newWidth) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scale = ((float) newWidth) / width;

        // Matrix to transform image
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);

        // Create new bitmap
        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
        bm.recycle();
        return resizedBitmap;
    }
}
