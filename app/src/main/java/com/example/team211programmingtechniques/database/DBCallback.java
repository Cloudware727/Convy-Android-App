package com.example.team211programmingtechniques.database;
// This interface is used for the purpose of wrapping processed, final data
// Used by callers of DBObject methods
// High-level
public interface DBCallback<T> {
    void onSuccessDB(T result);
    void onErrorDB(String error);
}
