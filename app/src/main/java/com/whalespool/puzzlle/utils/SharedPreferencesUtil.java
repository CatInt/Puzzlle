package com.whalespool.puzzlle.utils;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.whalespool.puzzlle.module.User;

/**
 * Created by yodazone on 2016/10/12.
 */

public class SharedPreferencesUtil {

    public static final String SP_NAME = "puzzle";
    public static final String SP_KEY_LEVEL = "level";
    public static final String SP_KEY_USER = "username";

    private SharedPreferences mPreferences;

    public SharedPreferencesUtil(Application application) {
        mPreferences = application.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
    }

    public <T> T getObject(String tag, Class<T> classT) {
        String str = mPreferences.getString(tag, null);
        if (str == null) {
            return null;
        }
        return new Gson().fromJson(str, classT);

    }

    public void setObject(String tag, Object obj) {
        String str = new Gson().toJson(obj);
        setString(tag, str);
    }

    private void setString(String tag, String obj) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putString(tag, obj);
        editor.apply();
    }

    private int getInt(String tag, int defValue) {
        return mPreferences.getInt(tag, defValue);
    }

    private String getString(String tag, String defValue) {
        return mPreferences.getString(tag, defValue);
    }

    public User getCurrentUser() {
        return getObject(SharedPreferencesUtil.SP_KEY_USER, User.class);
    }

    public int getCurrentLevel() {
        return getInt(SharedPreferencesUtil.SP_KEY_LEVEL, 3);
    }

    public void saveCurrentLevel(int level) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putInt(SP_KEY_LEVEL, level);
        editor.apply();
    }

//
}
