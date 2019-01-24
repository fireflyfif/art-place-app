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

import com.example.android.artplace.model.CustomArtsyDeserializer;
import com.example.android.artplace.model.artists.CustomArtistsDeserializer;
import com.example.android.artplace.model.artists.EmbeddedArtists;
import com.example.android.artplace.model.artworks.ArtworkWrapperResponse;
import com.example.android.artplace.model.artworks.CustomArtworksDeserializer;
import com.example.android.artplace.model.artworks.EmbeddedArtworks;
import com.example.android.artplace.remote.authentication.TokenAuthenticator;
import com.example.android.artplace.utils.TokenManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.android.artplace.utils.Utils.BASE_ARTSY_URL;
import static com.example.android.artplace.utils.Utils.HEADER_TOKEN_KEY;

public class ArtsyApiManager {

    private final static OkHttpClient sClient = buildClient();
    private final static Retrofit sRetrofit = buildRetrofit();

    private static final String TAG = ArtsyApiManager.class.getSimpleName();


    private static OkHttpClient buildClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                // TODO: Q: How many interceptors can I add?
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request request = chain.request();
                        Request.Builder requestBuilder = request.newBuilder();
                        request = requestBuilder.build();
                        return chain.proceed(request);
                    }
                });

        return builder.build();
    }

    private static Retrofit buildRetrofit() {
        return new Retrofit.Builder()
                .baseUrl(BASE_ARTSY_URL)
                .client(ArtsyApiManager.sClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static <T> T createService(Class<T> service) {
        return sRetrofit.create(service);
    }

    // Provide the Retrofit call
    public static <T> T createApiCall(Class<T> service, TokenManager tokenManager) {

        // For logging the url that is being made
        HttpLoggingInterceptor logsInterceptor = new HttpLoggingInterceptor();
        logsInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        // TODO: Temp solution for getting the newest token
        String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJyb2xlcyI6IiIsImV4cCI6MTU0ODYwMDYwOSwiaWF0IjoxNTQ3OTk1ODA5LCJhdWQiOiI1YjNjZGRhMWNiNGMyNzE2ZTliZTQyOWYiLCJpc3MiOiJHcmF2aXR5IiwianRpIjoiNWM0NDhhYTEwYjY2YTM1YjU1OTFjYzFjIn0.hwDECmkK70y3Pl1VtBiRfek8Pspxrs7gMviIZoa-zLk";

        OkHttpClient newClient = new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        String tokenString = tokenManager.getToken();
                        Request newRequest = chain
                                .request()
                                .newBuilder()
                                // Temp way of hard-coding the token
                                //.addHeader("X-XAPP-Token", token)
                                .addHeader(HEADER_TOKEN_KEY, tokenString)
                                .build();
                        Log.d(TAG, "Token is: " + tokenString);

                        return chain.proceed(newRequest);
                    }
                })
                // Add the authenticator where the new token is being fetched
                .authenticator(TokenAuthenticator.getInstance(tokenManager))
                .addInterceptor(logsInterceptor)
                .build();

        Retrofit newRetrofit = getRetrofit(newClient);

        return newRetrofit.create(service);
    }

    @NonNull
    private static Retrofit getRetrofit(OkHttpClient newClient) {
        return sRetrofit
                .newBuilder()
                .client(newClient)
                .build();
    }

    /*
    Register the TypeAdapter here for deserializing the model
     */
    private static Gson makeGson() {

        // Register two different TypeAdapters!
        // resource: https://stackoverflow.com/a/33459073/8132331
        return new GsonBuilder()
                .registerTypeAdapter(ArtworkWrapperResponse.class, new CustomArtsyDeserializer())
                .registerTypeAdapter(EmbeddedArtworks.class, new CustomArtworksDeserializer())
                .registerTypeAdapter(EmbeddedArtists.class, new CustomArtistsDeserializer())
                .create();
    }

}
