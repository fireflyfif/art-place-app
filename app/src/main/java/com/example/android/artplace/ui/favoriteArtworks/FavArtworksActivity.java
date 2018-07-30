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

package com.example.android.artplace.ui.favoriteArtworks;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.arch.paging.PagedList;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.example.android.artplace.R;
import com.example.android.artplace.callbacks.OnFavItemClickListener;
import com.example.android.artplace.database.entity.FavoriteArtworks;
import com.example.android.artplace.repository.FavArtRepository;
import com.example.android.artplace.ui.favoriteArtworks.adapter.FavArtworkListAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FavArtworksActivity extends AppCompatActivity implements OnFavItemClickListener {

    private static final String TAG = FavArtworksActivity.class.getSimpleName();

    @BindView(R.id.fav_artworks_rv)
    RecyclerView favArtworksRv;

    private FavArtworkListAdapter mAdapter;
    private FavArtRepository mFavArtRepository;
    private PagedList<FavoriteArtworks> mFavoriteArtworksList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fav_artworks);

        ButterKnife.bind(this);

        // COMPLETED: Too many instances are being called of the ArtworksDatabase class! Reduce that!
        //FavArtworksDao favArtworksDao = ArtworksDatabase.getInstance(getApplicationContext()).favArtworksDao();
        //mFavArtRepository = new FavArtRepository(getApplication());

        FavArtworksViewModel viewModel = ViewModelProviders.of(this).get(FavArtworksViewModel.class);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        favArtworksRv.setLayoutManager(layoutManager);

        mAdapter = new FavArtworkListAdapter(this, this);

        viewModel.getFavArtworkList().observe(this, new Observer<PagedList<FavoriteArtworks>>() {

            @Override
            public void onChanged(@Nullable PagedList<FavoriteArtworks> favoriteArtworks) {
                if (favoriteArtworks != null && favoriteArtworks.size() > 0) {
                    Log.d(TAG, "Submit artworks to the db " + favoriteArtworks.size());
                    mAdapter.submitList(favoriteArtworks);
                    // TODO: Add the button for add to favorites
                } else {
                    Log.e(TAG, "No artworks added to the database");
                }

                mFavoriteArtworksList = favoriteArtworks;
            }
        });

        favArtworksRv.setAdapter(mAdapter);

    }

    @Override
    public void onFavItemClick(FavoriteArtworks favArtworks) {
        // TODO: Make the Intent to the DetailActivity
    }

}
