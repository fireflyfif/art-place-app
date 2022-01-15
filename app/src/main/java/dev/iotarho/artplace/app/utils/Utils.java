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

package dev.iotarho.artplace.app.utils;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;

import org.threeten.bp.LocalDate;
import org.threeten.bp.format.DateTimeFormatter;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class Utils {

    private static Random rand = new Random();

    private Utils() {
    }

    public static final String PREFS_TOKEN_KEY = "com.example.android.artplace.PREFS_TOKEN_KEY";

    public static boolean isNullOrEmpty(@Nullable String string) {
        return string == null || string.trim().isEmpty();
    }

    public static boolean isTokenExpired(@NonNull String expiresAt) {
        if (isNullOrEmpty(expiresAt)) {
            return false;
        }
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("u-M-d");
        // get only the date of the expiry string
        // e.g. 2014-09-05T12:39:09.200Z
        String onlyDate = expiresAt.substring(0, 10);
        LocalDate validDate = LocalDate.parse(onlyDate, dateFormatter);
        LocalDate currentDate = LocalDate.now();
        Log.d("Utils", "currentDate is: " + currentDate + " validDate is: " + validDate);
        if (currentDate.isAfter(validDate)) {
            Log.d("Utils", "token is expired");
            return true;
        }
        Log.d("Utils", "token is not expired");
        return false;
    }

    public static String randomSearch() {
        String[] listArtist = {
                "Yayoi Kusama",
                "Roy Lichtenstein",
                "Cindy Sherman",
                "Keith Haring",
                "David Hockney",
                "Katherine Bernhardt",
                "Kehinde Wiley",
                "Ed Ruscha",
                "Banksy"
        };
        return listArtist[rand.nextInt(listArtist.length)];
    }

    public static int randomOffset() {
        int min = 1;
        int max = 100;
        return rand.nextInt((max - min) + 1) + min;
    }

    public static void setCollapsingToolbar(String titleString, AppBarLayout appBarLayout, CollapsingToolbarLayout collapsingToolbarLayout, int titleColor) {
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShown = true;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbarLayout.setTitle(titleString);
                    collapsingToolbarLayout.setCollapsedTitleTextColor(titleColor);
                    isShown = true;
                } else if (isShown) {
                    collapsingToolbarLayout.setTitle(titleString);//careful there should a space between double quote otherwise it wont work
                    collapsingToolbarLayout.setCollapsedTitleTextColor(titleColor);
                    isShown = false;
                }
            }
        });
    }

}
