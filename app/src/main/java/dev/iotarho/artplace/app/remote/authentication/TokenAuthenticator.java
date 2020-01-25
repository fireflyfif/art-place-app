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

package dev.iotarho.artplace.app.remote.authentication;

import android.util.Log;

import androidx.annotation.NonNull;

import java.io.IOException;

import dev.iotarho.artplace.app.callbacks.FetchTokenCallback;
import dev.iotarho.artplace.app.model.token.TypeToken;
import dev.iotarho.artplace.app.utils.PreferenceUtils;
import dev.iotarho.artplace.app.utils.TokenManager;
import dev.iotarho.artplace.app.utils.Utils;
import okhttp3.Authenticator;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;

// This class should be called when the token expires
// source: https://square.github.io/okhttp/recipes/
public class TokenAuthenticator implements Authenticator {

    private static final String TAG = TokenAuthenticator.class.getSimpleName();

    private static TokenAuthenticator INSTANCE;
    private TokenManager mTokenManager;
    private PreferenceUtils mPreferenceUtils;

    private TokenAuthenticator() {
        mTokenManager = TokenManager.getInstance();
        mPreferenceUtils = PreferenceUtils.getInstance();
    }

    public static synchronized TokenAuthenticator getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new TokenAuthenticator();
        }
        return INSTANCE;
    }

    @Override
    public Request authenticate(@NonNull Route route, @NonNull Response response) throws IOException {
        // Get token used in request
        String token = response.header(Utils.HEADER_TOKEN_KEY);
        Log.d(TAG, "token from the header: " + token); // null

        if (response.code() == 401) { //

        }

        // Refresh the token here: fetch and then save
        // TODO: Don't refresh if already we have it saved and it's not expired
        // Always do it in a synchronise block
        mTokenManager.fetchToken(new FetchTokenCallback() {
            @Override
            public void onSuccess(@NonNull TypeToken tokenObject) {
                // This fetch the token when needed
                // prevent fetching it 3 times
                String refreshedToken = tokenObject.getToken();
                String expiresAt = tokenObject.getExpiresAt();
                mPreferenceUtils.saveToken(tokenObject);
                mPreferenceUtils.saveExpiryDateOfToken(tokenObject);

                Log.d(TAG, "token fetched successfully, refreshedToken: " + refreshedToken + " ," + expiresAt);
            }

            @Override
            public void onError(@NonNull Throwable throwable) {
                Log.d(TAG, "error while fetching the token, " + throwable.getMessage());
            }
        });

        // Get the currently stored token
        String savedToken = mPreferenceUtils.getToken();
        Log.d(TAG, "token from prefs: " + savedToken);
        String expiresAt = mPreferenceUtils.getExpiryDate();
        Log.d(TAG, "token expired on: " + expiresAt);
        if (Utils.isNullOrEmpty(expiresAt) && Utils.isTokenExpired(expiresAt)) {
            Log.d(TAG, "token expired on: " + expiresAt);
            // todo: make a call to refresh the token
        }

        return response
                .request()
                .newBuilder()
                .header(Utils.HEADER_TOKEN_KEY, savedToken)
                .build();
    }
}

