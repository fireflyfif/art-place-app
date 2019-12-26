package dev.iotarho.artplace.app.utils;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import dev.iotarho.artplace.app.model.token.TypeToken;

import static dev.iotarho.artplace.app.utils.Utils.TOKEN_VALUE_KEY;

public class PreferenceUtils {

    private static volatile PreferenceUtils sInstance;
    private static final Object LOCK = new Object();
    private SharedPreferences mPrefs;

    public PreferenceUtils(Context context) {
        this.mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public synchronized static void createInstance(Context context) {
        if (sInstance == null) {
            synchronized (LOCK) {
                if (sInstance == null) {
                    sInstance = new PreferenceUtils(context);
                }
            }
        }
    }

    public static PreferenceUtils getInstance() {
        if (sInstance == null) {
            throw new IllegalStateException("Preference instance not initialized");
        }
        return sInstance;
    }

    public void saveToken(TypeToken tokenObject) {
        mPrefs
                .edit()
                .putString(TOKEN_VALUE_KEY, tokenObject.getToken())
                .apply();
    }

    public String getToken() {
        return mPrefs
                .getString(TOKEN_VALUE_KEY, "");
    }
}
