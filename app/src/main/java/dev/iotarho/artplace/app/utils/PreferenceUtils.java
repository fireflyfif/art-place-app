package dev.iotarho.artplace.app.utils;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import dev.iotarho.artplace.app.model.token.TypeToken;

public class PreferenceUtils {

    private static final String PREFS_KEY_TOKEN = "prefs_token_key";
    private static final String PREFS_KEY_TOKEN_EXPIRY = "prefs_token_expiry_key";
    private static final String PREFS_KEY_APP_THEME = "prefs_key_theme";

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

    public void saveThemePrefs(int themeInt) {
        mPrefs.edit().putInt(PREFS_KEY_APP_THEME, themeInt).apply();
    }

    public int getThemeFromPrefs() {
        return mPrefs.getInt(PREFS_KEY_APP_THEME, -1);
    }

    public void saveToken(TypeToken tokenObject) {
        mPrefs.edit().putString(PREFS_KEY_TOKEN, tokenObject.getToken()).apply();
    }

    public void saveExpiryDateOfToken(TypeToken tokenObject) {
        mPrefs.edit().putString(PREFS_KEY_TOKEN_EXPIRY, tokenObject.getExpiresAt()).apply();
    }

    public String getExpiryDate() {
        return mPrefs.getString(PREFS_KEY_TOKEN_EXPIRY, "");
    }

    public String getToken() {
        return mPrefs.getString(PREFS_KEY_TOKEN, "");
    }
}
