package com.example.team211programmingtechniques;
// Imports from built-in library
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
// XML-file extension
import androidx.appcompat.app.AppCompatActivity;
// To handle DB-pings
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
// To import DBObject-methods
import database.DBObject;
// To handle the GIF
import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;


public class entryActivity extends AppCompatActivity {
    // Database Object
    private DBObject db;
    // Executors
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    public entryActivity() {
        this.db = new DBObject();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Activity Initialization
        super.onCreate(savedInstanceState);
        Log.d("entryActivity", "onCreate() called");
        setContentView(R.layout.loading_page);
        Log.d("entryActivity", "Set content view to loading_page");
        // DroidsOnRoids ImageView GIF support
        GifImageView loadingGIF = findViewById(R.id.gifImageView);
        try {
            GifDrawable loadingGIFDrawable = new GifDrawable(getResources(), R.drawable.convy_loading_animation);
            loadingGIF.setImageDrawable(loadingGIFDrawable);
        } catch(IOException e) {
            e.printStackTrace();
        }
        Log.d("entryActivity","GIF loaded properly" );

        // Database ping check
        pingDatabaseTask();
    }

    private void pingDatabaseTask() {
        Log.d("entryActivity", "Starting database ping...");
        executor.execute(() -> {
            boolean reachable = db.isDatabaseReachable();
            Log.d("entryActivity", "Database reachable: " + reachable);
            while(!reachable) {
                try {
                    Log.d("entryActivity", "Databse not reachable, retrying in 2s...");
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    return;
                }
            }

                // Once reachable => switch back to main thread
                mainHandler.postDelayed(() -> {
                    Log.d("entryActivity", "Database reachable, Transitioning into loginActivity");
                    startActivity(new Intent(entryActivity.this, loginActivity.class));
                    finish();
                }, 3000);
            });
        }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdown();
    }
}
