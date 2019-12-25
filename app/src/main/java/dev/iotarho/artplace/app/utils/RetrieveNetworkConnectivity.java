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

import android.os.AsyncTask;
import android.util.Log;

import java.lang.ref.WeakReference;

import dev.iotarho.artplace.app.ArtPlaceApp;
import dev.iotarho.artplace.app.R;
import dev.iotarho.artplace.app.callbacks.SnackMessageListener;
import dev.iotarho.artplace.app.ui.artworks.ArtworksFragment;

/**
 * AsyncTask that is used for requesting a network connection upon a Refresh button click from the user
 *
 * Note: It's not ideal in this case, because it's being destroyed with the Lifecylce of the Activity,
 * but it demonstrate a use case of AsyncTask that is needed for passing the Rubrics fro Capstone Stage 2
 */
public class RetrieveNetworkConnectivity extends AsyncTask<String, Void, String> {

    private static final String TAG = RetrieveNetworkConnectivity.class.getSimpleName();

    private WeakReference<ArtworksFragment> mContext;
    private SnackMessageListener mListener;

    boolean flag = false;
    private Exception mException;


    public RetrieveNetworkConnectivity(ArtworksFragment context, SnackMessageListener snackMessageListener) {
        mContext = new WeakReference<>(context);
        mListener = snackMessageListener;
    }

    @Override
    protected String doInBackground(String... result) {

        String snackMessage;
        try {
            boolean isConnected = ConnectivityUtils.isConnected();
            if (isConnected) {
                flag = true;
                Log.d(TAG, "doInBackground, network=true ");

                // Cannot extract the string here, because it's a static method
                snackMessage = ArtPlaceApp.getInstance().getString(R.string.network_ok);

                return snackMessage;
            } else {
                flag = false;
                Log.d(TAG, "doInBackground, network= false");

                // Cannot extract the string here, because it's a static method
                snackMessage = ArtPlaceApp.getInstance().getString(R.string.no_network);

                return snackMessage;
            }
        } catch (Exception e) {
            this.mException = e;
            return null;
        }
    }

    @Override
    protected void onPostExecute(String result) {
        if (mListener != null) {
            if (flag) {
                // Show a message to the user there is an internet connection
                mListener.showSnackMessage(result);

                Log.d(TAG, "onPostExecute called with connectivity ON");

            } else {
                // Show a message to the user there is No internet connection
                mListener.showSnackMessage(result);

                Log.d(TAG, "onPostExecute called with NO connectivity");
            }
        }
    }
}
