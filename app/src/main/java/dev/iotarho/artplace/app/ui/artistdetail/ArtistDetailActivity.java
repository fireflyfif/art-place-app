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

package dev.iotarho.artplace.app.ui.artistdetail;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.card.MaterialCardView;
import com.ms.square.android.expandabletextview.ExpandableTextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import dev.iotarho.artplace.app.R;
import dev.iotarho.artplace.app.model.ImageLinks;
import dev.iotarho.artplace.app.model.artists.Artist;
import dev.iotarho.artplace.app.model.artworks.MainImage;
import dev.iotarho.artplace.app.ui.searchdetail.ShowDetailViewModelFactory;
import dev.iotarho.artplace.app.ui.searchdetail.ShowsDetailViewModel;
import dev.iotarho.artplace.app.utils.ArtistInfoUtils;
import dev.iotarho.artplace.app.utils.ImageUtils;
import dev.iotarho.artplace.app.utils.Injection;
import dev.iotarho.artplace.app.utils.Utils;

public class ArtistDetailActivity extends AppCompatActivity {

    private static final String TAG = ArtistDetailActivity.class.getSimpleName();

    public static final String ARTIST_URL_KEY = "artist_url";
    public static final String ARTIST_ARTWORK_URL_KEY = "artist_and_artwork_url";
    public static final String ARTIST_EXTRA_KEY = "artist_extra";

    @BindView(R.id.coordinator_artist)
    CoordinatorLayout coordinatorLayout;
    @BindView(R.id.toolbar_artist)
    Toolbar toolbar;
    @BindView(R.id.collapsing_toolbar_artist)
    CollapsingToolbarLayout collapsingToolbarLayout;
    @BindView(R.id.artist_cardview)
    MaterialCardView artistCard;
    @BindView(R.id.artist_name)
    TextView artistName;
    @BindView(R.id.artist_home)
    TextView artistHomeTown;
    @BindView(R.id.artist_image)
    ImageView artistImage;
    @BindView(R.id.artist_lifespan)
    TextView artistLifespan;
    @BindView(R.id.artist_location)
    TextView artistLocation;
    @BindView(R.id.artist_nationality)
    TextView artistNationality;
    @BindView(R.id.artist_bio)
    ExpandableTextView artistBio;
    @BindView(R.id.artist_bio_label)
    TextView artistBioLabel;
    @BindView(R.id.divider_2)
    View artistDivider;
    @BindView(R.id.hometown_label)
    TextView hometownLabel;
    @BindView(R.id.artist_location_label)
    TextView locationLabel;
    @BindView(R.id.artist_nationality_label)
    TextView artistNationalityLabel;

    private String artistBiography;
    private ArtistsDetailViewModel artistViewModel;
    private ShowsDetailViewModel showsDetailViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist_detail);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        ArtistDetailViewModelFactory artistDetailViewModelFactory = Injection.provideArtistDetailViewModel();
        artistViewModel = new ViewModelProvider(getViewModelStore(), artistDetailViewModelFactory).get(ArtistsDetailViewModel.class);
        ShowDetailViewModelFactory showDetailViewModelFactory = Injection.provideShowDetailViewModel();
        showsDetailViewModel = new ViewModelProvider(getViewModelStore(), showDetailViewModelFactory).get(ShowsDetailViewModel.class);

        // Get the ID from the clicked artwork from the received Intent
        if (getIntent().getExtras() != null) {
            if (getIntent().hasExtra(ARTIST_EXTRA_KEY)) {
                Artist artistFromIntent = getIntent().getParcelableExtra(ARTIST_EXTRA_KEY);
                Log.d(TAG, "Received artist extra from the intent: " + artistFromIntent.getName());
                setupUi(artistFromIntent);
            }

            if (getIntent().hasExtra(ARTIST_URL_KEY)) {
                String artistUrlExtra = getIntent().getStringExtra(ARTIST_URL_KEY);
                if (!Utils.isNullOrEmpty(artistUrlExtra)) {
                    Log.d(TAG, "Received artist url from the intent: " + artistUrlExtra);
                    initArtistContentViewModel(artistUrlExtra);
                }
            }

            if (getIntent().hasExtra(ARTIST_ARTWORK_URL_KEY)) {
                String receivedArtistUrlString = getIntent().getStringExtra(ARTIST_ARTWORK_URL_KEY);
                Log.d(TAG, "Received artist and artwork url from the intent: " + receivedArtistUrlString);

                artistViewModel.initArtistDataFromArtwork(receivedArtistUrlString);
                artistViewModel.getArtistDataFromArtwork().observe(this, artists -> {
                    if (artists != null) {
                        for (int i = 0; i < artists.size(); i++) {
                            Artist artistCurrent = artists.get(i);
                            setupUi(artistCurrent);
                        }
                    }
                });
            }
        }
    }

    private void initArtistContentViewModel(String receivedArtistUrlString) {
        artistViewModel.initArtistData(receivedArtistUrlString);
        artistViewModel.getArtistData().observe(this, this::setupUi);
    }

    private void setupUi(Artist currentArtist) {
        ArtistInfoUtils.setupArtistUi(currentArtist, artistCard, artistName, artistHomeTown,
                hometownLabel, artistLifespan, artistDivider, artistLocation, locationLabel, artistNationality,
                artistNationalityLabel, artistBio, artistBioLabel);
        // Get the name of the artist
        String artistNameString = currentArtist.getName();
        if (!Utils.isNullOrEmpty(artistNameString)) {
            collapsingToolbarLayout.setTitle(artistNameString);
        }
        ArtistInfoUtils.displayArtistImage(currentArtist, artistImage);
        ImageUtils.setupZoomyImage(this, artistImage);
    }

    // Scrape the Artsy website for additional information
    // TODO: Find out if we can get the Permalink here
    private void getBioFromReadMoreLink(String readMoreLink) {
        showsDetailViewModel.initBioFromWeb(readMoreLink);
        showsDetailViewModel.getBioFromWeb().observe(this, bio -> {
                    if (!Utils.isNullOrEmpty(bio)) {
                        artistBio.setText(bio);
                        artistBio.setVisibility(View.VISIBLE);
                        artistBioLabel.setVisibility(View.VISIBLE);
                    }
                }
        );
    }
}
