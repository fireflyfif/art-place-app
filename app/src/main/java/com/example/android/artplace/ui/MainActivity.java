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

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.arch.paging.PagedList;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.android.artplace.BuildConfig;
import com.example.android.artplace.R;
import com.example.android.artplace.adapters.ArtworkListAdapter;
import com.example.android.artplace.adapters.ArtworksAdapter;
import com.example.android.artplace.datasource.ArtworkDataSourceFactory;
import com.example.android.artplace.model.Artwork;
import com.example.android.artplace.model.Embedded;
import com.example.android.artplace.remote.ArtsyApiManager;
import com.example.android.artplace.viewmodel.ArtworksViewModel;
import com.example.android.artplace.viewmodel.ArtworksViewModelFactory;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private ArtworksAdapter mArtworkAdapter;
    private RecyclerView mArtworkRv;
    private Embedded mEmbeddedObject;
    private List<Artwork> mArtworkList;

    private ArtsyApiManager mApiManager;

    private ArtworkListAdapter mPagedListAdapter;
    private ArtworksViewModel mViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize the ViewModel
        mViewModel = ViewModelProviders.of(this,
                new ArtworksViewModelFactory(ArtworkDataSourceFactory
                        .getInstance()))
                .get(ArtworksViewModel.class);

        mArtworkRv = findViewById(R.id.artworks_rv);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        mArtworkRv.setLayoutManager(gridLayoutManager);

        // Set the PagedAdapter
        mPagedListAdapter = new ArtworkListAdapter(getApplicationContext());

        // Create new instance of the Embedded object
        //mEmbeddedObject = new Embedded();
        //mArtworkList = new ArrayList<>();

        // When a new page is available, call submitList() method of the PagedListAdapter class

        mViewModel.getArtworkLiveData().observe(this, new Observer<PagedList<Artwork>>() {

            @Override
            public void onChanged(@Nullable PagedList<Artwork> artworks) {
                if (artworks != null) {
                    mPagedListAdapter.submitList(artworks); // artworks is 0
                }
            }
        });

        //TODO: Get the network state

        mArtworkRv.setAdapter(mPagedListAdapter);
    }

}
