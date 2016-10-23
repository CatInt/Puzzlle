package com.whalespool.puzzlle;

import android.app.Application;
import android.content.Context;

import com.squareup.leakcanary.LeakCanary;
import com.whalespool.puzzlle.utils.SharedPreferencesUtil;

import cn.bmob.v3.Bmob;

public class PuzzlleApplication extends Application {
    private static final String BMOB_API_KEY = "38d580929cfe6d1375311067d6a7efa0";
    public static Context mContext;

    public static SharedPreferencesUtil PreferencesUtil;

//    private static DishManager dm;

    @Override
    public void onCreate() {
        super.onCreate();
        LeakCanary.install(this);
        Bmob.initialize(this, BMOB_API_KEY);
        if (mContext == null) mContext = getApplicationContext();
        PreferencesUtil = new SharedPreferencesUtil(this);
    }


//    public static DishManager getDishManager(){
//        return dm;
//    }
}
