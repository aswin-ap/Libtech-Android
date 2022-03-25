package com.wetwo.librarymanagment.data.prefrence;


import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.wetwo.librarymanagment.data.model.AddressModel;

public class SessionManager {

    private static String TAG = SessionManager.class.getSimpleName();

    private SharedPreferences shpref;
    private SharedPreferences.Editor editor;
    Context context;
    private static final String PREF_NAME = "FoodApp";
    private static final String KEY_IS_LOGED_IN = "isLoggedIn";
    private static final String USERID = "userId";
    private static final String USERNAME = "userName";
    private static final String RESTAURANT = "restaurant";
    private static final String ADDRESS = "address";
    private static final String DOCUMENT_ID = "documentId";
    private static final String MOBILE = "mobile";

    public SessionManager(Context context) {
        this.context = context;
        shpref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = shpref.edit();
    }

    public void setLogin(boolean isLoggedin) {
        editor.putBoolean(KEY_IS_LOGED_IN, isLoggedin);
        editor.apply();
        Log.e(TAG, "userlogin session");
    }

    public void setUserId(String name) {
        editor.putString(USERID, name);
        editor.apply();
    }

    public void setUserName(String name) {
        editor.putString(USERNAME, name);
        editor.apply();
    }

    public void setMobile(String name) {
        editor.putString(MOBILE, name);
        editor.apply();
    }

    public void setDocumentId(String id) {
        editor.putString(DOCUMENT_ID, id);
        editor.apply();
    }

    public void saveAddress(AddressModel model) {
        Gson gson = new Gson();
        String json = gson.toJson(model);
        editor.putString(ADDRESS, json);
        editor.apply();
    }

    public boolean isLoggedin() {
        return shpref.getBoolean(KEY_IS_LOGED_IN, false);
    }

    public String getUserId() {
        return shpref.getString(USERID, "");
    }

    public String getUserName() {
        return shpref.getString(USERNAME, "");
    }

    public String getCRestaurant() {
        return shpref.getString(RESTAURANT, "");
    }

    public String getDocumentId() {
        return shpref.getString(DOCUMENT_ID, "");
    }

    public String getMobile() { return shpref.getString(MOBILE, ""); }

    public AddressModel getAddress() {
        Gson gson = new Gson();
        String json = shpref.getString(ADDRESS, "");
        return gson.fromJson(json, AddressModel.class);
    }


    public void clear() {
        editor.clear();
        editor.apply();
    }
}

