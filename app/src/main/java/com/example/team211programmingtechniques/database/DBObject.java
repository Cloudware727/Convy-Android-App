package com.example.team211programmingtechniques.database;

// Imports - Json related
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.team211programmingtechniques.HistoryMyItem;
import com.example.team211programmingtechniques.HistoryRentedItems;
import com.example.team211programmingtechniques.RentItem;
import com.example.team211programmingtechniques.RouteInfo;

public class DBObject {
    // URLS
    final String DBUrl;
    // Volley-needed objects
    private RequestQueue requestQ;
    String apiKey = "AIzaSyAs8FEIso6r09Vy6MlVcyMaSOqY1b0dQts";
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

    public void retrieveUserInfo (String userName, DBCallback<String[]> callback) {
        String retriveSuffix = "/retrieveUserInfo/" + userName;
        String finalURL = DBUrl + retriveSuffix;
        
        volleyGETRequest(finalURL, new VolleyCallback() {
            @Override
            public void onSuccessVolley(JSONArray result) {
                // Default fallback
                String[] defaultInfo = new String[] { "null", "null", "null", "null", "null"};

                if (result == null || result.length() == 0) {
                    callback.onSuccessDB(defaultInfo);
                    return;
                }

                try {
                    // Just take the first object
                    JSONObject curObject = result.getJSONObject(0);
                    String[] actualInfo = userInfoStringArray(curObject);

                    callback.onSuccessDB(actualInfo);

                } catch (JSONException e) {
                    Log.e("Database", "JSON Parsing Error", e);
                    callback.onSuccessDB(defaultInfo); // fallback on failure
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
        SharedPreferences prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        String username = prefs.getString("username", "default");

        String ItemSuffix = "/GetLast10Items";
        String SendUrl = DBUrl + ItemSuffix + "/" +username;

        volleyGETRequest(SendUrl, new VolleyCallback() {
            @Override
            public void onSuccessVolley(JSONArray result) {
                try {
                    List<RentItem> rentItems = new ArrayList<>();

                    for (int i = 0; i < result.length(); i++) {
                        JSONObject obj = result.getJSONObject(i);

                        int id = obj.getInt("item_id");
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

                        RentItem item = new RentItem(id,title,photo, price, phone, location, description, category);
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

    public void getAllUserItems(String username, DBCallback<List<HistoryMyItem>> callback) {
        String suffix = "/getAllUserItems/" + username;
        String getURL = DBUrl + suffix;

        volleyGETRequest(getURL, new VolleyCallback() {
            @Override
            public void onSuccessVolley(JSONArray result) {
                try {
                    List<HistoryMyItem> historyMyItemList = new ArrayList<>();

                    for (int i = 0; i < result.length(); i++) {
                        JSONObject jObject = result.getJSONObject(i);
                        // retrieve all data
                        int id = jObject.getInt("item_id");
                        String name = jObject.getString("item_name");
                        String description = jObject.getString("description");
                        int price = jObject.getInt("price_per_day");
                        String dateListed = jObject.getString("date_listed");
                        String category = jObject.getString("category_name");
                        boolean status = jObject.getInt("status") == 1;

                        // Decode Base64 photo
                        String base64Photo = jObject.getString("photos");
                        byte[] imageBytes = Base64.decode(base64Photo, Base64.DEFAULT);
                        Bitmap photo = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                        // create new item
                        HistoryMyItem newItem = new HistoryMyItem(id, name, description, price, dateListed, category, status, photo);
                        historyMyItemList.add(newItem);
                    }

                    callback.onSuccessDB(historyMyItemList);

                } catch (JSONException e) {
                    callback.onErrorDB(e.getMessage());
                    Log.e("Database", "JSON Error" + e.getMessage(), e);
                }
            }

            @Override
            public void onErrorVolley(String error) {
                callback.onErrorDB(error);
                Log.e("Volley", "Error" + error);
            }
        });
    }
    public void CheckIfUserIsAccepted(DBCallback<Boolean> callback,int id ){
        String username =context.getSharedPreferences("user_prefs",Context.MODE_PRIVATE)
                .getString("username", null);
        String ItemEnd = "/CheckIfUserIsAccepted/";
        String SendUrl = DBUrl + ItemEnd + username + "/"+id;

        volleyGETRequest(SendUrl, new VolleyCallback() {

            @Override
            public void onSuccessVolley(JSONArray result) {
                try {
                    if (result == null || result.length() == 0) {
                        // No match found: not accepted
                        callback.onSuccessDB(false);
                        return;
                    }

                    JSONObject obj = result.getJSONObject(0);
                    int status = obj.optInt("status", 0); // Default to 0 if key missing
                    callback.onSuccessDB(status == 1);

                } catch (JSONException e) {
                    Log.e("Database", "JSON Parsing Error", e);
                    callback.onSuccessDB(false);
                }
            }

            @Override
            public void onErrorVolley(String error) {
                callback.onErrorDB(error);
                Log.e("Volley", "Error: " + error);
            }
        });
    }

    public void getDistanceBetween(String origin, String destination, DBCallback<String> callback) {
        String url = "https://maps.googleapis.com/maps/api/distancematrix/json?origins=" +
               Uri.encode(origin) + "&destinations=" + Uri.encode(destination)+
                "&key=" + apiKey;
        if(origin == null || origin.isEmpty()){
            callback.onSuccessDB("Please set your location");
            return;
        }

        volleyGETJsonObject(url, new VolleyCallbackObject() {
            @Override
            public void onSuccessVolley(JSONObject response) {
                Log.e("DistanceAPI", "Raw response: " + response.toString());
                try {
                    JSONObject element = response.getJSONArray("rows")
                            .getJSONObject(0)
                            .getJSONArray("elements")
                            .getJSONObject(0);

                    if (!element.getString("status").equals("OK")) {
                        callback.onSuccessDB("N/A");
                        return;
                    }

                    String distance = element.getJSONObject("distance").getString("text");
                    callback.onSuccessDB(distance);
                } catch (Exception e) {
                    Log.e("DistanceError", "Failed to parse distance", e);
                    callback.onSuccessDB("N/A");
                }

            }

            @Override
            public void onErrorVolley(String error) {
                callback.onErrorDB(error);
            }
        });
    }

    public void getMapRoute(String origin, String destination, DBCallback<RouteInfo> callback) {
        String url = "https://maps.googleapis.com/maps/api/directions/json?" +
                "origin=" + Uri.encode(origin) +
                "&destination=" + Uri.encode(destination) +
                "&key=" + apiKey;

        volleyGETJsonObject(url, new VolleyCallbackObject() {
            @Override
            public void onSuccessVolley(JSONObject response) {
                try {
                    JSONArray routes = response.getJSONArray("routes");
                    if (routes.length() == 0) {
                        callback.onSuccessDB(null);
                        return;
                    }

                    JSONObject firstRoute = routes.getJSONObject(0);
                    String polyline = firstRoute.getJSONObject("overview_polyline").getString("points");

                    JSONObject leg = firstRoute.getJSONArray("legs").getJSONObject(0);
                    String distance = leg.getJSONObject("distance").getString("text");
                    String duration = leg.getJSONObject("duration").getString("text");

                    callback.onSuccessDB(new RouteInfo(polyline, distance, duration));
                } catch (Exception e) {
                    Log.e("MapRouteError", "Failed to parse route", e);
                    callback.onSuccessDB(null);
                }
            }

            @Override
            public void onErrorVolley(String error) {
                callback.onErrorDB(error);
            }
        });
    }

    // Update First Name
    public void changeFirstName(String firstName, String username, DBCallback<Boolean> callback) {
        String url = DBUrl + "/changeFirstName/" + firstName + "/" + username;
        volleyGETRequest(url, new VolleyCallback() {
            @Override
            public void onSuccessVolley(JSONArray response) {
                if (response.length() == 0) {
                    callback.onSuccessDB(true);  // success
                } else {
                    callback.onSuccessDB(false); // something unexpected returned
                }
            }

            @Override
            public void onErrorVolley(String error) {
                callback.onErrorDB(error);
            }
        });
    }

    // Update Last Name
    public void changeLastName(String lastName, String username, DBCallback<Boolean> callback) {
        String url = DBUrl + "/changeLastName/" + lastName + "/" + username;
        volleyGETRequest(url, new VolleyCallback() {
            @Override
            public void onSuccessVolley(JSONArray response) {
                if (response.length() == 0) {
                    callback.onSuccessDB(true);  // success
                } else {
                    callback.onSuccessDB(false); // something unexpected returned
                }
            }

            @Override
            public void onErrorVolley(String error) {
                callback.onErrorDB(error);
            }
        });
    }

    // Update Email
    public void changeEmail(String email, String username, DBCallback<Boolean> callback) {
        String url = DBUrl + "/changeEmail/" + email + "/" + username;
        volleyGETRequest(url, new VolleyCallback() {
            @Override
            public void onSuccessVolley(JSONArray response) {
                if (response.length() == 0) {
                    callback.onSuccessDB(true);  // success
                } else {
                    callback.onSuccessDB(false); // something unexpected returned
                }
            }

            @Override
            public void onErrorVolley(String error) {
                callback.onErrorDB(error);
            }
        });
    }

    // Update Phone
    public void changePhone(String phoneNumber, String username, DBCallback<Boolean> callback) {
        String url = DBUrl + "/changePhone/" + phoneNumber + "/" + username;
        volleyGETRequest(url, new VolleyCallback() {
            @Override
            public void onSuccessVolley(JSONArray response) {
                if (response.length() == 0) {
                    callback.onSuccessDB(true);  // success
                } else {
                    callback.onSuccessDB(false); // something unexpected returned
                }
            }

            @Override
            public void onErrorVolley(String error) {
                callback.onErrorDB(error);
            }
        });
    }

    // Update Location
    public void changeLocation(String location, String username, DBCallback<Boolean> callback) {
        String url = DBUrl + "/changeLocation/" + location + "/" + username;
        volleyGETRequest(url, new VolleyCallback() {
            @Override
            public void onSuccessVolley(JSONArray response) {
                if (response.length() == 0) {
                    callback.onSuccessDB(true);  // success
                } else {
                    callback.onSuccessDB(false); // something unexpected returned
                }
            }

            @Override
            public void onErrorVolley(String error) {
                callback.onErrorDB(error);
            }
        });
    }

    public void AddAnOffer(int id,String username,DBCallback<Boolean> callback) {
        String url = DBUrl + "/AddAnOffer/" + id + "/" + username;
        volleyGETRequest(url, new VolleyCallback() {
            @Override
            public void onSuccessVolley(JSONArray response) {
                if (response.length() == 0) {
                    callback.onSuccessDB(true);  // success
                } else {
                    callback.onSuccessDB(false); // something unexpected returned
                }
            }

            @Override
            public void onErrorVolley(String error) {
                callback.onErrorDB(error);
            }
        });
    }

    // Return the offers based on a certain item id
    public void returnOffers(int itemID, DBCallback<List<String>> callback) {
        String suffix = "/getOffers/" + String.valueOf(itemID);
        String getURL = DBUrl + suffix;
        volleyGETRequest(getURL, new VolleyCallback() {
            @Override
            public void onSuccessVolley(JSONArray result) {
                try {
                    List<String> usernames = new ArrayList<>();
                    for (int i = 0; i < result.length(); i++) {
                        JSONObject jObject = result.getJSONObject(i);
                        String username = jObject.getString("username");
                        usernames.add(username);
                    }
                    callback.onSuccessDB(usernames);
                } catch (JSONException e) {
                    callback.onErrorDB(e.getMessage());
                    Log.e("Database", "JSON Error" + e.getMessage(), e);
                }
            }

            @Override
            public void onErrorVolley(String error) {
                callback.onErrorDB(error);
                Log.e("Volley", "Error" + error);
            }
        });

    }

    // For the rendering of the buttons
    public void returnOfferStatus(int itemID, String username, DBCallback<Integer> callback) {
        String suffix = "/returnOfferStatus/" + itemID + "/" + username;
        String getURL = DBUrl + suffix;

        volleyGETRequest(getURL, new VolleyCallback() {
            @Override
            public void onSuccessVolley(JSONArray result) {
                try {
                    if (result.length() > 0) {
                        JSONObject jObject = result.getJSONObject(0);
                        int status = jObject.getInt("status");  // assuming status is returned like this
                        callback.onSuccessDB(status);
                    } else {
                        callback.onErrorDB("No data found");
                    }
                } catch (JSONException e) {
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

    // Updates the status column for a certain item in the offers table
    public void updateOfferStatus(int status, int itemID, String username, DBCallback<Void> callback) {
        String suffix = "/updateOfferStatus/" + status + "/" + itemID + "/" + username;
        String getURL = DBUrl + suffix;
        volleyGETRequest(getURL, new VolleyCallback() {
            @Override
            public void onSuccessVolley(JSONArray result) {
                callback.onSuccessDB(null);  // or pass any needed response
            }

            @Override
            public void onErrorVolley(String error) {
                callback.onErrorDB(error);
            }
        });
    }


    // Updates the status column for a certain item in the item table
    public void updateItemStatus(int status, int itemID, DBCallback<String> callback) {
        String suffix = "/updateItemStatus/" + status + "/" + itemID;
        String url = DBUrl + suffix;

        volleyGETRequest(url, new VolleyCallback() {
            @Override
            public void onSuccessVolley(JSONArray result) {
                callback.onSuccessDB("Item status updated");
            }

            @Override
            public void onErrorVolley(String error) {
                callback.onErrorDB(error);
            }
        });
    }

    // Deletes all other rows in the offers table for a given item, except for a given item-username pair
    public void deleteOtherOffers(int itemID, String username, DBCallback<String> callback) {
        String suffix = "/deleteOtherOffers/" + itemID + "/" + username;
        String url = DBUrl + suffix;

        volleyGETRequest(url, new VolleyCallback() {
            @Override
            public void onSuccessVolley(JSONArray result) {
                callback.onSuccessDB("Other offers deleted");
            }

            @Override
            public void onErrorVolley(String error) {
                callback.onErrorDB(error);
            }
        });
    }

    // Deletes a certain offer within offers which has status == 1
    public void deleteCertainOffer(String username, int itemID, DBCallback<String> callback) {
        String suffix = "/deleteCertainOffer/" + username + "/" + itemID;
        String url = DBUrl + suffix;

        volleyGETRequest(url, new VolleyCallback() {
            @Override
            public void onSuccessVolley(JSONArray result) {
                callback.onSuccessDB("Offer deleted");
            }

            @Override
            public void onErrorVolley(String error) {
                callback.onErrorDB(error);
            }
        });
    }

    // Add an entry into transaction from the perspective of the lender
    public void addTransaction(int itemID, String renter, String lender, String date, DBCallback<String> callback) {
        // Note: the URL path as given is /addTransaction/item/renter/lender/date
        String suffix = "/addTransaction/" + itemID + "/" + renter + "/" + lender + "/" + date;
        String url = DBUrl + suffix;

        volleyGETRequest(url, new VolleyCallback() {
            @Override
            public void onSuccessVolley(JSONArray result) {
                callback.onSuccessDB("Transaction added");
            }

            @Override
            public void onErrorVolley(String error) {
                callback.onErrorDB(error);
            }
        });
    }


    public void SearchQuery(String search,DBCallback<List<Integer>> callback) {
        String url = DBUrl + "/SearchBarQuery/" + search;
        volleyGETRequest(url, new VolleyCallback() {
            @Override
            public void onSuccessVolley(JSONArray response) {
                    List<Integer> itemIds = new ArrayList<>();
                    try {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject obj = response.getJSONObject(i);
                            itemIds.add(obj.getInt("item_id"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                }
                callback.onSuccessDB(itemIds);
            }

            @Override
            public void onErrorVolley(String error) {
                callback.onErrorDB(error);
            }
        });
    }

    public void getItemsForRenting(String username, DBCallback<List<RentItem>> callback) {
        String suffix = "/GetItemsForRenting/" + username;
        String getURL = DBUrl + suffix;

        volleyGETRequest(getURL, new VolleyCallback() {
            @Override
            public void onSuccessVolley(JSONArray result) {
                try {
                    List<RentItem> rentingItemList = new ArrayList<>();

                    for (int i = 0; i < result.length(); i++) {
                        JSONObject jObject = result.getJSONObject(i);

                        int id = jObject.getInt("item_id");
                        String name = jObject.getString("item_name");
                        String description = jObject.getString("description");
                        int price = (int) jObject.getDouble("price_per_day");
                        String number = jObject.getString("phone_number");
                        String location = jObject.getString("location");
                        String category = jObject.getString("category_name");

                        // Decode Base64 photo
                        String base64Photo = jObject.getString("photos");
                        byte[] imageBytes = Base64.decode(base64Photo, Base64.DEFAULT);
                        Bitmap photo = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);

                        RentItem newItem = new RentItem(id, name, photo, price, number, location, description,category);
                        rentingItemList.add(newItem);
                    }

                    callback.onSuccessDB(rentingItemList);

                } catch (JSONException e) {
                    callback.onErrorDB(e.getMessage());
                    Log.e("Database", "JSON Error: " + e.getMessage(), e);
                }
            }

            @Override
            public void onErrorVolley(String error) {
                callback.onErrorDB(error);
                Log.e("Volley", "Request Error: " + error);
            }
        });
    }

    public void getAllRentedItems(String username, DBCallback<List<HistoryRentedItems>> callback) {
        String suffix = "/returnRentedItems/" + username;
        String getURL = DBUrl + suffix;

        volleyGETRequest(getURL, new VolleyCallback() {
            @Override
            public void onSuccessVolley(JSONArray result) {
                try {
                    List<HistoryRentedItems> rentedItemsList = new ArrayList<>();

                    for (int i = 0; i < result.length(); i++) {
                        JSONObject jObject = result.getJSONObject(i);
                        String itemName = jObject.getString("item_name");
                        String category = jObject.getString("category_name");
                        String lenderUsername = jObject.getString("lender_username");
                        String dateOfReturn = jObject.getString("date_of_sale");
                        int price = jObject.getInt("price_per_day");

                        HistoryRentedItems rentedItem = new HistoryRentedItems(itemName, category, lenderUsername, dateOfReturn, price);
                        rentedItemsList.add(rentedItem);
                    }

                    callback.onSuccessDB(rentedItemsList);
                } catch (JSONException e) {
                    callback.onErrorDB(e.getMessage());
                    Log.e("Database", "JSON parsing error: " + e.getMessage(), e);
                }
            }

            @Override
            public void onErrorVolley(String error) {
                callback.onErrorDB(error);
                Log.e("Volley", "Request error: " + error);
            }
        });
    }



    /*
    /--------------------------------------------------------------------------------------------------------------/
    */
    // General Methods

    private String[] userInfoStringArray(JSONObject obj) {
        return new String[] {
                obj.optString("first_name", "null"),
                obj.optString("last_name", "null"),
                obj.optString("email", "null"),
                obj.optString("phone_number", "null"),
                obj.optString("location", "null"),
        };
    }

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

    private void volleyGETJsonObject(String url, VolleyCallbackObject callback) {
        requestQ = Volley.newRequestQueue(context);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> callback.onSuccessVolley(response),
                error -> callback.onErrorVolley(error.getLocalizedMessage())
        );
        requestQ.add(request);
    }

}
