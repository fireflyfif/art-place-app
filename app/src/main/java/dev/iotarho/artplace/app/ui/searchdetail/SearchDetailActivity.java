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

package dev.iotarho.artplace.app.ui.searchdetail;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.ViewModelProvider;
import androidx.palette.graphics.Palette;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import dev.iotarho.artplace.app.R;
import dev.iotarho.artplace.app.model.Self;
import dev.iotarho.artplace.app.model.Thumbnail;
import dev.iotarho.artplace.app.model.artists.Artist;
import dev.iotarho.artplace.app.model.search.LinksResult;
import dev.iotarho.artplace.app.model.search.Permalink;
import dev.iotarho.artplace.app.model.search.Result;
import dev.iotarho.artplace.app.model.search.ShowContent;
import dev.iotarho.artplace.app.ui.artistdetail.ArtistsDetailViewModel;

public class SearchDetailActivity extends AppCompatActivity {

    private static final String RESULT_PARCEL_KEY = "results_key";
    private static final String TAG = SearchDetailActivity.class.getSimpleName();
    private int mLightMutedColor = 0xFFAAAAAA;

    // Search Detail main Views
    @BindView(R.id.content_title)
    TextView contentTitle;
    @BindView(R.id.content_type)
    Button contentType;
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
    @BindView(R.id.artwork_title_layout)
    LinearLayout bgLayout;
    @BindView(R.id.card_view_content)
    CardView cardView;
    @BindView(R.id.read_more_button)
    Button readMoreButton;

    // Show Card Views
    @BindView(R.id.show_cardview)
    CardView showCardView;
    @BindView(R.id.show_bg)
    View showBackground;
    @BindView(R.id.show_start_date)
    TextView showStartDate;
    @BindView(R.id.show_start_label)
    TextView showStartDateLabel;
    @BindView(R.id.show_end_date)
    TextView showEndDate;
    @BindView(R.id.show_end_label)
    TextView showEndDateLabel;
    @BindView(R.id.show_description)
    TextView showDescription;
    @BindView(R.id.show_description_label)
    TextView showDescriptionLabel;
    @BindView(R.id.show_press_release)
    TextView showPress;
    @BindView(R.id.show_press_label)
    TextView showPressLabel;

    // Artist Card Views
    @BindView(R.id.artist_cardview)
    CardView artistCardView;
    @BindView(R.id.artist_name)
    TextView artistName;
    @BindView(R.id.artist_home)
    TextView artistHomeTown;
    @BindView(R.id.hometown_label)
    TextView hometownLabel;
    @BindView(R.id.artist_lifespan)
    TextView artistLifespan;
    @BindView(R.id.artist_location)
    TextView artistLocation;
    @BindView(R.id.artist_location_label)
    TextView locationLabel;
    @BindView(R.id.artist_nationality)
    TextView artistNationality;
    @BindView(R.id.artist_nationality_label)
    TextView artistNationalityLabel;
    @BindView(R.id.artist_bio)
    TextView artistBio;
    @BindView(R.id.artist_bio_label)
    TextView artistBioLabel;

    private ShowsDetailViewModel mShowsViewModel;
    private ArtistsDetailViewModel mArtistViewModel;
    private int mGeneratedLightColor;


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
                Result mResults = receivedBundle.getParcelable(RESULT_PARCEL_KEY);

                if (mResults != null) {

                    if (toolbar != null) {
                        toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.color_primary),
                                PorterDuff.Mode.SRC_ATOP);
                    }

                    setupSearchUi(mResults);
                }
            }
        }
    }

    private void setupSearchUi(Result results) {
        String titleString = results.getTitle();
        String typeString = results.getType();
        String descriptionString = null;
        if (results.getDescription() != null) {
            descriptionString = results.getDescription();
            contentDescription.setText(descriptionString);

            if (results.getDescription().isEmpty()) {
                contentDescription.setVisibility(View.GONE);
            }
        }

        Log.d(TAG, "Title: " + titleString + "\nType: " + typeString + "\nDescription: " + descriptionString);

        collapsingToolbarLayout.setTitle(titleString);
        collapsingToolbarLayout.setCollapsedTitleTextColor(getResources().getColor(R.color.color_primary));

        contentTitle.setText(titleString);
        contentType.setText(typeString);

        if (results.getLinks() != null) {
            LinksResult linksResult = results.getLinks();

            if (linksResult.getThumbnail() != null) {
                Thumbnail thumbnail = linksResult.getThumbnail();
                String imageThumbnailString = thumbnail.getHref();

                if (imageThumbnailString != null || imageThumbnailString.isEmpty()) {
                    // Set the backdrop image
                    Picasso.get()
                            .load(imageThumbnailString)
                            .placeholder(R.color.color_primary)
                            .error(R.color.color_error)
                            .into(contentImage);

                    // Set the second image
                    Picasso.get()
                            .load(imageThumbnailString)
                            .placeholder(R.color.color_primary)
                            .error(R.color.color_error)
                            .into(secondImage, new Callback() {
                                @Override
                                public void onSuccess() {
                                    // Get the image as a bitmap
                                    Bitmap bitmap = ((BitmapDrawable) secondImage.getDrawable()).getBitmap();
                                    secondImage.setImageBitmap(bitmap);

                                    // Get a color from the bitmap by using the Palette library
                                    Palette palette = Palette.from(bitmap).generate();
                                    mGeneratedLightColor = palette.getLightVibrantColor(mLightMutedColor);

                                    cardView.setCardBackgroundColor(mGeneratedLightColor);
                                }

                                @Override
                                public void onError(Exception e) {

                                }
                            });
                } else {
                    // Set the backdrop image
                    Picasso.get()
                            .load(R.color.color_primary)
                            .placeholder(R.color.color_primary)
                            .error(R.color.color_error)
                            .into(contentImage);

                    // Set the second image
                    Picasso.get()
                            .load(R.color.color_primary)
                            .placeholder(R.color.color_primary)
                            .error(R.color.color_error)
                            .into(secondImage);
                }


                Self self = linksResult.getSelf();
                String selfLinkString;
                if (self != null) {
                    selfLinkString = self.getHref();
                    Log.d(TAG, "Self Link: " + selfLinkString);

                    artistCardView.setVisibility(View.GONE);
                    showCardView.setVisibility(View.GONE);

                    if (typeString.equals("show")) {
                        showCardView.setVisibility(View.VISIBLE);
                        // Init the View Model from the detail search content endpoint
                        initShowsContentViewModel(selfLinkString);
                    }

                    if (typeString.equals("artist")) {
                        artistCardView.setVisibility(View.VISIBLE);
                        initArtistContentViewModel(selfLinkString);
                    }
                }

                Permalink permalink = linksResult.getPermalink();
                readMoreButton.setOnClickListener(v -> {
                    if (permalink != null) {
                        String readMoreLink = permalink.getHref();
                        Log.d(TAG, "Perma Link: " + readMoreLink);
                        Intent openUrlIntent = new Intent(Intent.ACTION_VIEW);
                        openUrlIntent.setData(Uri.parse(readMoreLink));
                        startActivity(openUrlIntent);
                    }
                });
            }
        }
    }

    /**
     * Method for initialising the views from the show search View Model
     *
     * @param selfLink is the link that should be passed as a link to be used as a new call
     */
    private void initShowsContentViewModel(String selfLink) {
        mShowsViewModel = new ViewModelProvider(this).get(ShowsDetailViewModel.class);
        mShowsViewModel.initSearchLink(selfLink);

        mShowsViewModel.getResultSelfLink().observe(this, showContent -> {
            if (showContent != null) {
                setupShowsContentUi(showContent);
            }
        });
    }

    private void setupShowsContentUi(ShowContent showContent) {

        showBackground.setBackgroundColor(mGeneratedLightColor);

        if (showContent.getPressRelease() != null) {
            String pressRelease = showContent.getPressRelease();
            showPress.setText(pressRelease);

            if (showContent.getPressRelease().isEmpty()) {
                showPress.setVisibility(View.GONE);
                showPressLabel.setVisibility(View.GONE);
            }
        }

        if (showContent.getDescription() != null) {
            String mDescription = showContent.getDescription();
            showDescription.setText(mDescription);

            if (showContent.getDescription().isEmpty()) {
                showDescription.setVisibility(View.GONE);
                showDescriptionLabel.setVisibility(View.GONE);
            }
        }

        if (showContent.getStartAt() != null) {
            String startDate = showContent.getStartAt();
            showStartDate.setText(startDate);

            if (showContent.getStartAt().isEmpty()) {
                showStartDate.setVisibility(View.GONE);
                showStartDateLabel.setVisibility(View.GONE);
            }
        }

        if (showContent.getEndAt() != null) {
            String endDate = showContent.getEndAt();
            showEndDate.setText(endDate);

            if (showContent.getEndAt().isEmpty()) {
                showEndDate.setVisibility(View.GONE);
                showEndDateLabel.setVisibility(View.GONE);
            }
        }
    }

    private void initArtistContentViewModel(String receivedArtistUrlString) {

        mArtistViewModel = new ViewModelProvider(this).get(ArtistsDetailViewModel.class);
        mArtistViewModel.initArtistData(receivedArtistUrlString);

        mArtistViewModel.getArtistData().observe(this, artists -> { // onChanged is never called?
            if (artists != null) {
                setupArtistUi(artists);
            }
        });
    }

    private void setupArtistUi(Artist currentArtist) {

        // Get the name of the artist
        if (currentArtist.getName() != null) {
            String artistNameString = currentArtist.getName();
            artistName.setText(artistNameString);
            artistName.setBackgroundColor(mGeneratedLightColor);
            Log.d(TAG, "Artist name:" + artistNameString);
        } else {
            artistName.setText(getString(R.string.not_applicable));
        }

        // Get the Home town of the artist
        if (currentArtist.getHometown() != null) {
            String artistHomeTownString = currentArtist.getHometown();
            artistHomeTown.setText(artistHomeTownString);
            Log.d(TAG, "Artist hometown:" + artistHomeTownString);

            if (currentArtist.getHometown().isEmpty()) {
                artistHomeTown.setVisibility(View.INVISIBLE);
                hometownLabel.setVisibility(View.INVISIBLE);
            }
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

            if (currentArtist.getLocation().isEmpty()) {
                artistLocation.setVisibility(View.INVISIBLE);
                locationLabel.setVisibility(View.INVISIBLE);
            }
        } else {
            artistLocation.setText(getString(R.string.not_applicable));
        }

        if (currentArtist.getNationality() != null) {
            String artistNationalityString = currentArtist.getNationality();
            artistNationality.setText(artistNationalityString);
            Log.d(TAG, "Artist nationality:" + artistNationalityString);

            if (currentArtist.getNationality().isEmpty()) {
                artistNationality.setVisibility(View.INVISIBLE);
                artistNationalityLabel.setVisibility(View.INVISIBLE);
            }
        } else {
            artistNationality.setText(getString(R.string.not_applicable));
        }

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
