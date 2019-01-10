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

package com.example.android.artplace.ui.searchdetail;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.example.android.artplace.R;
import com.example.android.artplace.model.ImageLinks;
import com.example.android.artplace.model.Self;
import com.example.android.artplace.model.Thumbnail;
import com.example.android.artplace.model.artists.Artist;
import com.example.android.artplace.model.artworks.MainImage;
import com.example.android.artplace.model.search.LinksResult;
import com.example.android.artplace.model.search.Permalink;
import com.example.android.artplace.model.search.Result;
import com.example.android.artplace.model.search.ShowContent;
import com.example.android.artplace.ui.artistdetail.ArtistsDetailViewModel;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SearchDetailActivity extends AppCompatActivity {

    private static final String RESULT_PARCEL_KEY = "results_key";
    private static final String TAG = SearchDetailActivity.class.getSimpleName();
    private int mLightMutedColor = 0xFFAAAAAA;

    @BindView(R.id.content_title)
    TextView contentTitle;
    @BindView(R.id.content_type)
    TextView contentType;
    @BindView(R.id.content_image)
    ImageView contentImage;
    @BindView(R.id.content_image2)
    ImageView secondImage;
    @BindView(R.id.content_description)
    TextView contentDescription;
    @BindView(R.id.toolbar_detail)
    Toolbar toolbar;
    @BindView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout collapsingToolbarLayout;
    @BindView(R.id.background_title_layout)
    LinearLayout bgLayout;
    @BindView(R.id.card_view_content)
    CardView cardView;
    @BindView(R.id.content_detail_description)
    TextView detailDescription;
    @BindView(R.id.content_detail_press_release)
    TextView detailPressRelease;
    @BindView(R.id.read_more_button)
    Button readMoreButton;

    @BindView(R.id.artist_name)
    TextView artistName;
    @BindView(R.id.artist_home)
    TextView artistHomeTown;
//    @BindView(R.id.artist_image)
//    ImageView artistImage;
    @BindView(R.id.artist_lifespan)
    TextView artistLifespan;
    @BindView(R.id.artist_location)
    TextView artistLocation;
    @BindView(R.id.artist_nationality)
    TextView artistNationality;

    private Result mResults;
    private ShowsDetailViewModel mShowsViewModel;
    private ArtistsDetailViewModel mArtistViewModel;

    private String mDescription;
    private String mPressRelease;
    private String mStartDate;
    private String mEndDate;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_detail);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if (getIntent().getExtras() != null) {
            if (getIntent().hasExtra(RESULT_PARCEL_KEY)) {
                Bundle receivedBundle = getIntent().getExtras();
                mResults = receivedBundle.getParcelable(RESULT_PARCEL_KEY);

                if (mResults != null) {

                    if (toolbar != null) {
                        toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.colorAccent),
                                PorterDuff.Mode.SRC_ATOP);
                    }

                    String titleString = mResults.getTitle();
                    String typeString = mResults.getType();
                    String descriptionString = mResults.getDescription();
                    Log.d(TAG, "Title: " + titleString + "\nType: " + typeString + "\nDescription: " + descriptionString);

                    collapsingToolbarLayout.setTitle(titleString);
                    collapsingToolbarLayout.setCollapsedTitleTextColor(getResources().getColor(R.color.colorAccent));

                    contentTitle.setText(titleString);
                    contentType.setText(typeString);
                    contentDescription.setText(descriptionString);

                    if (mResults.getLinks() != null) {
                        LinksResult linksResult = mResults.getLinks();

                        if (linksResult.getThumbnail() != null) {
                            Thumbnail thumbnail = linksResult.getThumbnail();
                            String imageThumbnailString = thumbnail.getHref();

                            if (imageThumbnailString != null || imageThumbnailString.isEmpty()) {
                                // Set the backdrop image
                                Picasso.get()
                                        .load(imageThumbnailString)
                                        .placeholder(R.color.colorPrimary)
                                        .error(R.color.colorPrimary)
                                        .into(contentImage);

                                // Set the second image
                                Picasso.get()
                                        .load(imageThumbnailString)
                                        .placeholder(R.color.colorPrimary)
                                        .error(R.color.colorPrimary)
                                        .into(secondImage, new Callback() {
                                            @Override
                                            public void onSuccess() {
                                                // Get the image as a bitmap
                                                Bitmap bitmap = ((BitmapDrawable) secondImage.getDrawable()).getBitmap();
                                                secondImage.setImageBitmap(bitmap);

                                                // Get a color from the bitmap by using the Palette library
                                                Palette palette = Palette.from(bitmap).generate();
                                                int generatedLightColor = palette.getLightVibrantColor(mLightMutedColor);

                                                cardView.setCardBackgroundColor(generatedLightColor);
                                                //bgLayout.setBackgroundColor(generatedLightColor);
                                            }

                                            @Override
                                            public void onError(Exception e) {

                                            }
                                        });
                            } else {
                                // Set the backdrop image
                                Picasso.get()
                                        .load(R.color.colorPrimary)
                                        .placeholder(R.color.colorPrimary)
                                        .error(R.color.colorPrimary)
                                        .into(contentImage);

                                // Set the second image
                                Picasso.get()
                                        .load(R.color.colorPrimary)
                                        .placeholder(R.color.colorPrimary)
                                        .error(R.color.colorPrimary)
                                        .into(secondImage);
                            }


                            Self self = linksResult.getSelf();
                            String selfLinkString;
                            if (self != null) {
                                selfLinkString = self.getHref();
                                Log.d(TAG, "Self Link: " + selfLinkString);


                                if (typeString.equals("shows")) {
                                    // Init the View Model from the detail search content endpoint
                                    initShowsContentViewModel(selfLinkString);
                                }

                                if (typeString.equals("artist")) {
                                    initArtistContentViewModel(selfLinkString);
                                }
                            }

                            Permalink permalink = linksResult.getPermalink();
                            readMoreButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (permalink != null) {
                                        String readMoreLink = permalink.getHref();
                                        Log.d(TAG, "Perma Link: " + readMoreLink);
                                        Intent openUrlIntent = new Intent(Intent.ACTION_VIEW);
                                        openUrlIntent.setData(Uri.parse(readMoreLink));
                                        startActivity(openUrlIntent);
                                    }
                                }
                            });

                        }
                    }
                }
            }
        }
    }

    /**
     * Method for initialising the views from the detail search View Model
     *
     * @param selfLink is the link that should be passed as a link to be used as a new call
     */
    private void initShowsContentViewModel(String selfLink) {
        mShowsViewModel = ViewModelProviders.of(this).get(ShowsDetailViewModel.class);
        mShowsViewModel.initSearchLink(selfLink);

        mShowsViewModel.getResultSelfLink().observe(this, new Observer<ShowContent>() {

            @Override
            public void onChanged(@Nullable ShowContent showContent) {
                if (showContent != null) {
                    setupShowsContentUi(showContent);
                }
            }
        });
    }

    private void setupShowsContentUi(ShowContent showContent) {
        mPressRelease = showContent.getPressRelease();
        mDescription = showContent.getDescription();
        mStartDate = showContent.getStartAt();
        mEndDate = showContent.getEndAt();

        detailDescription.setText(mDescription);
        detailPressRelease.setText(mPressRelease);
    }

    private void initArtistContentViewModel(String receivedArtistUrlString) {

        mArtistViewModel = ViewModelProviders.of(this).get(ArtistsDetailViewModel.class);
        mArtistViewModel.initArtistLink(receivedArtistUrlString);

        mArtistViewModel.getArtistFromLink().observe(this, new Observer<List<Artist>>() {
            @Override
            public void onChanged(@Nullable List<Artist> artists) {
                if (artists != null) {

                    for (int i = 0; i < artists.size(); i++) {
                        Artist artistCurrent = artists.get(i);
                        setupUi(artistCurrent);
                    }
                }
            }
        });
    }

    private void setupUi(Artist currentArtist) {

        // Get the name of the artist
        if (currentArtist.getName() != null) {
            String artistNameString = currentArtist.getName();
            artistName.setText(artistNameString);
            Log.d(TAG, "Artist name:" + artistNameString);
        } else {
            artistName.setText(getString(R.string.not_applicable));
        }

        // Get the Home town of the artist
        if (currentArtist.getHometown() != null) {
            String artistHomeTownString = currentArtist.getHometown();
            artistHomeTown.setText(artistHomeTownString);
            Log.d(TAG, "Artist hometown:" + artistHomeTownString);
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
            Log.d(TAG, "Artist life span:" + lifespanConcatString);
        } else {
            artistLifespan.setText(getString(R.string.not_applicable));
        }

        // Get the location of the artist
        if (currentArtist.getLocation() != null) {
            String artistLocationString = currentArtist.getLocation();
            artistLocation.setText(artistLocationString);
            Log.d(TAG, "Artist location:" + artistLocationString);
        } else {
            artistLocation.setText(getString(R.string.not_applicable));
        }

        if (currentArtist.getNationality() != null) {
            String artistNationalityString = currentArtist.getNationality();
            artistNationality.setText(artistNationalityString);
            Log.d(TAG, "Artist nationality:" + artistNationalityString);
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
       /* Picasso.get()
                .load(Uri.parse(newArtistLinkString))
                .placeholder(R.color.colorPrimary)
                .error(R.color.colorPrimary)
                .into(artistImage);*/

    }
}
