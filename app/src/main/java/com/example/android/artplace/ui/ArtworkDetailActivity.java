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
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.artplace.R;
import com.example.android.artplace.model.Artworks.ArtistsLink;
import com.example.android.artplace.model.Artworks.Artwork;
import com.example.android.artplace.model.Artworks.MainImage;
import com.example.android.artplace.model.Artworks.Dimensions;
import com.example.android.artplace.model.ImageLinks;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ArtworkDetailActivity extends AppCompatActivity {

    private static final String TAG = ArtworkDetailActivity.class.getSimpleName();
    private static final String ARTWORK_PARCEL_KEY = "artwork_key";

    @BindView(R.id.artwork_title)
    TextView artworkName;
    @BindView(R.id.artwork_artist)
    TextView artistNameLink;
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

        if (getIntent().getExtras() != null) {
            Bundle bundle = getIntent().getExtras();

            mArtworkObject = bundle.getParcelable(ARTWORK_PARCEL_KEY);

            if (mArtworkObject != null) {
                String titleString = mArtworkObject.getTitle();
                artworkName.setText(titleString);

                String mediumString = mArtworkObject.getMedium();
                artworkMedium.setText(mediumString);

                String categoryString = mArtworkObject.getCategory();
                artworkCategory.setText(categoryString);

                String dateString = mArtworkObject.getDate();
                artworkDate.setText(dateString);

                String museumString = mArtworkObject.getCollectingInstitution();
                artworkMuseum.setText(museumString);

                Dimensions dimensionObject = mArtworkObject.getDimensions();

//                CmSize cmSizeObject = dimensionObject.getCmSize(); // CmSize is null??
//                String dimensInCmString = cmSizeObject.getText();
//                dimensCm.setText(dimensInCmString);

//                InSize inSizeObject = dimensionObject.getInSize(); // InSize is null??
//                String dimensInInchString = inSizeObject.getText();
//                dimensIn.setText(dimensInInchString);
            }

            List<String> imageVersionList = mArtworkObject.getImageVersions();
            // Get the first entry from this list, which corresponds to "large"
            String versionString = imageVersionList.get(0);

            ImageLinks imageLinksObject = mArtworkObject.getLinks();

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
            Picasso.get()
                    .load(Uri.parse(newArtworkLinkString))
                    .into(artworkImage);

            ArtistsLink artistsLinkObject = imageLinksObject.getArtists();
            String artistLinkString = artistsLinkObject.getHref(); // This link needs a token!!!
            Log.d(TAG, "Link to the artist: " + artistLinkString);

            artistNameLink.setText(artistLinkString);
            artistNameLink.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(ArtworkDetailActivity.this, ArtistDetailActivity.class);
                    intent.putExtra("artist_link", artistLinkString);
                    startActivity(intent);
                }
            });

        }
    }
}

