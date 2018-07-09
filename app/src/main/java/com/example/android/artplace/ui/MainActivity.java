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

package com.example.android.artplace.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.example.android.artplace.BuildConfig;
import com.example.android.artplace.R;
import com.example.android.artplace.adapters.ArtworksAdapter;
import com.example.android.artplace.model.Artwork;
import com.example.android.artplace.model.Embedded;
import com.example.android.artplace.remote.MainApplication;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.android.artplace.BuildConfig.TOKEN;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private ArtworksAdapter mArtworkAdapter;
    private RecyclerView mArtworkRv;
    private Embedded mEmbeddedObject;
    private List<Artwork> mArtworkList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mArtworkRv = findViewById(R.id.artworks_rv);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        mArtworkRv.setLayoutManager(gridLayoutManager);

        // Create new instance of the Embedded object
        mEmbeddedObject = new Embedded();
        mArtworkList = new ArrayList<>();

        loadArtworks();
    }

    private void loadArtworks() {

        MainApplication.sManager.getEmbedded(new Callback<Embedded>() {

            @Override
            public void onResponse(Call<Embedded> call, Response<Embedded> response) {
                if (response.isSuccessful()) {

                    mArtworkList = mEmbeddedObject.getArtworks();
                    //Log.d(TAG, "List of Artworks: " + mArtworkList.size());

                    if (mArtworkAdapter == null) {
                        mArtworkAdapter = new ArtworksAdapter(this, mEmbeddedObject, mArtworkList);
                        mArtworkRv.setAdapter(mArtworkAdapter);
                        mArtworkRv.setHasFixedSize(true);
                    }

                    int statusCode = response.code();
                    Log.e(TAG, "Response code: " + statusCode);

                } else {
                    int statusCode = response.code();
                    Log.e(TAG, "Response code: " + statusCode);
                }
            }

            @Override
            public void onFailure(Call<Embedded> call, Throwable t) {

                Log.e(TAG, "onFailure called with msg: " + t.toString());
            }
        }, TOKEN);
    }
}
