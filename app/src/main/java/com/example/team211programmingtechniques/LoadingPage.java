package com.example.team211programmingtechniques;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.team211programmingtechniques.database.DBObject;

public class LoadingPage extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading_page);

        new Thread(() -> {
            DBObject db = new DBObject();
            String response = db.sendGetRequestString(
                    "https://studev.groept.be/api/a24pt211/ping"
            );

            runOnUiThread(() -> {
                if (!response.equals("[]")) {
                    Intent intent = new Intent(LoadingPage.this, LoginPage.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(LoadingPage.this, "Connection error, please try again later", Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }
}
