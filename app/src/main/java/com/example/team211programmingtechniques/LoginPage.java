package com.example.team211programmingtechniques;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.team211programmingtechniques.database.DBObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class LoginPage extends AppCompatActivity {

    public String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedHash = digest.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : encodedHash) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_page);

        TextView tv_sign_up = findViewById(R.id.tv_sign_up);  // Jumps to SignUpPage when clicked
        tv_sign_up.setOnClickListener(v -> {
            Intent intent = new Intent(LoginPage.this, SignUpPage.class);
            startActivity(intent);
        });

        EditText et_username = findViewById(R.id.et_username);
        EditText et_password = findViewById(R.id.et_password);
        Button btn_login = findViewById(R.id.btn_login);

        btn_login.setOnClickListener(v -> {
            String username = et_username.getText().toString().trim();
            String password = et_password.getText().toString().trim();
            String hashedpassword = hashPassword(password);


            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(LoginPage.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            new Thread(() -> {
                DBObject db = new DBObject();
                String response = db.sentGetRequestString(
                        "https://studev.groept.be/api/a24pt211/CheckLoginData/" + username + "/" + hashedpassword
                );

                response = response.trim();
                int matchFound = 0;

                try {
                    JSONArray jsonArray = new JSONArray(response);
                    JSONObject obj = jsonArray.getJSONObject(0);
                    matchFound = obj.getInt("match_found");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                int finalMatchFound = matchFound;
                String finalResponse = response;
                runOnUiThread(() -> {
                    if (finalMatchFound == 1) {
                        Toast.makeText(LoginPage.this, "Login successful!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(LoginPage.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(LoginPage.this, "Invalid username or password", Toast.LENGTH_SHORT).show();
                    }
                });
            }).start();
        });
    }
}
