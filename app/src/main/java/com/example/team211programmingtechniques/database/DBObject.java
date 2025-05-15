package com.example.team211programmingtechniques.database;

// Imports
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.*;
import java.nio.charset.StandardCharsets; // For formatting queries to JSON WEB API
import java.util.Scanner;

public class DBObject {
    // URLS
    final String DBUrl;
    public DBObject() {
        this.DBUrl = "https://studev.groept.be/api/a24pt211";
    }
    /*
    /--------------------------------------------------------------------------------------------------------------/
    */
    // Specific methods
    public boolean isDatabaseReachable() {
        String pingSuffix = "/ping";
        String finalURL = DBUrl + pingSuffix;
        String dbResponse = sendGetRequestString(finalURL);

        JSONArray jArray;
        try {
            jArray = new JSONArray(dbResponse);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        JSONObject jObject;
        try {
            jObject = jArray.getJSONObject(0);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        boolean status;
        try {
            status = (jObject.getInt("1") == 1);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return status;
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

    public boolean sendPostRequest(String urlString, String params) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);

            // Send POST request parameters
            OutputStream os = conn.getOutputStream();
            os.write(params.getBytes());
            os.flush();
            os.close();

            // Read API response
            Scanner scanner = new Scanner(conn.getInputStream());
            StringBuilder response = new StringBuilder();
            while (scanner.hasNext()) {
                response.append(scanner.nextLine());
            }
            scanner.close();

            // Check if response is empty
            if (response.toString().isEmpty()) {
                return false; // Empty response means creation failed
            }

            // Check if response contains success indicator
            return response.toString().equals("[]"); // Adjust based on actual API response

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
