package com.darwin.fastclicker;
import android.content.Context;
import android.preference.PreferenceManager;

public class Preferences {

    private static final String PREF_RECORD = "lastRecord";
    private static final String PREF_THEME = "lastTheme";

    public static String getRecord(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(PREF_RECORD, "0");
    }
    public static void setRecord(Context context, String lastResult) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString(PREF_RECORD, lastResult)
                .apply();
    }

    public static String getTheme(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(PREF_THEME, "default");
    }
    public static void setTheme(Context context, String lastTheme) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString(PREF_THEME, lastTheme)
                .apply();
    }
}