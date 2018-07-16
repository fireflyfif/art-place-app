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

package com.example.android.artplace;

import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;

import com.example.android.artplace.model.CustomDeserializer;
import com.example.android.artplace.model.Embedded;
import com.example.android.artplace.remote.ArtsyApiInterface;
import com.example.android.artplace.remote.ArtsyApiManager;
import com.example.android.artplace.utils.Utils;
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

public class ArtPlaceApp extends Application {

    private ArtsyApiInterface mArtsyApi;

    private static ArtPlaceApp INSTANCE;

    public static ArtPlaceApp getInstance() {
        return INSTANCE;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        INSTANCE = this;
    }

    /*
    Helper method that gets the context of the application
     */
    private static ArtPlaceApp get(Context context) {
        return (ArtPlaceApp) context.getApplicationContext();
    }

    /*
    Method that is used for initializing  the ViewModel,
    because the ViewModel class has a parameter of ArtPlaceApp
     */
    public static ArtPlaceApp create(Context context) {
        return ArtPlaceApp.get(context);
    }


    /**
     * Method that creates a Retrofit instance from the ArtsyApiManager
     */
    public ArtsyApiInterface getArtsyApi() {
        if (mArtsyApi == null) {
            mArtsyApi = ArtsyApiManager.create();
        }
        return mArtsyApi;
    }

    public void setArtsyApi(ArtsyApiInterface artsyApi) {
        mArtsyApi = artsyApi;
    }
}
