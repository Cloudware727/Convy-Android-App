package com.example.team211programmingtechniques;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.team211programmingtechniques.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        replaceFragment(new RentFragment());

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.account) {
                replaceFragment(new AccountFragment());
                // Handle home screen
            } else if (itemId == R.id.history) {
                replaceFragment(new HistoryFragment());
                // Handle favorites screen
            } else if (itemId == R.id.rent) {
                replaceFragment(new RentFragment());
                // Handle history screen
            } else if (itemId == R.id.lend) {
                replaceFragment(new LendFragment());
                // Handle account screen
            }

            return true;
        });
    }

        private void replaceFragment (Fragment fragment){
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.frame_layout, fragment);
            fragmentTransaction.commit();

        }
    }



