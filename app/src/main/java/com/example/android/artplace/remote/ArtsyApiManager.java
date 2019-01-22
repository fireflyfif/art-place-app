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

import com.example.android.artplace.ArtPlaceApp;
import com.example.android.artplace.model.CustomArtsyDeserializer;
import com.example.android.artplace.model.artists.CustomArtistsDeserializer;
import com.example.android.artplace.model.artists.EmbeddedArtists;
import com.example.android.artplace.model.artworks.ArtworkWrapperResponse;
import com.example.android.artplace.model.artworks.CustomArtworksDeserializer;
import com.example.android.artplace.model.artworks.EmbeddedArtworks;
import com.example.android.artplace.model.token.TypeToken;
import com.example.android.artplace.remote.authentication.TokenAuthenticator;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.android.artplace.BuildConfig.CLIENT_ID;
import static com.example.android.artplace.BuildConfig.CLIENT_SECRET;
import static com.example.android.artplace.utils.Utils.BASE_ARTSY_URL;
import static java.net.HttpURLConnection.HTTP_UNAUTHORIZED;

public class ArtsyApiManager {

    private static OkHttpClient.Builder client;
    private static String mToken;

    private final static OkHttpClient sClient = buildClient();
    private final static Retrofit sRetrofit = buildRetrofit(sClient);

    private static final String TAG = ArtsyApiManager.class.getSimpleName();



    private static OkHttpClient buildClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
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

    private static Retrofit buildRetrofit(OkHttpClient client) {
        return new Retrofit.Builder()
                .baseUrl(BASE_ARTSY_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static <T> T createService(Class<T> service) {
        return sRetrofit.create(service);
    }

    /*public static <T> T createServiceWithAuth(Class<T> service) {
        OkHttpClient newClient = sClient.newBuilder().addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();

                Request.Builder builder = request.newBuilder();

                *//*if (tokenManager.getNewToken().getToken() != null) {
                    // nothing to do here
                }*//*

                request = builder.build();

                return chain.proceed(request);
            }
        }).authenticator(TokenAuthenticator.getInstance()).build();

        Retrofit newRetrofit = sRetrofit
                .newBuilder()
                .client(newClient)
                .build();

        return newRetrofit.create(service);
    }*/


    /**
     * Make the call to get the token
     * TODO: The method does not save the token
     * @return
     */
    private static String getNewToken() {
        // mToken can't save the new value of the token from the response.
        // TODO: Find a way to store the value of the token!

       /* ArtPlaceApp.getInstance().getToken().refreshToken(CLIENT_ID, CLIENT_SECRET)
                .enqueue(new Callback<TypeToken>() {

                    TypeToken typeToken = new TypeToken();
                    @Override
                    public void onResponse(@NonNull Call<TypeToken> call, @NonNull retrofit2.Response<TypeToken> response) {

                        if (response.isSuccessful()) {
                            typeToken = response.body();
                            // Save the token into Shared Preferences
                            //tokenManager.saveToken(response.body());

                            if (typeToken != null) {
                                mToken = typeToken.getToken();

                                Log.d(TAG, "New obtained token: " + mToken);
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

        // The value is always null
        if (mToken == null) {
            mToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJyb2xlcyI6IiIsImV4cCI6MTU0MjkyMDQ3MSwiaWF0IjoxNTQyMzE1NjcxLCJhdWQiOiI1YjNjZGRhMWNiNGMyNzE2ZTliZTQyOWYiLCJpc3MiOiJHcmF2aXR5IiwianRpIjoiNWJlZGRlOTdhOTNjM2EwMDJiZTRiNjI0In0.IV75y4dXfNXe7LfXs4E3GVnV3a4O55Bg7n82RttSOrw";
        }
        Log.d(TAG, "New obtained token finally: " + mToken);
        return mToken;
    }


    ////\\\\////\\\\////\\\\////\\\\////\\\\////\\\\////\\\\////\\\\////\\\\////\\\\////\\\\////\\\\

    // Provide the Retrofit call
    public static <T> T createApiCall (Class<T> service) {

        // For logging the url that is being made
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        // TODO: Temp solution for getting the newest token
        //mToken = getNewToken();
        mToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJyb2xlcyI6IiIsImV4cCI6MTU0ODYwMDYwOSwiaWF0IjoxNTQ3OTk1ODA5LCJhdWQiOiI1YjNjZGRhMWNiNGMyNzE2ZTliZTQyOWYiLCJpc3MiOiJHcmF2aXR5IiwianRpIjoiNWM0NDhhYTEwYjY2YTM1YjU1OTFjYzFjIn0.hwDECmkK70y3Pl1VtBiRfek8Pspxrs7gMviIZoa-zLk";

        Log.d(TAG, "Token is: " + mToken);

        OkHttpClient newClient = new OkHttpClient.Builder().addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request newRequest = chain.request().newBuilder()
                        // Temp way of hard-coding the token
                        .addHeader("X-XAPP-Token", mToken)
                        //.addHeader("X-XAPP-Token", tokenManager.getNewToken().getToken())
                        //.addHeader("X-XAPP-Token", getNewToken())
                        .build();



                return chain.proceed(newRequest);
            }
        }).authenticator(TokenAuthenticator.getInstance())
                .addInterceptor(interceptor)
                .build();

        Retrofit newRetrofit = sRetrofit
                .newBuilder()
                .client(newClient)
                .build();

        return newRetrofit.create(service);
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
