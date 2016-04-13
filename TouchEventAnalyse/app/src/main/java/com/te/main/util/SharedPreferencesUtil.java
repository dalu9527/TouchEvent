package com.te.main.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * SharedPreferences的相关处理
 * @author xxoo
 * @date 16-4-13.
 */
public class SharedPreferencesUtil {

    private static SharedPreferences getSharedPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static void writeBoolean(Context context, String key, boolean value) {
        SharedPreferences appPrefs = getSharedPreferences(context);
        SharedPreferences.Editor edit = appPrefs.edit();
        edit.putBoolean(key, value);
        edit.apply();
    }


    public static boolean getBoolean(Context context, String key, boolean def) {
        SharedPreferences appPrefs = getSharedPreferences(context);
        return appPrefs.getBoolean(key, def);
    }

}
