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

import android.media.Image;
import android.net.Uri;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.artplace.R;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FavDetailActivity extends AppCompatActivity {

    private static final String ARTWORK_ID_KEY = "artwork_id";
    private static final String ARTWORK_TITLE_KEY = "artwork_title";
    private static final String ARTWORK_ARTIST_KEY = "artwork_artist";
    private static final String ARTWORK_CATEGORY_KEY = "artwork_category";
    private static final String ARTWORK_MEDIUM_KEY = "artwork_medium";
    private static final String ARTWORK_DATE_KEY = "artwork_date";
    private static final String ARTWORK_MUSEUM_KEY = "artwork_museum";
    private static final String ARTWORK_IMAGE_KEY = "artwork_image";

    @BindView(R.id.fav_detail_appbar)
    AppBarLayout appBarLayout;
    @BindView(R.id.fav_detail_toolbar)
    Toolbar toolbar;
    @BindView(R.id.fav_detail_collapsing_toolbar)
    CollapsingToolbarLayout collapsingToolbarLayout;
    @BindView(R.id.fav_detail_coordinator)
    CoordinatorLayout coordinatorLayout;
    @BindView(R.id.fav_detail_artwork_image)
    ImageView favImage;
    @BindView(R.id.fav_detail_title)
    TextView favTitle;
    @BindView(R.id.fav_detail_artist)
    TextView favArtist;
    @BindView(R.id.fav_detail_date)
    TextView favDate;
    @BindView(R.id.fav_detail_category)
    TextView favCategory;
    @BindView(R.id.fav_detail_medium)
    TextView favMedium;
    @BindView(R.id.fav_detail_museum)
    TextView favMuseum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fav_detail);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        // TODO: Add the parent to the Manifest
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if (getIntent().getExtras() != null) {

            String favIdString = getIntent().getStringExtra(ARTWORK_ID_KEY);
            String favTitleString = getIntent().getStringExtra(ARTWORK_TITLE_KEY);
            String favArtistString = getIntent().getStringExtra(ARTWORK_ARTIST_KEY);
            String favDateString = getIntent().getStringExtra(ARTWORK_DATE_KEY);
            String favImageString = getIntent().getStringExtra(ARTWORK_IMAGE_KEY);
            String favCategoryString = getIntent().getStringExtra(ARTWORK_CATEGORY_KEY);
            String favMediumString = getIntent().getStringExtra(ARTWORK_MEDIUM_KEY);
            String favMuseumString = getIntent().getStringExtra(ARTWORK_MUSEUM_KEY);

            collapsingToolbarLayout.setTitle(favTitleString);

            favTitle.setText(favTitleString);
            favArtist.setText(favArtistString);
            favDate.setText(favDateString);
            favMedium.setText(favMediumString);
            favMuseum.setText(favMuseumString);
            favCategory.setText(favCategoryString);

            Picasso.get()
                    .load(Uri.parse(favImageString))
                    .error(R.drawable.movie_video_02)
                    .placeholder(R.drawable.movie_video_02)
                    .into(favImage);

        }
    }
}
