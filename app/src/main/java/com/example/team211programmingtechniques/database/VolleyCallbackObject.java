package com.example.team211programmingtechniques.database;

import org.json.JSONArray;
import org.json.JSONObject;

public interface VolleyCallbackObject {
    void onSuccessVolley(JSONObject result);
    void onErrorVolley(String error);

}
