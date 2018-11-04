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

package com.example.android.artplace.remote;

import android.support.annotation.NonNull;
import android.util.Log;

import com.example.android.artplace.model.token.TypeToken;

import java.io.IOException;

import okhttp3.Authenticator;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;
import retrofit2.Call;

import static com.example.android.artplace.utils.Utils.CLIENT_ID;
import static com.example.android.artplace.utils.Utils.CLIENT_SECRET;

public class TokenAuthenticator implements Authenticator {

    private static final String TAG = TokenAuthenticator.class.getSimpleName();

    private static TokenAuthenticator INSTANCE;
    private final TokenServiceHolder mTokenServiceHolder;
    private String token;

    private TokenAuthenticator(TokenServiceHolder tokenServiceHolder) {
        mTokenServiceHolder = tokenServiceHolder;
    }

    static synchronized TokenAuthenticator getInstance(TokenServiceHolder tokenServiceHolder) {
        if (INSTANCE == null) {
            INSTANCE = new TokenAuthenticator(tokenServiceHolder);
        }

        return INSTANCE;
    }


    @Override
    public Request authenticate(@NonNull Route route, @NonNull Response response) throws IOException {

        ArtsyApiInterface service = mTokenServiceHolder.get();
        if (service == null) {
            return null;
        }


        Call<TypeToken> call = service.refreshToken(CLIENT_ID, CLIENT_SECRET);
        retrofit2.Response<TypeToken> tokenResponse = call.execute();

        if (tokenResponse.isSuccessful()) {
            TypeToken typeToken = tokenResponse.body();
            if (typeToken != null) {
                token = typeToken.getToken();
            }
            Log.d(TAG, "Token: " + token);

            return response.request().newBuilder()
                    .build();
        } else {
            return null;
        }

    }

}

