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
import android.arch.paging.PagedList;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.widget.Toast;

import com.example.android.artplace.ArtPlaceApp;
import com.example.android.artplace.R;
import com.example.android.artplace.ui.adapters.ArtworkListAdapter;
import com.example.android.artplace.model.Artwork;
import com.example.android.artplace.viewmodel.ArtworksViewModel;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements ArtworkListAdapter.OnArtworkClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String ARTWORK_PARCEL_KEY = "artwork_key";

    @BindView((R.id.artworks_rv))
    RecyclerView artworksRv;

    private ArtworkListAdapter mPagedListAdapter;
    private ArtworksViewModel mViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        // Initialize the ViewModel
        mViewModel = new ArtworksViewModel(ArtPlaceApp.create(this));
        //mViewModel = ViewModelProviders.of(this).get(ArtworksViewModel.class);

        int columnCount = getResources().getInteger(R.integer.list_column_count);
        StaggeredGridLayoutManager staggeredGridLayoutManager =
                new StaggeredGridLayoutManager(columnCount, StaggeredGridLayoutManager.VERTICAL);

        artworksRv.setLayoutManager(staggeredGridLayoutManager);

        // Set the PagedListAdapter
        mPagedListAdapter = new ArtworkListAdapter(getApplicationContext(), this);

        mViewModel.getArtworkLiveData().observe(this, new Observer<PagedList<Artwork>>() {

            @Override
            public void onChanged(@Nullable PagedList<Artwork> artworks) {
                if (artworks != null) {
                    // When a new page is available, call submitList() method of the PagedListAdapter
                    mPagedListAdapter.submitList(artworks); // the paged list of artworks is 0, but it's working
                }
            }
        });

        //TODO: Get the network state

        artworksRv.setAdapter(mPagedListAdapter);
    }

    @Override
    public void onArtworkClick(Artwork artwork) {
        Toast.makeText(this, "Clicked artwork with id " + artwork.getId(), Toast.LENGTH_SHORT).show();

        Bundle bundle = new Bundle();
        bundle.putParcelable(ARTWORK_PARCEL_KEY, artwork);

        Intent intent = new Intent(MainActivity.this, ArtworkDetailActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);

    }

}
