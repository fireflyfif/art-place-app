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
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.artplace.ArtPlaceApp;
import com.example.android.artplace.R;
import com.example.android.artplace.callbacks.ResultFromDbCallback;
import com.example.android.artplace.database.entity.FavoriteArtworks;
import com.example.android.artplace.model.Artworks.ArtistsLink;
import com.example.android.artplace.model.Artworks.Artwork;
import com.example.android.artplace.model.Artworks.CmSize;
import com.example.android.artplace.model.Artworks.Dimensions;
import com.example.android.artplace.model.Artworks.InSize;
import com.example.android.artplace.model.Artworks.MainImage;
import com.example.android.artplace.model.ImageLinks;
import com.example.android.artplace.model.Thumbnail;
import com.example.android.artplace.repository.FavArtRepository;
import com.example.android.artplace.ui.artistDetailActivity.ArtistDetailActivity;
import com.example.android.artplace.utils.StringUtils;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.facebook.imagepipeline.request.Postprocessor;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.wasabeef.fresco.processors.BlurPostprocessor;

public class ArtworkDetailActivity extends AppCompatActivity {

    private static final String TAG = ArtworkDetailActivity.class.getSimpleName();
    private static final String ARTWORK_PARCEL_KEY = "artwork_key";
    private static final String ARTWORK_ID_KEY = "artwork_id";
    private static final String ARTWORK_TITLE_KEY = "artwork_title";
    private static final String IS_FAV_SAVED_STATE = "is_fav";
    private static final int FAV_TAG = 0;
    private static final int NON_FAV_TAG = 1;

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
    @BindView(R.id.fav_button)
    FloatingActionButton mFavButton;
    @BindView(R.id.adView)
    AdView adView;
    @BindView(R.id.blurry_image)
    SimpleDraweeView blurryImage;

    private Artwork mArtworkObject;
    private String mArtworkIdString;
    private String mTitleString;
    private String mMediumString;
    private String mCategoryString;
    private String mDateString;
    private String mMuseumString;
    private String mNewArtworkLinkString;
    private String mNewSquareArtworkLinkString;
    private String mArtistNameFromSlug;
    private String mArtworkThumbnailString;
    private String mDimensInCmString;
    private String mDimensInInchString;

    private boolean mIsFavorite;
    private Tracker mTracker;

    // Variables for Fresco Library
    private Postprocessor mPostprocessor;
    private ImageRequest mImageRequest;
    private PipelineDraweeController mController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize Fresco
        Fresco.initialize(this);
        setContentView(R.layout.activity_artwork_detail);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Obtain the shared Tracker instance.
        // source: https://developers.google.com/analytics/devguides/collection/android/v4/
        ArtPlaceApp application = (ArtPlaceApp) getApplication();
        mTracker = application.getDefaultTracker();

        // Initialize Ads
        // Sample AdMob app ID: ca-app-pub-3940256099942544~3347511713
        // TODO: Add my own ID
        MobileAds.initialize(this, getString(R.string.admob_id));

        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();
        adView.loadAd(adRequest);

        if (savedInstanceState != null) {
            mIsFavorite = savedInstanceState.getBoolean(IS_FAV_SAVED_STATE);
        }

        if (getIntent().getExtras() != null) {
            Bundle bundle = getIntent().getExtras();

            mArtworkObject = bundle.getParcelable(ARTWORK_PARCEL_KEY);

            if (mArtworkObject != null) {
                setupUi(mArtworkObject);
            }
        }

        // Check if the item exists in the db already or not!!!
        FavArtRepository.getInstance(getApplication()).executeGetItemById(mArtworkIdString, new ResultFromDbCallback() {
            @Override
            public void setResult(boolean isFav) {
                if (isFav) {
                    mIsFavorite = true;
                    mFavButton.setTag(FAV_TAG);
                    // Set the button to display it's already added
                    Log.d(TAG, "Item already exists in the db.");
                    mFavButton.setImageResource(R.drawable.ic_favorite_24dp);
                } else {
                    // Add to the db
                    mIsFavorite = false;
                    mFavButton.setTag(NON_FAV_TAG);
                    Log.d(TAG, "Insert a new item into the db");
                    mFavButton.setImageResource(R.drawable.ic_favorite_border_24dp);
                }
            }
        });

        clickFab();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(IS_FAV_SAVED_STATE, mIsFavorite);
    }

    @Override
    protected void onResume() {
        super.onResume();

        mTracker.setScreenName(TAG);
        // Send initial screen screen view hit.
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    private void setupUi(Artwork currentArtwork) {

        mArtworkIdString = currentArtwork.getId();

        if (currentArtwork.getTitle() != null) {
            mTitleString = currentArtwork.getTitle();
            artworkName.setText(mTitleString);
            collapsingToolbarLayout.setTitle(mTitleString);
            Log.d(TAG, "Title of the artwork: " + mTitleString);

        } else {
            artworkName.setText(R.string.not_applicable);
        }

        if (currentArtwork.getMedium() != null) {
            mMediumString = currentArtwork.getMedium();
            artworkMedium.setText(mMediumString);
        } else {
            artworkMedium.setText(R.string.not_applicable);
        }

        if (currentArtwork.getCategory() != null) {
            mCategoryString = currentArtwork.getCategory();
            artworkCategory.setText(mCategoryString);
        } else {
            artworkCategory.setText(R.string.not_applicable);
        }

        if (currentArtwork.getDate() != null) {
            mDateString = currentArtwork.getDate();
            artworkDate.setText(mDateString);
        } else {
            artworkDate.setText(R.string.not_applicable);
        }

        if (currentArtwork.getCollectingInstitution() != null) {
            mMuseumString = currentArtwork.getCollectingInstitution();
            artworkMuseum.setText(mMuseumString);
        } else {
            artworkMuseum.setText(R.string.not_applicable);
        }

        if (currentArtwork.getDimensions() != null) {
            Dimensions dimensionObject = currentArtwork.getDimensions();

            if (dimensionObject.getCmSize() != null) {
                CmSize cmSizeObject = dimensionObject.getCmSize();
                mDimensInCmString = cmSizeObject.getText();
                dimensCm.setText(mDimensInCmString);
            } else {
                dimensCm.setText(R.string.not_applicable);
            }

            if (dimensionObject.getInSize() != null) {
                InSize inSizeObject = dimensionObject.getInSize();
                mDimensInInchString = inSizeObject.getText();
                dimensIn.setText(mDimensInInchString);
            } else {
                dimensIn.setText(R.string.not_applicable);
            }
        }

        ImageLinks imageLinksObject = currentArtwork.getLinks();
        MainImage mainImageObject = imageLinksObject.getImage();

        if (currentArtwork.getImageVersions() != null) {

            List<String> imageVersionList = currentArtwork.getImageVersions();

            String versionLargeString;
            // Check if the list with image version contains "large"
            if (imageVersionList.contains("large")) {

                // Set the version to "large"
                versionLargeString = "large";
                Log.d(TAG, "Version of the image: " + versionLargeString);
            } else {

                // Set the first String from the version list
                versionLargeString = imageVersionList.get(0);
                Log.d(TAG, "Version of the image: " + versionLargeString);
            }

            // Get the sixth entry from this list, which corresponds to "square"
            String versionSquareString;
            if (imageVersionList.contains("square")) {

                versionSquareString = "square";
                Log.d(TAG, "Version of the square image: " + versionSquareString);
            } else {

                versionSquareString = imageVersionList.get(0);
                Log.d(TAG, "Version of the square image: " + versionSquareString);
            }


            // Get the link for the current artwork,
            // e.g.: "https://d32dm0rphc51dk.cloudfront.net/rqoQ0ln0TqFAf7GcVwBtTw/{image_version}.jpg"
            String artworkImgLinkString = mainImageObject.getHref();

            // Replace the {image_version} from the artworkImgLinkString with
            // the wanted version, e.g. "large"
            mNewArtworkLinkString = artworkImgLinkString
                    .replaceAll("\\{.*?\\}", versionLargeString);
            Log.d(TAG, "New link to the image: " + mNewArtworkLinkString);

            // Get the first entry from this list, which corresponds to "large"
            mNewSquareArtworkLinkString = artworkImgLinkString.replaceAll("\\{.*?\\}", versionSquareString);
            Log.d(TAG, "New link to the square image: " + mNewSquareArtworkLinkString);

            // Extract the string to thumbnail so that it is saved in favorites
            Thumbnail thumbnail = imageLinksObject.getThumbnail();
            mArtworkThumbnailString = thumbnail.getHref();

            // Initialize Blur Post Processor
            // Tutorial:https://android.jlelse.eu/android-image-blur-using-fresco-vs-picasso-ea095264abbf
            mPostprocessor = new BlurPostprocessor(this, 20);

            // Instantiate Image Request using Post Processor as parameter
            mImageRequest = ImageRequestBuilder.newBuilderWithSource(Uri.parse(mNewArtworkLinkString))
                    .setPostprocessor(mPostprocessor)
                    .build();

            // Instantiate Controller
            mController = (PipelineDraweeController) Fresco.newDraweeControllerBuilder()
                    .setImageRequest(mImageRequest)
                    .setOldController(blurryImage.getController())
                    .build();

            // Load the blurred image
            blurryImage.setController(mController);

        }


        // Set the square image with Picasso
        if (mNewSquareArtworkLinkString == null || mNewSquareArtworkLinkString.isEmpty()) {
            Picasso.get()
                    .load(R.drawable.placeholder)
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.placeholder)
                    .into(artworkImage);
        } else {
            Picasso.get()
                    .load(Uri.parse(mNewSquareArtworkLinkString))
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.placeholder)
                    .into(artworkImage);
        }

        if (imageLinksObject.getArtists() != null) {
            ArtistsLink artistsLinkObject = imageLinksObject.getArtists();
            String artistLinkString = artistsLinkObject.getHref(); // This link needs a token!!!
            Log.d(TAG, "Link to the artist: " + artistLinkString);

            String artworkId = currentArtwork.getId();
            Log.d(TAG, "Artwork id: " + artworkId);

            mArtistNameFromSlug = StringUtils.getArtistNameFromSlug(currentArtwork);
            Log.d(TAG, "Name of Artist after extraction: " + mArtistNameFromSlug);

            artistNameLink.setText(mArtistNameFromSlug);

            String finalTitleString = mTitleString;
            artistNameLink.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    // Check first if the artist name is not null or "N/A"
                    if ((mArtistNameFromSlug == null) || (mArtistNameFromSlug.equals("N/A"))) {
                        // Show a message to the user that there is no artist for the selected artwork
                        Snackbar.make(coordinatorLayout, R.string.snackbar_no_data_artist, Snackbar.LENGTH_LONG).show();
                        return;
                    }

                    Intent intent = new Intent(ArtworkDetailActivity.this, ArtistDetailActivity.class);
                    // Send the name of the artwork as extra
                    intent.putExtra(ARTWORK_TITLE_KEY, finalTitleString);
                    intent.putExtra(ARTWORK_ID_KEY, artworkId);
                    startActivity(intent);
                }
            });
        }
    }

    private void clickFab() {
        mFavButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setIconOnFab(v);
            }
        });
    }

    /**
     * Set the icons on the FAB according to the clicks
     * @param view the returned view of the button
     */
    private void setIconOnFab(View view) {
        int tagValue = (int) view.getTag();

        switch (tagValue) {
            case (FAV_TAG):
                // Delete from the db
                deleteItemFromFav();
                mFavButton.setTag(NON_FAV_TAG);
                mFavButton.setImageResource(R.drawable.ic_favorite_border_24dp);
                break;
            case (NON_FAV_TAG):
                // Add an item to the db
                addArtworkToFavorites();
                mFavButton.setTag(FAV_TAG);
                mFavButton.setImageResource(R.drawable.ic_favorite_24dp);
                break;
            default:
                mFavButton.setTag(NON_FAV_TAG);
                mFavButton.setImageResource(R.drawable.ic_favorite_border_24dp);
                break;
        }
    }

    /**
     * Method for deleting an item from the database
     */
    private void deleteItemFromFav() {
        FavArtRepository.getInstance(getApplication()).deleteItem(mArtworkIdString);
        Snackbar.make(coordinatorLayout, R.string.snackbar_item_removed, Snackbar.LENGTH_SHORT).show();
        Log.d(TAG, "Delete the item from the db");
    }

    /**
     * Method for adding an item to the database
     */
    private void addArtworkToFavorites() {

        String artworkId = mArtworkIdString;
        String artworkTitle = mTitleString;
        String artworkSlug = mArtistNameFromSlug;
        String artworkCategory = mCategoryString;
        String artworkMedium = mMediumString;
        String artworkDate = mDateString;
        String artworkMuseum = mMuseumString;
        String artworkThumbnail = mArtworkThumbnailString;
        String artworkImage = mNewArtworkLinkString;
        String artworkDimensInch = mDimensInInchString;
        String artworkDimensCm = mDimensInCmString;

        FavoriteArtworks favArtwork = new FavoriteArtworks(artworkId, artworkTitle, artworkSlug,
                artworkCategory, artworkMedium, artworkDate, artworkMuseum, artworkThumbnail, artworkImage, artworkDimensInch, artworkDimensCm);

        FavArtRepository.getInstance(getApplication()).insertItem(favArtwork);
        Snackbar.make(coordinatorLayout, R.string.snackbar_item_added, Snackbar.LENGTH_SHORT).show();
        Log.d(TAG, "Insert a new item into the db");
    }
}

