package com.boyuan.identifier.system.common;

import android.app.Activity;
import android.content.*;
import android.content.SharedPreferences.Editor;

public final class Configuration
{
    public static final String ENCODING = "UTF-8";

    private static Configuration instance = null;

    private SharedPreferences preferences = null;

    private Configuration(Context context)
    {
    	preferences = context.getSharedPreferences("boyuan-identifier", 0x00);
    }
    
    public synchronized final int getInt(String key, int defaultVal)
    {
        return preferences.getInt(key, defaultVal);
    }

    public synchronized final long getLong(String key, long defaultVal)
    {
        return preferences.getLong(key, defaultVal);
    }
    
    public synchronized final float getFloat(String key, float defaultVal)
    {
    	return preferences.getFloat(key, defaultVal);
    }

    public synchronized final String getString(String key, String defaultVal)
    {
        return preferences.getString(key, defaultVal);
    }

    public synchronized final void put(String key, long val)
    {
        Editor editor = preferences.edit();
        editor.putLong(key, val);
        editor.commit();
    }
    
    public synchronized final void put(String key, float val)
    {
    	Editor editor = preferences.edit();
    	editor.putFloat(key, val);
    	editor.commit();
    }

    public synchronized final void put(String key, String val)
    {
        Editor editor = preferences.edit();
        editor.putString(key, val);
        editor.commit();
    }

    public synchronized final void put(String key, int val)
    {
        Editor editor = preferences.edit();
        editor.putInt(key, val);
        editor.commit();
    }

    public static synchronized final Configuration getInstance(Context context)
    {
        if (null == instance) instance = new Configuration(context);
        return instance;
    }
}
