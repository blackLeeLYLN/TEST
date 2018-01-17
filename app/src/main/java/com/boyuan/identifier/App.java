package com.boyuan.identifier;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

import com.boyuan.identifier.system.common.CrashHandler;
import com.boyuan.identifier.system.common.Device;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Administrator on 2016/2/29.
 */
public class App extends Application {
    public static Context mContext;


    @Override
    public void onCreate()
    {
        super.onCreate();
        mContext = this;

        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(getApplicationContext());

        Device.enablePanel();
    }


    @Override
    public void onTerminate() {
        super.onTerminate();
        mContext = null;
        Device.disablePanel();
    }


    private static List<Activity> listActivity = new ArrayList<>();

    public static void setListActivity(Activity activity) {
        listActivity.add(activity);
    }

    public static void finishActivity() {
        for (Activity activity : listActivity
                ) {
            activity.finish();
        }
    }

    public static void finishLastActivity() {
        if(listActivity.size()>0){
            listActivity.get(listActivity.size()-1).finish();
        }
    }


}
