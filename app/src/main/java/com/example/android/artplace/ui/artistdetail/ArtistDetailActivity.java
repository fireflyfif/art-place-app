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

package com.example.android.artplace.ui.artistdetail;

import android.arch.lifecycle.ViewModelProviders;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.artplace.ArtPlaceApp;
import com.example.android.artplace.R;
import com.example.android.artplace.model.artists.Artist;
import com.example.android.artplace.model.artworks.MainImage;
import com.example.android.artplace.model.ImageLinks;
import com.example.android.artplace.utils.TokenManager;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ArtistDetailActivity extends AppCompatActivity {

    private static final String TAG = ArtistDetailActivity.class.getSimpleName();

    private static final String ARTWORK_TITLE_KEY = "artwork_title";
    private static final String ARTIST_URL_KEY = "artist_url";

    @BindView(R.id.coordinator_artist)
    CoordinatorLayout coordinatorLayout;
    @BindView(R.id.toolbar_artist)
    Toolbar toolbar;
    @BindView(R.id.collapsing_toolbar_artist)
    CollapsingToolbarLayout collapsingToolbarLayout;
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
    TextView artistBio;
    @BindView(R.id.artist_bio_label)
    TextView artistBioLabel;

    private ArtistsDetailViewModel mArtistViewModel;
    private Tracker mTracker;
    private TokenManager mTokenManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist_detail);

        ButterKnife.bind(this);

        // Obtain the shared Tracker instance.
        // source: https://developers.google.com/analytics/devguides/collection/android/v4/
        ArtPlaceApp application = (ArtPlaceApp) getApplication();
        mTracker = application.getDefaultTracker();

        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        //mTokenManager = TokenManager.getInstance(getSharedPreferences("prefs", MODE_PRIVATE));

        // TODO: Save the token into SharedPreferences
        /*if (mTokenManager.getNewToken().getToken() != null) {
            // TODO: get the new token here
            String newToken = mTokenManager.getNewToken().getToken();
            Log.d(TAG, "Get the new token here: " + newToken);
        }*/

        // Get the ID from the clicked artwork from the received Intent
        if (getIntent().getExtras() != null) {
            if (getIntent().hasExtra(ARTIST_URL_KEY)) {

                String receivedArtworkTitle = getIntent().getStringExtra(ARTWORK_TITLE_KEY);
                String receivedArtistUrlString = getIntent().getStringExtra(ARTIST_URL_KEY);

                Log.d(TAG, "Received artist url from the intent: " + receivedArtistUrlString);

                collapsingToolbarLayout.setTitle(receivedArtworkTitle);

                // Initialize the ViewModel
                mArtistViewModel = ViewModelProviders.of(this).get(ArtistsDetailViewModel.class);
                mArtistViewModel.initArtistDataFromArtwork(receivedArtistUrlString);

                mArtistViewModel.getArtistDataFromArtwork().observe(this, artists -> {
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

    @Override
    protected void onResume() {
        super.onResume();

        mTracker.setScreenName(TAG);
        // Send initial screen screen view hit.
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    private void setupUi(Artist currentArtist) {

        // Get the name of the artist
        if (currentArtist.getName() != null) {
            String artistNameString = currentArtist.getName();
            artistName.setText(artistNameString);

            //collapsingToolbarLayout.setTitle(artistNameString);
        } else {
            artistName.setText(getString(R.string.not_applicable));
        }

        // Get the Home town of the artist
        if (currentArtist.getHometown() != null) {
            String artistHomeTownString = currentArtist.getHometown();
            artistHomeTown.setText(artistHomeTownString);
        } else {
            artistHomeTown.setText(getString(R.string.not_applicable));
        }

        // Get the date of the birth and dead of the artist
        String artistBirthString;
        String artistDeathString;
        if (currentArtist.getBirthday() != null || currentArtist.getDeathday() != null) {
            artistBirthString = currentArtist.getBirthday();
            artistDeathString = currentArtist.getDeathday();

            String lifespanConcatString = artistBirthString + " - " + artistDeathString;
            artistLifespan.setText(lifespanConcatString);
        } else {
            artistLifespan.setText(getString(R.string.not_applicable));
        }

        // Get the location of the artist
        if (currentArtist.getLocation() != null) {
            String artistLocationString = currentArtist.getLocation();
            artistLocation.setText(artistLocationString);
        } else {
            artistLocation.setText(getString(R.string.not_applicable));
        }

        if (currentArtist.getNationality() != null) {
            String artistNationalityString = currentArtist.getNationality();
            artistNationality.setText(artistNationalityString);
        } else {
            artistNationality.setText(getString(R.string.not_applicable));
        }

        // Get the list of image versions first
        List<String> imageVersionList = currentArtist.getImageVersions();
        // Get the first entry from this list, which corresponds to "large"
        String versionString = imageVersionList.get(0);

        ImageLinks imageLinksObject = currentArtist.getLinks();
        MainImage mainImageObject = imageLinksObject.getImage();
        // Get the link for the current artist,
        // e.g.: "https://d32dm0rphc51dk.cloudfront.net/rqoQ0ln0TqFAf7GcVwBtTw/{image_version}.jpg"
        String artistImgLinkString = mainImageObject.getHref();
        // Replace the {image_version} from the artworkImgLinkString with
        // the wanted version, e.g. "large"
        String newArtistLinkString = artistImgLinkString
                .replaceAll("\\{.*?\\}", versionString);

        // Handle no image cases with placeholders
        Picasso.get()
                .load(Uri.parse(newArtistLinkString))
                .placeholder(R.color.colorPrimary)
                .error(R.color.colorPrimary)
                .into(artistImage);

        if (currentArtist.getBiography() != null) {
            String artistBioString = currentArtist.getBiography();
            artistBio.setText(artistBioString);

            if (currentArtist.getBiography().isEmpty()) {
                artistBio.setVisibility(View.GONE);
                artistBioLabel.setVisibility(View.GONE);
            }
        } else {
            artistBio.setVisibility(View.GONE);
            artistBioLabel.setVisibility(View.GONE);
        }

    }
}
