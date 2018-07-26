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

import android.os.Looper;
import android.support.annotation.NonNull;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import android.os.Handler;

// Help from this resource: https://github.com/udacity/ud851-Exercises/blob/student/Lesson09b-ToDo-List-AAC/T09b.04-Exercise-Executors/app/src/main/java/com/example/android/todolist/AppExecutors.java
public class AppExecutors {

    private static final Object LOCK = new Object();
    private static AppExecutors INSTANCE;
    private final Executor mDiskIO;
    private final Executor mNetworkIO;
    private final Executor mMainThread;

    // Make this class a singleton
    private AppExecutors(Executor diskIO, Executor networkIO, Executor mainThread) {
        mDiskIO = diskIO;
        mNetworkIO = networkIO;
        mMainThread = mainThread;
    }

    public static AppExecutors getInstance() {
        if (INSTANCE == null) {
            synchronized (LOCK) {
                INSTANCE = new AppExecutors(Executors.newSingleThreadExecutor(),
                        Executors.newFixedThreadPool(5),
                        new MainThreadExecutor());
            }
        }
        return INSTANCE;
    }

    public Executor networkIO() {
        return mNetworkIO;
    }

    public Executor diskIO() {
        return mDiskIO;
    }

    public Executor mainThread() {
        return mMainThread;
    }


    private static class MainThreadExecutor implements Executor {

        private Handler mainThreadHandler = new Handler(Looper.getMainLooper());

        @Override
        public void execute(@NonNull Runnable command) {
            mainThreadHandler.post(command);
        }
    }
}
