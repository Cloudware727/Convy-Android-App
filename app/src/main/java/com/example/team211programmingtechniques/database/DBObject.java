package com.example.team211programmingtechniques.database;

// Imports - Json related
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
// Imports - data processing related
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.*;
import java.nio.charset.StandardCharsets; // For formatting queries to JSON WEB API
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
// Imports - Volley related
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.team211programmingtechniques.RentItem;

public class DBObject {
    // URLS
    final String DBUrl;
    // Volley-needed objects
    private RequestQueue requestQ;
    private final Context context;
    public DBObject(Context context) {
        this.DBUrl = "https://studev.groept.be/api/a24pt211";
        this.context = context;
    }
    /*
    /--------------------------------------------------------------------------------------------------------------/
    */
    // Specific methods
    public void isDatabaseReachable(DBCallback<Boolean> callback) {
        String pingSuffix = "/ping";
        String finalURL = DBUrl + pingSuffix;

        volleyGETRequest(finalURL, new VolleyCallback() {
            @Override
            public void onSuccessVolley(JSONArray result) {
                try {
                    boolean reachable = false;
                    for (int i = 0; i < result.length(); i++) {
                        JSONObject jObject = result.getJSONObject(i);
                        reachable = (jObject.getInt("1") == 1);
                    }
                    callback.onSuccessDB(reachable);
                } catch (JSONException e) {
                    callback.onErrorDB(e.getMessage());
                    Log.e("Database", e.getMessage(), e);
                }
            }
            @Override
            public void onErrorVolley(String error) {
                callback.onErrorDB(error);
                Log.e("Volley", "Error: " + error);
            }
        });
    }

    public void postImage(DBCallback<Boolean> callback, String username, String imageString, String itemName, String dateListed, int itemPrice, String itemDescription, int itemCategoryIndex, int itemStatus) {
        // Create URL
        String[] urlSuffixes = new String[] {"postImage",
                                            "username",
                                            "itemname",
                                            "description",
                                            "priceperday",
                                            "photostring",
                                            "datelisted",
                                            "category",
                                            "status"};
        String finalURLPostImage = DBUrl + "/" + urlSuffixes[0];
        // Create HashMap for parameters
        HashMap<String, String> imagePostParams = new HashMap<String, String>();
        imagePostParams.put(urlSuffixes[1], username);
        imagePostParams.put(urlSuffixes[2], itemName);
        imagePostParams.put(urlSuffixes[3], itemDescription);
        imagePostParams.put(urlSuffixes[4], String.valueOf(itemPrice));
        imagePostParams.put(urlSuffixes[5], imageString);
        imagePostParams.put(urlSuffixes[6], dateListed);
        imagePostParams.put(urlSuffixes[7], String.valueOf(itemCategoryIndex));
        imagePostParams.put(urlSuffixes[8], String.valueOf(itemStatus));

        volleyPOSTRequest(finalURLPostImage, imagePostParams, new VolleyCallback() {
            @Override
            public void onSuccessVolley(JSONArray result) {
                boolean queryResult = false;
                if (result.length() == 0) {
                    queryResult = true;
                }
                callback.onSuccessDB(queryResult);
            }

            @Override
            public void onErrorVolley(String error) {
                callback.onErrorDB(error);
            }
        });
    }

    public void GetItemDetails(DBCallback<List<RentItem>> callback) {
        String ItemSuffix = "/GetLast10Items";
        String SendUrl = DBUrl + ItemSuffix;

        volleyGETRequest(SendUrl, new VolleyCallback() {
            @Override
            public void onSuccessVolley(JSONArray result) {
                try {
                    List<RentItem> rentItems = new ArrayList<>();

                    for (int i = 0; i < result.length(); i++) {
                        JSONObject obj = result.getJSONObject(i);

                        String title = obj.getString("item_name");
                        String description = obj.getString("description");
                        int price = obj.getInt("price_per_day");
                        String phone = obj.getString("phone_number");
                        String location = obj.getString("location");
                        String category = obj.getString("category_name");

                        // Decode Base64 photo
                        String base64Photo = obj.getString("photos");
                        byte[] imageBytes = Base64.decode(base64Photo, Base64.DEFAULT);
                        Bitmap photo = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);

                        RentItem item = new RentItem(title,photo, price, phone, location, description, category);
                        rentItems.add(item);
                    }
                    // Success: return parsed list
                    callback.onSuccessDB(rentItems);
                }
                catch (JSONException e) {
                    callback.onErrorDB(e.getMessage());
                    Log.e("Database", "JSON Error: " + e.getMessage(), e);
                }
            }

            @Override
            public void onErrorVolley(String error) {
                callback.onErrorDB(error);
                Log.e("Volley", "Error: " + error);
            }
        });
    }





    /*
    /--------------------------------------------------------------------------------------------------------------/
    */
    // General Methods
    public String sendGetRequestString(String urlString) {
        HttpURLConnection conn = null;
        StringBuilder response = new StringBuilder();
        String line;
        try {
            URL url = new URL(urlString);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();

            BufferedReader scanner = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            while ((line = scanner.readLine()) != null ) {
                response.append(line).append('\n');
            }
            scanner.close();
            return response.toString();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        return response.toString();
    }

     /*
    /--------------------------------------------------------------------------------------------------------------/
    */
    // General Methods - Volley version (better)
    private void volleyGETRequest( String GETRequestURL, VolleyCallback callback ) {
        requestQ = Volley.newRequestQueue(context);
        JsonArrayRequest submitRequest = new JsonArrayRequest(Request.Method.GET, GETRequestURL, null,

                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        callback.onSuccessVolley(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        callback.onErrorVolley(error.getLocalizedMessage());
                    }
                }
            );
        requestQ.add(submitRequest);
    };
    private void volleyPOSTRequest (String POSTRequestURL, final Map<String, String> postParams, VolleyCallback callback ) {
        requestQ = Volley.newRequestQueue(context);
        StringRequest submitRequest = new StringRequest(Request.Method.POST, POSTRequestURL,

                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jArray = new JSONArray(response);
                            callback.onSuccessVolley(jArray);
                        } catch (JSONException e) {
                            Log.e("Database", e.getMessage(), e);
                            callback.onErrorVolley("JSON parsing error: " + e.getMessage() );
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        callback.onErrorVolley(error.getLocalizedMessage());
                    }
                }
        ) {
            @Override
            protected Map<String,String> getParams() throws AuthFailureError {
                return postParams != null ? postParams : new HashMap<String, String>();
            }
        };
        requestQ.add(submitRequest);
    }
}
