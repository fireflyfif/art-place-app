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

import com.example.android.artplace.utils.Utils;
import com.example.android.artplace.model.CustomDeserializer;
import com.example.android.artplace.model.Embedded;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ArtsyApiManager {

    private static final String TAG = ArtsyApiManager.class.getSimpleName();

    private static Retrofit sRetrofit = null;
    private static ArtsyApiManager sApiManager;
    private static ArtsyApiInterface sArtsyApiInterface;

    private ArtsyApiManager() {

        if (sRetrofit == null) {

            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(interceptor).build();

            // Register the TypeAdapter here for deserializing the model
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(Embedded.class, new CustomDeserializer())
                    .create();

            sRetrofit = new Retrofit.Builder()
                    .baseUrl(Utils.BASE_ARTSY_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();

        }

        sArtsyApiInterface = sRetrofit.create(ArtsyApiInterface.class);

    }

    public static ArtsyApiManager getInstance() {
        if (sApiManager == null) {
            sApiManager = new ArtsyApiManager();
        }
        return sApiManager;
    }

    public void getEmbedded(Callback<Embedded> callback, String token, int pageSize) {
        Call<Embedded> artsyResponseCall = sArtsyApiInterface.getEmbedded(token, pageSize);
        artsyResponseCall.enqueue(callback);
    }

}
