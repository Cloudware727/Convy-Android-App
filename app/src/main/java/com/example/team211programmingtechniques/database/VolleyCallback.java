package com.example.team211programmingtechniques.database;
import org.json.JSONArray;
// This interface is used for the purpose of wrapping raw Volley responses
// Used within DBObject to handle Volley
// Low-level
// We need an interface as Volley works in an asynchronous way
public interface VolleyCallback {
    void onSuccessVolley(JSONArray result);
    void onErrorVolley(String error);
}
