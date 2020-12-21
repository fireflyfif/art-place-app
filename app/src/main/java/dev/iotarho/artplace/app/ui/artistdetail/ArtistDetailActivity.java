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

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.Postprocessor;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.card.MaterialCardView;
import com.ms.square.android.expandabletextview.ExpandableTextView;

import java.util.List;
import java.util.Timer;

import butterknife.BindView;
import butterknife.ButterKnife;
import dev.iotarho.artplace.app.R;
import dev.iotarho.artplace.app.callbacks.OnArtworkClickListener;
import dev.iotarho.artplace.app.callbacks.OnRefreshListener;
import dev.iotarho.artplace.app.model.ArtworksLink;
import dev.iotarho.artplace.app.model.ImageLinks;
import dev.iotarho.artplace.app.model.artists.Artist;
import dev.iotarho.artplace.app.model.artworks.Artwork;
import dev.iotarho.artplace.app.model.artworks.MainImage;
import dev.iotarho.artplace.app.ui.artworkdetail.ArtworkDetailActivity;
import dev.iotarho.artplace.app.ui.artworkdetail.adapter.ArtworksByArtistAdapter;
import dev.iotarho.artplace.app.ui.searchdetail.ShowDetailViewModelFactory;
import dev.iotarho.artplace.app.ui.searchdetail.ShowsDetailViewModel;
import dev.iotarho.artplace.app.utils.ArtistInfoUtils;
import dev.iotarho.artplace.app.utils.ImageUtils;
import dev.iotarho.artplace.app.utils.Injection;
import dev.iotarho.artplace.app.utils.Utils;
import jp.wasabeef.fresco.processors.BlurPostprocessor;

public class ArtistDetailActivity extends AppCompatActivity implements OnRefreshListener, OnArtworkClickListener {

    private static final String TAG = ArtistDetailActivity.class.getSimpleName();

    public static final String ARTIST_URL_KEY = "artist_url";
    private static final String ARTIST_PARCEL_KEY = "artist_key";
    public static final String ARTIST_ARTWORK_URL_KEY = "artist_and_artwork_url";
    public static final String ARTIST_EXTRA_KEY = "artist_extra";
    private static final String ARTWORK_PARCEL_KEY = "artwork_key";

    @BindView(R.id.coordinator_artist)
    CoordinatorLayout coordinatorLayout;
    @BindView(R.id.toolbar_artist)
    Toolbar toolbar;
    @BindView(R.id.appbar_artist)
    AppBarLayout appBarLayout;
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
    @BindView(R.id.blurry_image)
    SimpleDraweeView blurryImage;
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
    @BindView(R.id.artworks_by_artist_rv)
    RecyclerView artworksByArtistRv;

    private String artistBiography;
    private ArtistsDetailViewModel artistViewModel;
    private ShowsDetailViewModel showsDetailViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist_detail);
        ButterKnife.bind(this);

        // Set the Up Button Navigation to another color
        // source: https://stackoverflow.com/a/26837072/8132331
        PorterDuffColorFilter colorFilter = new PorterDuffColorFilter(getResources().getColor(R.color.color_primary),
                PorterDuff.Mode.SRC_ATOP);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        if ((toolbar.getNavigationIcon()) != null) {
            toolbar.getNavigationIcon().setColorFilter(colorFilter);
        }

        ArtistDetailViewModelFactory artistDetailViewModelFactory = Injection.provideArtistDetailViewModel();
        artistViewModel = new ViewModelProvider(getViewModelStore(), artistDetailViewModelFactory).get(ArtistsDetailViewModel.class);
        ShowDetailViewModelFactory showDetailViewModelFactory = Injection.provideShowDetailViewModel();
        showsDetailViewModel = new ViewModelProvider(getViewModelStore(), showDetailViewModelFactory).get(ShowsDetailViewModel.class);

        // Get the ID from the clicked artwork from the received Intent
        if (getIntent().getExtras() != null) {
            if (getIntent().hasExtra(ARTIST_EXTRA_KEY)) {
                Artist artistFromIntent = getIntent().getParcelableExtra(ARTIST_EXTRA_KEY);
                setupUi(artistFromIntent);
            }

            if (getIntent().hasExtra(ARTIST_PARCEL_KEY)) {
                Artist artist = getIntent().getParcelableExtra(ARTIST_PARCEL_KEY);
                setupUi(artist);
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
            Utils.setCollapsingToolbar(artistNameString, appBarLayout, collapsingToolbarLayout, getResources().getColor(R.color.color_primary));
        }
        ArtistInfoUtils.displayArtistImage(currentArtist, artistImage);
        ImageUtils.setupZoomyImage(this, artistImage);

        ImageLinks imageLinks = currentArtist.getLinks();
        MainImage mainImage = imageLinks.getImage();
        List<String> imageVersionList = currentArtist.getImageVersions();
        String largeArtworkLink = ImageUtils.getLargeImageUrl(imageVersionList, mainImage);

        // Initialize Blur Post Processor
        Postprocessor postProcessor = new BlurPostprocessor(this, 20);
        ImageUtils.makeImageBlurry(postProcessor, blurryImage, largeArtworkLink);

        ArtworksLink artworksLink = imageLinks.getArtworksLink();
        String href = artworksLink.getHref();
        Log.d(TAG, "temp, href of artworks: " + href);
        initArtworksByArtistsViewModel(href);
    }

    private void initArtworksByArtistsViewModel(String artworksLink) {
        artistViewModel.initArtworksByArtistData(artworksLink);
        artistViewModel.getArtworksByArtistsData().observe(this, this::setupArtworksByArtist);
    }

    private void setupArtworksByArtist(List<Artwork> artworksList) {
        ArtworksByArtistAdapter artworksByArtist = new ArtworksByArtistAdapter(artworksList, this, this);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        artworksByArtistRv.setLayoutManager(gridLayoutManager);
        artworksByArtistRv.setAdapter(artworksByArtist);
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

    /*private void setUIColorsFromImage() {
        // Get the image as a bitmap
        Bitmap bitmap = ((BitmapDrawable) artistImage.getDrawable()).getBitmap();
        // Get a color from the bitmap by using the Palette library
        Palette palette = Palette.from(bitmap).generate();
        mGeneratedLightColor = palette.getLightVibrantColor(mLightMutedColor);
//        cardView.setCardBackgroundColor(mGeneratedLightColor);
        // set the color of the back button
        colorFilter = new PorterDuffColorFilter(mGeneratedLightColor, PorterDuff.Mode.SRC_ATOP);
        if ((toolbar.getNavigationIcon()) != null) {
            toolbar.getNavigationIcon().setColorFilter(colorFilter);
        }
    }*/

    @Override
    public void onRefreshConnection() {
        // TODO: implement how to behave on refresh
    }

    @Override
    public void onArtworkClick(Artwork artwork, int position) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(ARTWORK_PARCEL_KEY, artwork);

        Intent intent = new Intent(this, ArtworkDetailActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }
}
