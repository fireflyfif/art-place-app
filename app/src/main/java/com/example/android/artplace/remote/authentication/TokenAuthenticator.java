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

package com.example.android.artplace.remote.authentication;

import android.support.annotation.NonNull;
import android.util.Log;

import com.example.android.artplace.model.token.TypeToken;
import com.example.android.artplace.remote.ArtsyApiInterface;
import com.example.android.artplace.remote.ArtsyApiManager;
import com.example.android.artplace.utils.TokenManager;

import java.io.IOException;

import okhttp3.Authenticator;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;
import retrofit2.Call;

import static com.example.android.artplace.BuildConfig.CLIENT_ID;
import static com.example.android.artplace.BuildConfig.CLIENT_SECRET;


public class TokenAuthenticator implements Authenticator {

    private static final String TAG = TokenAuthenticator.class.getSimpleName();

    private static TokenAuthenticator INSTANCE;
    private TokenManager mTokenManager;
    private String mToken;

    private TokenAuthenticator() {

    }

    static synchronized TokenAuthenticator getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new TokenAuthenticator();
        }

        return INSTANCE;
    }

    @Override
    public Request authenticate(@NonNull Route route, @NonNull Response response) throws IOException {

        // Get the token from the preferences
        //TypeToken token = mTokenManager.getNewToken();
        
        TokenService tokenService = ArtsyApiManager.createService(TokenService.class);

        /*ArtPlaceApp.getInstance().getToken().refreshToken(CLIENT_ID, CLIENT_SECRET)
                .enqueue(new Callback<TypeToken>() {

                    @Override
                    public void onResponse(@NonNull Call<TypeToken> call, @NonNull retrofit2.Response<TypeToken> response) {

                        if (response.isSuccessful()) {
                            TypeToken tokenObject = response.body();
                            if (tokenObject != null) {
                                // Save the token into Shared Preferences
                                mTokenManager.saveToken(tokenObject);
                                //mToken = newToken.getToken();
                                Log.d(TAG, "Token saved into SharedPrefs");
                            }

                            Log.d(TAG, "Get new Token loaded successfully! " + response.code());
                        } else if (response.code() == HTTP_UNAUTHORIZED) {
                            Log.d(TAG, "Response code 401 - Unauthorized!");
                        } else {
                            mToken = "";
                            Log.d(TAG, "Get new Token loaded NOT successfully! " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<TypeToken> call, @NonNull Throwable t) {
                        Log.d(TAG, "OnFailure! " + t.getMessage());
                    }
                });*/

        Call<TypeToken> call = tokenService.refreshToken(CLIENT_ID, CLIENT_SECRET);
        retrofit2.Response<TypeToken> tokenResponse = call.execute();

        if (tokenResponse.isSuccessful()) {
            TypeToken tokenObject = tokenResponse.body();
            String newToken = null;
            if (tokenObject != null) {
                mTokenManager.saveToken(tokenObject);
                newToken = tokenObject.getToken();
                Log.d(TAG, "Token from authenticate2 saved into SharedPrefs: " + newToken);
            }

            return response
                    .request()
                    .newBuilder()
                    .header("X-XAPP-Token", newToken)
                    .build();
        } else {
            return null;
        }
    }

}

