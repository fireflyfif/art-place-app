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

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.example.android.artplace.ArtPlaceApp;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

// Help from this SO port: https://stackoverflow.com/a/25816086/8132331
public class ConnectivityUtils {

    private static final String TAG = ConnectivityUtils.class.getSimpleName();


    public ConnectivityUtils() {
    }

    private static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                ArtPlaceApp.getInstance().getApplicationContext()
                        .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = null;
        if (connectivityManager != null) {
            networkInfo = connectivityManager.getActiveNetworkInfo();
        }

        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }

    public static boolean isConnected(Context context) {
        if (isNetworkAvailable(context)) {
            try {
                HttpURLConnection urlConnection = (HttpURLConnection)
                        (new URL("http://clients3.google.com/generate_204")
                                .openConnection());
                urlConnection.setRequestProperty("User/Agent", "Android");
                urlConnection.setRequestProperty("Connection", "close");
                urlConnection.setConnectTimeout(1500);
                urlConnection.connect();
                return (urlConnection.getResponseCode() == 204 &&
                        urlConnection.getContentLength() == 0);
            } catch (IOException e) {
                Log.e(TAG, "Error checking internet connection", e) ;
            }
        } else {
            Log.d(TAG, "No network available!");
        }

        return false;
    }

}
