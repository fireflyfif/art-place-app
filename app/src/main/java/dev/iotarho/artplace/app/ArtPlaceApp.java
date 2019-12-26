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

package dev.iotarho.artplace.app;

import android.app.Application;
import android.content.Context;

import androidx.appcompat.app.AppCompatDelegate;

import com.google.firebase.analytics.FirebaseAnalytics;

import dev.iotarho.artplace.app.utils.PreferenceUtils;

// Singleton class that extends the Application.
// Singleton pattern, explained here: https://medium.com/exploring-code/how-to-make-the-perfect-singleton-de6b951dfdb0
public class ArtPlaceApp extends Application {

    // With volatile variable all the write will happen on volatile sInstance
    // before any read of sInstance variable
    private static volatile ArtPlaceApp sInstance;

    // Set the theme to the whole app
    static {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
    }


    @Override
    public void onCreate() {
        super.onCreate();

        sInstance = this;
        FirebaseAnalytics.getInstance(getApplicationContext());
        PreferenceUtils.createInstance(getApplicationContext());
    }

    public static ArtPlaceApp getInstance() {
        // Double check locking pattern
        if (sInstance == null) {

            synchronized (ArtPlaceApp.class) {
                // If there is no instance available, create one
                if (sInstance == null) {
                    sInstance = new ArtPlaceApp();
                }
            }
        }

        return sInstance;
    }

    /**
     * Helper method that gets the context of the application
     */
    private static ArtPlaceApp get(Context context) {
        return (ArtPlaceApp) context.getApplicationContext();
    }

    /**
     * Method that is used for initializing  the ViewModel,
     * because the ViewModel class has a parameter of ArtPlaceApp
     */
    public static ArtPlaceApp create(Context context) {
        return ArtPlaceApp.get(context);
    }

}
