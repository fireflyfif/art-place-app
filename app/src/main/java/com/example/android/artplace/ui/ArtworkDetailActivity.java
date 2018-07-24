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

import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;

import com.example.android.artplace.R;
import com.example.android.artplace.model.remote.Artworks.ArtistsLink;
import com.example.android.artplace.model.remote.Artworks.Artwork;
import com.example.android.artplace.model.remote.Artworks.CmSize;
import com.example.android.artplace.model.remote.Artworks.InSize;
import com.example.android.artplace.model.remote.Artworks.MainImage;
import com.example.android.artplace.model.remote.Artworks.Dimensions;
import com.example.android.artplace.model.remote.ImageLinks;
import com.example.android.artplace.ui.ArtistActivity.ArtistDetailActivity;
import com.squareup.picasso.Picasso;

import java.text.Normalizer;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ArtworkDetailActivity extends AppCompatActivity {

    private static final String TAG = ArtworkDetailActivity.class.getSimpleName();
    private static final String ARTWORK_PARCEL_KEY = "artwork_key";
    private static final String ARTWORK_ID_KEY = "artwork_id";
    private static final String ARTWORK_TITLE_KEY = "artwork_title";

    @BindView(R.id.coordinator_artwork)
    CoordinatorLayout coordinatorLayout;
    @BindView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout collapsingToolbarLayout;
    @BindView(R.id.toolbar_detail)
    Toolbar toolbar;
    @BindView(R.id.artwork_title)
    TextView artworkName;
    @BindView(R.id.artwork_artist_button)
    Button artistNameLink;
    @BindView(R.id.artwork_medium)
    TextView artworkMedium;
    @BindView(R.id.artwork_category)
    TextView artworkCategory;
    @BindView(R.id.artwork_date)
    TextView artworkDate;
    @BindView(R.id.artwork_museum)
    TextView artworkMuseum;
    @BindView(R.id.artwork_image)
    ImageView artworkImage;
    @BindView(R.id.artwork_dimens_cm)
    TextView dimensCm;
    @BindView(R.id.artwork_dimens_in)
    TextView dimensIn;

    private Artwork mArtworkObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artwork_detail);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if (getIntent().getExtras() != null) {
            Bundle bundle = getIntent().getExtras();

            mArtworkObject = bundle.getParcelable(ARTWORK_PARCEL_KEY);

            if (mArtworkObject != null) {
                setupUi(mArtworkObject);
            }
        }
    }

    private void setupUi(Artwork currentArtwork) {

        String titleString = null;
        if (currentArtwork.getTitle() != null) {
            titleString = currentArtwork.getTitle();
            artworkName.setText(titleString);
            collapsingToolbarLayout.setTitle(titleString);
            Log.d(TAG, "Title of the artwork: " + titleString);


        } else {
            artworkName.setText("N/A");
        }

        if (currentArtwork.getMedium() != null) {
            String mediumString = currentArtwork.getMedium();
            artworkMedium.setText(mediumString);
        } else {
            artworkMedium.setText("N/A");
        }

        if (currentArtwork.getCategory() != null) {
            String categoryString = currentArtwork.getCategory();
            artworkCategory.setText(categoryString);
        } else {
            artworkCategory.setText("N/A");
        }

        if (currentArtwork.getDate() != null) {
            String dateString = currentArtwork.getDate();
            artworkDate.setText(dateString);
        } else {
            artworkDate.setText("N/A");
        }

        if (currentArtwork.getCollectingInstitution() != null) {
            String museumString = currentArtwork.getCollectingInstitution();
            artworkMuseum.setText(museumString);
        } else {
            artworkMuseum.setText("N/A");
        }

        if (currentArtwork.getDimensions() != null) {
            Dimensions dimensionObject = currentArtwork.getDimensions();

            if (dimensionObject.getCmSize() != null) {
                CmSize cmSizeObject = dimensionObject.getCmSize();
                String dimensInCmString = cmSizeObject.getText();
                dimensCm.setText(dimensInCmString);
            } else {
                dimensCm.setText("N/A");
            }

            if (dimensionObject.getInSize() != null) {
                InSize inSizeObject = dimensionObject.getInSize();
                String dimensInInchString = inSizeObject.getText();
                dimensIn.setText(dimensInInchString);
            } else {
                dimensIn.setText("N/A");
            }
        }

        List<String> imageVersionList = currentArtwork.getImageVersions();
        // Get the first entry from this list, which corresponds to "large"
        String versionString = imageVersionList.get(0);

        ImageLinks imageLinksObject = currentArtwork.getLinks();

        MainImage mainImageObject = imageLinksObject.getImage();
        // Get the link for the current artwork,
        // e.g.: "https://d32dm0rphc51dk.cloudfront.net/rqoQ0ln0TqFAf7GcVwBtTw/{image_version}.jpg"
        String artworkImgLinkString = mainImageObject.getHref();
        // Replace the {image_version} from the artworkImgLinkString with
        // the wanted version, e.g. "large"
        String newArtworkLinkString = artworkImgLinkString
                .replaceAll("\\{.*?\\}", versionString);

        Log.d(TAG, "New link to the image: " + newArtworkLinkString);

        // Set the image here
        // TODO: Handle error cases
        Picasso.get()
                .load(Uri.parse(newArtworkLinkString))
                .placeholder(R.drawable.movie_video_02)
                .error(R.drawable.movie_video_02)
                .into(artworkImage);

        if (imageLinksObject.getArtists() != null) {
            ArtistsLink artistsLinkObject = imageLinksObject.getArtists();
            String artistLinkString = artistsLinkObject.getHref(); // This link needs a token!!!
            Log.d(TAG, "Link to the artist: " + artistLinkString);

            String artworkId = currentArtwork.getId();
            Log.d(TAG, "Artwork id: " + artworkId);

            // The Slug contains the name of the artist as well as the name of the artwork
            // e.g. "gustav-klimt-der-kuss-the-kiss"
            String artworkSlug = currentArtwork.getSlug();
            // Remove all "-" from the slug
            String newSlugString = artworkSlug.replaceAll("-", " ").toLowerCase();
            Log.d(TAG, "New Slug string: " + newSlugString);

            // Clear the title of the artwork from any punctuations or characters that are not English letters
            String newTitleString = titleString
                    .toLowerCase()
                    .replaceAll("'", "")
                    .replaceAll("\\.", "")
                    .replaceAll(",", "")
                    .replaceAll(":", "")
                    .replaceAll("-", " ")
                    .replaceAll("[()]", "");

            // Normalize the letters
            // Tutorial here: https://www.drillio.com/en/2011/java-remove-accent-diacritic/
            String normalizedTitleString = Normalizer.normalize(newTitleString, Normalizer.Form.NFD);
            String removedDiacriticsFromTitle = normalizedTitleString.replaceAll("\\p{InCombiningDiacriticalMarks}+", "").trim();
            Log.d(TAG, "New title without diacritics: " + removedDiacriticsFromTitle);

            Log.d(TAG, "Does the slug contain the artwork title: " + newSlugString.contains(removedDiacriticsFromTitle)
                    + " \nNew artwork Title: " + removedDiacriticsFromTitle);

            String artistNameFromSlug = null;
            // Check if the slug contains the title of the artwork
            if (newSlugString.contains(removedDiacriticsFromTitle)) {

                artistNameFromSlug = newSlugString.replace(removedDiacriticsFromTitle, "");

                Log.d(TAG, "Only name of the artist: " + artistNameFromSlug);

                if (artistNameFromSlug.equals((""))) {
                    artistNameFromSlug = "N/A";
                }
                // Display the name of the Artist
                artistNameLink.setText(artistNameFromSlug);
            } else {
                // Display just "Artist"
                artistNameLink.setText(R.string.artist_name);

            }

            String finalArtistNameFromSlug = artistNameFromSlug;
            String finalTitleString = titleString;
            artistNameLink.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    // Check first if the artist name is not null or "N/A"
                    if ((finalArtistNameFromSlug == null) || (finalArtistNameFromSlug.equals("N/A"))) {
                        // Show a message to the user that there is no artist for the selected artwork
                        Snackbar.make(coordinatorLayout, "Sorry, No data for this artist.", Snackbar.LENGTH_LONG).show();
                        return;
                    }

                    Intent intent = new Intent(ArtworkDetailActivity.this, ArtistDetailActivity.class);
                    // TODO: Send the name of the artwork as extra
                    intent.putExtra(ARTWORK_TITLE_KEY, finalTitleString);
                    intent.putExtra(ARTWORK_ID_KEY, artworkId);
                    startActivity(intent);
                }
            });
        }
    }
}

