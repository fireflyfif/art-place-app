/*
 * PROJECT LICENSE
 * This project was submitted by Iva Ivanova as part of the Nanodegree at Udacity.
 *
 * According to Udacity Honor Code we agree that we will not plagiarize (a form of cheating) the work of others. :
 * Plagiarism at Udacity can range from submitting a project you didn’t create to copying code into a program without
 * citation. Any action in which you misleadingly claim an idea or piece of work as your own when it is not constitutes
 * plagiarism.
 * Read more here: https://udacity.zendesk.com/hc/en-us/articles/360001451091-What-is-plagiarism-
 *
 * MIT License
 *
 * Copyright (c) 2018 Iva Ivanova
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 *
 */

package com.example.android.artplace.utils;

import android.content.SharedPreferences;
import android.util.Log;

import com.example.android.artplace.model.token.TypeToken;
import com.example.android.artplace.remote.ArtsyApiManager;
import com.example.android.artplace.remote.authentication.TokenService;

import java.io.IOException;

import retrofit2.Response;

import static com.example.android.artplace.BuildConfig.CLIENT_ID;
import static com.example.android.artplace.BuildConfig.CLIENT_SECRET;

/*
Helper class for saving the token into SharedPreferences
 */
public class TokenManager {

    private static final String TAG = TokenManager.class.getSimpleName();
    private static final String KEY_TOKEN = "GET_TOKEN";

    private SharedPreferences mPrefs;
    private SharedPreferences.Editor mEditor;

    private TokenService mTokenService;

    private static TokenManager INSTANCE;

    private TokenManager(SharedPreferences prefs) {
        mPrefs = prefs;
    }

    public static synchronized TokenManager getInstance(SharedPreferences prefs) {
        if (INSTANCE == null) {
            INSTANCE = new TokenManager(prefs);
        }
        return INSTANCE;
    }

    public void saveToken(TypeToken tokenObject) {
        mEditor.putString(KEY_TOKEN, tokenObject.getToken()).commit();
    }

    public void deleteToken() {
        mEditor.remove("GET_TOKEN").commit();
    }

    public String getToken() {
        return mPrefs.getString(KEY_TOKEN, "");
    }

    // TODO: Not sure if I need this method?
    public TypeToken getNewToken() {
        TypeToken token = new TypeToken();
        String tokenString = token.getToken();
        Log.d(TAG, "Get new token from TypeToken: " + tokenString);

        token.setToken(mPrefs.getString("GET_TOKEN", tokenString));
        return token;
    }

    public int refreshToken() {
        TokenService tokenService = ArtsyApiManager.createService(TokenService.class);

        Response<TypeToken> tokenResponse;
        int responseCode = 0;

        try {
            // TODO: Should I do this asynchronously with enqueue() method?
            tokenResponse = tokenService.refreshToken(CLIENT_ID, CLIENT_SECRET).execute();
            responseCode = tokenResponse.code();
            Log.d(TAG, "Response code: " + responseCode);

            if (tokenResponse.isSuccessful()) {
                TypeToken tokenObject = tokenResponse.body();

                String newToken;
                if (tokenObject != null) {
                    // Save the token into SharedPreferences
                    saveToken(tokenObject);
                    newToken = tokenObject.getToken();
                    Log.d(TAG, "Token from authenticate2 saved into SharedPrefs: " + newToken);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return responseCode;
    }
}
