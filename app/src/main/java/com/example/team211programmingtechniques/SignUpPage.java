package com.example.team211programmingtechniques;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.team211programmingtechniques.database.DBObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SignUpPage extends AppCompatActivity {
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
        setContentView(R.layout.signup_page);

        TextView tv_sign_up = findViewById(R.id.tv_back_to_login);  // Jumps to SignUpPage when clicked
        tv_sign_up.setOnClickListener(v -> {
            Intent intent = new Intent(SignUpPage.this, LoginPage.class);
            startActivity(intent);
        });

        EditText et_username = findViewById(R.id.et_signup_username);
        EditText et_password = findViewById(R.id.et_signup_password);
        EditText et_email = findViewById(R.id.et_signup_email);
        EditText et_confirmpassword = findViewById(R.id.et_signup_confirm_password);
        Button btn_signup = findViewById(R.id.btn_signup);

        btn_signup.setOnClickListener(v -> {
            String username = et_username.getText().toString().trim();
            String email = et_email.getText().toString().trim();
            String password = et_password.getText().toString().trim();
            String confirmpassword = et_confirmpassword.getText().toString().trim();
            String hashedpassword = hashPassword(password);

            if (username.isEmpty() || password.isEmpty() || email.isEmpty() || confirmpassword.isEmpty()) {
                Toast.makeText(SignUpPage.this, "Please fill in the required fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if(!password.equals(confirmpassword)){
                Toast.makeText(SignUpPage.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }

            new Thread(() -> {
                DBObject db = new DBObject();
                String response = db.sentGetRequestString(
                        "https://studev.groept.be/api/a24pt211/CheckIfUserNameOrPasswordExists/" + username + "/" + email
                );

                response = response.trim();
                String finalResponse = response;

                runOnUiThread(() -> {
                    if (finalResponse.equals("[]")) {
                        new Thread(() -> {
                            DBObject db2 = new DBObject();
                            String inserted = db2.sentGetRequestString("https://studev.groept.be/api/a24pt211/SignInQuery/"+username+"/"+hashedpassword+"/"+email);
                            String insertUser = db2.sentGetRequestString(
                                    "https://studev.groept.be/api/a24pt211/AddUsernameToPersonalDetails/"+username
                            );

                            runOnUiThread(() -> {
                                if (finalResponse.equals("[]")) {

                                    Toast.makeText(SignUpPage.this, "Signup successful!", Toast.LENGTH_SHORT).show();
                                    // Redirect to login or next screen
                                } else {
                                    Toast.makeText(SignUpPage.this, "Error creating account", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }).start();
                    } else {
                        Toast.makeText(SignUpPage.this, "Username or email already in use", Toast.LENGTH_SHORT).show();
                    }
                });
            }).start();
        });
    }
}
