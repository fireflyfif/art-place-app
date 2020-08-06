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

import org.threeten.bp.LocalDate;
import org.threeten.bp.format.DateTimeFormatter;

public class Utils {

    private Utils() {}

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

}
