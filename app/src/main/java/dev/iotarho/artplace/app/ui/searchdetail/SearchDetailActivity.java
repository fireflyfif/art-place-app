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
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ablanco.zoomy.Zoomy;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.card.MaterialCardView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Objects;

import butterknife.BindDimen;
import butterknife.BindView;
import butterknife.ButterKnife;
import dev.iotarho.artplace.app.R;
import dev.iotarho.artplace.app.model.ArtworksLink;
import dev.iotarho.artplace.app.model.ImageLinks;
import dev.iotarho.artplace.app.model.Self;
import dev.iotarho.artplace.app.model.Thumbnail;
import dev.iotarho.artplace.app.model.artists.Artist;
import dev.iotarho.artplace.app.model.artworks.ArtistsLink;
import dev.iotarho.artplace.app.model.artworks.Artwork;
import dev.iotarho.artplace.app.model.artworks.CmSize;
import dev.iotarho.artplace.app.model.artworks.Dimensions;
import dev.iotarho.artplace.app.model.artworks.InSize;
import dev.iotarho.artplace.app.model.artworks.MainImage;
import dev.iotarho.artplace.app.model.genes.GeneContent;
import dev.iotarho.artplace.app.model.search.LinksResult;
import dev.iotarho.artplace.app.model.search.Permalink;
import dev.iotarho.artplace.app.model.search.Result;
import dev.iotarho.artplace.app.model.search.ShowContent;
import dev.iotarho.artplace.app.ui.artistdetail.ArtistListAdapter;
import dev.iotarho.artplace.app.ui.artistdetail.ArtistsDetailViewModel;
import dev.iotarho.artplace.app.ui.artworkdetail.adapter.ArtworksByArtistAdapter;
import dev.iotarho.artplace.app.ui.artworks.ArtworksViewModel;
import dev.iotarho.artplace.app.utils.StringUtils;
import dev.iotarho.artplace.app.utils.Utils;
import io.noties.markwon.Markwon;

public class SearchDetailActivity extends AppCompatActivity {

    private static final String RESULT_PARCEL_KEY = "results_key";
    private static final String TAG = SearchDetailActivity.class.getSimpleName();
    private static final String SHOW = "show";
    private static final String ARTIST = "artist";
    private static final String ARTWORK = "artwork";
    private static final String GENE = "gene";
    private int mLightMutedColor = 0xFFAAAAAA;

    // Search Detail main Views
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
    @BindView(R.id.artwork_title_layout)
    LinearLayout bgLayout;
    @BindView(R.id.card_view_content)
    CardView cardView;
    @BindView(R.id.read_more_button)
    Button readMoreButton;

    // Show Card Views
    @BindView(R.id.show_gene_name)
    TextView showGeneName;
    @BindView(R.id.show_cardview)
    CardView showCardView;
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
    MaterialCardView artistCardView;
    @BindView(R.id.artist_name)
    TextView artistName;
    @BindView(R.id.artist_home)
    TextView artistHomeTown;
    @BindView(R.id.divider_2)
    View artistDivider;
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

    // Artwork Card View
    @BindView(R.id.artwork_cardview)
    MaterialCardView artworkCardView;
    @BindView(R.id.artwork_title)
    TextView artworkNameTextView;
    @BindView(R.id.artwork_artist_button)
    TextView artistNameButton;
    @BindView(R.id.artwork_medium)
    TextView artworkMedium;
    @BindView(R.id.artwork_category)
    TextView artworkCategory;
    @BindView(R.id.artwork_date)
    TextView artworkDate;
    @BindView(R.id.artwork_museum)
    TextView artworkMuseum;
    @BindView(R.id.museum_label)
    TextView artworkMuseumLabel;
    @BindView(R.id.artwork_dimens_cm)
    TextView dimensCm;
    @BindView(R.id.artwork_dimens_in)
    TextView dimensIn;
    @BindView(R.id.artwork_info_markdown)
    TextView artworkInfoMarkdown;
    @BindView(R.id.info_label)
    TextView artworkInfoLabel;

    @BindDimen(R.dimen.margin_42dp)
    int bottomMargin;

    @BindView(R.id.artworks_by_artist_rv)
    RecyclerView artworksByArtistRv;

    private ShowsDetailViewModel showsDetailViewModel;
    private ArtistsDetailViewModel artistViewModel;
    private ArtworksViewModel artworksViewModel;

    private int mGeneratedLightColor;
    private String emptyField;
    private String artistBiography;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_detail);
        ButterKnife.bind(this);

        PorterDuffColorFilter colorFilter = new PorterDuffColorFilter(getResources().getColor(R.color.color_primary),
                PorterDuff.Mode.SRC_ATOP);

        setSupportActionBar(toolbar);
        if ((toolbar.getNavigationIcon()) != null) {
            toolbar.getNavigationIcon().setColorFilter(colorFilter);
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        showsDetailViewModel = new ViewModelProvider(this).get(ShowsDetailViewModel.class);
        artistViewModel = new ViewModelProvider(this).get(ArtistsDetailViewModel.class);
        artworksViewModel = new ViewModelProvider(this).get(ArtworksViewModel.class);

        if (getIntent().getExtras() != null) {
            if (getIntent().hasExtra(RESULT_PARCEL_KEY)) {
                Bundle receivedBundle = getIntent().getExtras();
                Result result = receivedBundle.getParcelable(RESULT_PARCEL_KEY);
                if (result != null) {
                    setupSearchUi(result);
                }
            }
        }
    }

    private void setupSearchUi(Result results) {
        String titleString = results.getTitle();
        String typeString = results.getType();
        String descriptionString = results.getDescription();
        contentDescription.setText(descriptionString != null ? descriptionString : "");

        if (Utils.isNullOrEmpty(descriptionString)) {
            contentDescription.setVisibility(View.GONE);
        }
        setCollapsingToolbar(titleString);
        contentTitle.setText(titleString);
        contentType.setText(typeString);
        Log.d(TAG, "Title: " + titleString + "\nType: " + typeString + "\nDescription: " + descriptionString);

        if (results.getLinks() == null) {
            return;
        }
        LinksResult linksResult = results.getLinks();
        getThumbnail(linksResult);

        Self self = linksResult.getSelf();
        if (self == null || Utils.isNullOrEmpty(self.getHref())) {
            return;
        }
        String selfLinkString = self.getHref();
        Log.d(TAG, "temp, Self Link: " + selfLinkString);

        if (typeString.equals(SHOW)) {
            Log.d(TAG, "temp, card: show, selfLinkString= " + selfLinkString);
            showCardView.setVisibility(View.VISIBLE);
            initShowsContentViewModel(selfLinkString);
        }

        if (typeString.equals(ARTIST)) {
            Log.d(TAG, "temp, card: artist, selfLinkString= " + selfLinkString);
            initArtistContentViewModel(selfLinkString);
        }

        if (typeString.equals(ARTWORK)) {
            Log.d(TAG, "temp, card: artwork, selfLinkString= " + selfLinkString);
            // Initialize the ViewModel
            initArtworkViewModel(selfLinkString); // self links for artworks return most of the time "artwork not found" (this was confirmed also on Postman))
        }

        if (typeString.equals(GENE)) {
            initGenesContent(selfLinkString);
        }

        Permalink permalink = linksResult.getPermalink();
        String readMoreLink = permalink.getHref() == null ? "" : permalink.getHref();
        Log.d(TAG, "Perma Link: " + readMoreLink);
        getBioFromReadMoreLink(readMoreLink);

        readMoreButton.setOnClickListener(v -> {
            Intent openUrlIntent = new Intent(Intent.ACTION_VIEW);
            openUrlIntent.setData(Uri.parse(readMoreLink));
            startActivity(openUrlIntent);
        });
    }

    private void initGenesContent(String selfLinkString) {
        showsDetailViewModel.initGenesResultFromLink(selfLinkString);
        showsDetailViewModel.getGeneResultData().observe(this, this::setupGeneUi);
    }

    private void setupGeneUi(GeneContent geneResult) {
        if (geneResult == null) {
            return;
        }
        showCardView.setVisibility(View.VISIBLE);
        String geneName = geneResult.getDisplayName();
        String geneDescription = geneResult.getDescription();

        if (!Utils.isNullOrEmpty(geneName)) {
            showGeneName.setText(geneName);
            showGeneName.setVisibility(View.VISIBLE);
        }
        if (!Utils.isNullOrEmpty(geneDescription)) {
            final Markwon markwon = Markwon.create(this);
            markwon.setMarkdown(showDescription, geneDescription);
            showDescriptionLabel.setVisibility(View.VISIBLE);
            showDescription.setVisibility(View.VISIBLE);
        }

        ImageLinks imageLinksObject = geneResult.getLinks();
        MainImage mainImageObject = imageLinksObject.getImage();
        List<String> imageVersionList = geneResult.getImageVersions();
        showsDetailViewModel.getArtworkLargeImage(imageVersionList, mainImageObject).observe(this, s -> {
            if (s != null) {
                Log.d(TAG, "temp, largeArtworkLink is " + s);
                // Set the large image with Picasso
                Picasso.get()
                        .load(s)
                        .placeholder(R.color.color_primary)
                        .error(R.color.color_error)
                        .into(secondImage);

                Zoomy.Builder builder = new Zoomy.Builder(this)
                        .target(secondImage)
                        .enableImmersiveMode(false)
                        .animateZooming(false);
                builder.register();
            }
        });

        ArtworksLink artworksLink = imageLinksObject.getArtworksLink();
        String artworkLink = artworksLink.getHref(); // this link returns always the same result from the API (very disappointing)

        ArtistsLink artistsLink = imageLinksObject.getArtists();
        String artistLink = artistsLink.getHref();
        if (!Utils.isNullOrEmpty(artistLink)) {
            artistCardView.setVisibility(View.VISIBLE);
            Log.d(TAG, "temp, href of artists: " + artistLink);
            initArtistsViewModel(artistLink);
        }
    }

    // Scrape the Artsy website for additional information
    private void getBioFromReadMoreLink(String readMoreLink) {
        showsDetailViewModel.initBioFromWeb(readMoreLink);
        showsDetailViewModel.getBioFromWeb().observe(this, bio -> {
                    if (!Utils.isNullOrEmpty(bio) && Utils.isNullOrEmpty(artistBiography)) {
                        Log.d(TAG, "temp, artistBio = " + bio);
                        artistBio.setVisibility(View.VISIBLE);
                        artistBioLabel.setVisibility(View.VISIBLE);
                        artistBio.setText(bio);
                    }
                }
        );
    }

    private void getThumbnail(LinksResult linksResult) {
        if (linksResult.getThumbnail() == null) {
            return;
        }
        Thumbnail thumbnail = linksResult.getThumbnail();
        String imageThumbnailString = thumbnail.getHref();
        // Set the backdrop image
        Picasso.get()
                .load(imageThumbnailString)
                .placeholder(R.color.color_primary)
                .error(R.color.color_error)
                .into(contentImage, new Callback() {
                    @Override
                    public void onSuccess() {
                        // Get the image as a bitmap
                        Bitmap bitmap = ((BitmapDrawable) contentImage.getDrawable()).getBitmap();
                        // Get a color from the bitmap by using the Palette library
                        Palette palette = Palette.from(bitmap).generate();
                        mGeneratedLightColor = palette.getLightVibrantColor(mLightMutedColor);
                        cardView.setCardBackgroundColor(mGeneratedLightColor);
                        int darkMutedColor = palette.getDarkMutedColor(mLightMutedColor);
                        contentTitle.setTextColor(darkMutedColor);
                    }

                    @Override
                    public void onError(Exception e) {
                    }
                });
    }

    private void setCollapsingToolbar(String titleString) {
        int[][] states = new int[][]{
                new int[]{android.R.attr.state_enabled}, // enabled
        };
        int[] colors = new int[]{
                getResources().getColor(R.color.color_on_background),
        };

        collapsingToolbarLayout.setTitle(titleString);
        collapsingToolbarLayout.setExpandedTitleTextColor(new ColorStateList(states, colors));
        collapsingToolbarLayout.setExpandedTitleMarginBottom(bottomMargin);
        collapsingToolbarLayout.setCollapsedTitleTextColor(getResources().getColor(R.color.color_primary));
    }

    /**
     * Method for initialising the views from the show search View Model
     *
     * @param selfLink is the link that should be passed as a link to be used as a new call
     */
    private void initShowsContentViewModel(String selfLink) {
        showsDetailViewModel.initSearchLink(selfLink);
        showsDetailViewModel.getResultSelfLink().observe(this, this::setupShowsContentUi);
    }

    private void setupShowsContentUi(ShowContent showContent) {
        if (showContent == null) {
            return;
        }

        String pressRelease = showContent.getPressRelease();
        if (!Utils.isNullOrEmpty(pressRelease)) {
            showPress.setText(pressRelease);
            showPress.setVisibility(View.VISIBLE);
            showPressLabel.setVisibility(View.VISIBLE);
        }

        String contentDescription = showContent.getDescription();
        if (!Utils.isNullOrEmpty(contentDescription)) {
            showDescription.setText(contentDescription);
            showDescription.setVisibility(View.VISIBLE);
            showDescriptionLabel.setVisibility(View.VISIBLE);
        }

        String startDate = showContent.getStartAt();
        if (!Utils.isNullOrEmpty(startDate)) {
            showStartDate.setText(StringUtils.getDate(startDate));
            showStartDate.setVisibility(View.VISIBLE);
            showStartDateLabel.setVisibility(View.VISIBLE);
        }

        String endDate = showContent.getEndAt();
        if (!Utils.isNullOrEmpty(endDate)) {
            showEndDate.setText(StringUtils.getDate(endDate));
            showEndDate.setVisibility(View.VISIBLE);
            showEndDateLabel.setVisibility(View.VISIBLE);
        }

        // TODO: This is repeated for the second time
        LinksResult linksResult = showContent.getLinks();
        MainImage mainImageObject = linksResult.getImage();
        List<String> imageVersionList = showContent.getImageVersions();
        showsDetailViewModel.getArtworkLargeImage(imageVersionList, mainImageObject).observe(this, s -> {
            if (s != null) {
                // Set the large image with Picasso
                Picasso.get()
                        .load(s)
                        .placeholder(R.color.color_primary)
                        .error(R.color.color_error)
                        .into(secondImage);

                Zoomy.Builder builder = new Zoomy.Builder(this)
                        .target(secondImage)
                        .enableImmersiveMode(false)
                        .animateZooming(false);
                builder.register();
            }
        });
    }

    private void initArtistContentViewModel(String receivedArtistUrlString) {
        artistViewModel.initArtistData(receivedArtistUrlString);
        artistViewModel.getArtistData().observe(this, this::setupArtistUi);
    }

    private void setupArtistUi(Artist currentArtist) {
        if (currentArtist == null) {
            Log.d(TAG, "Artist is null");
            return;
        }
        // Meet necessary criteria for showing up artist card
        String artistNameString = currentArtist.getName();
        String hometown = currentArtist.getHometown();
        String birthday = currentArtist.getBirthday();
        String artistDeathString = currentArtist.getDeathday();
        String location = currentArtist.getLocation();
        String nationality = currentArtist.getNationality();
        artistBiography = currentArtist.getBiography();

        if (Utils.isNullOrEmpty(hometown) && Utils.isNullOrEmpty(location) && Utils.isNullOrEmpty(nationality) &&
                Utils.isNullOrEmpty(birthday) || Utils.isNullOrEmpty(artistDeathString)) {
            Log.d(TAG, "temp, not enough artist info to show details");
            return;
        }
        artistCardView.setVisibility(View.VISIBLE);
        emptyField = getString(R.string.not_applicable);

        // Name
        artistName.setText(Utils.isNullOrEmpty(artistNameString) ? emptyField : artistNameString);
        // Home town
        if (!Utils.isNullOrEmpty(hometown)) {
            artistHomeTown.setVisibility(View.VISIBLE);
            hometownLabel.setVisibility(View.VISIBLE);
            artistHomeTown.setText(hometown);
        }
        // Birth and dead date
        if (!Utils.isNullOrEmpty(birthday) || !Utils.isNullOrEmpty(artistDeathString)) {
            String lifespanConcatString = birthday + " - " + artistDeathString;
            artistLifespan.setText(lifespanConcatString);
            artistDivider.setVisibility(View.VISIBLE);
        }
        // Location
        if (!Utils.isNullOrEmpty(location)) {
            artistLocation.setText(location);
            artistLocation.setVisibility(View.VISIBLE);
            locationLabel.setVisibility(View.VISIBLE);
        }
        // Nationality
        if (!Utils.isNullOrEmpty(nationality)) {
            artistNationality.setText(nationality);
            artistNationality.setVisibility(View.VISIBLE);
            artistNationalityLabel.setVisibility(View.VISIBLE);
        }
        // Biography
        if (!Utils.isNullOrEmpty(artistBiography)) {
            artistBio.setText(artistBiography);
            artistBio.setVisibility(View.VISIBLE);
            artistBioLabel.setVisibility(View.VISIBLE);
        }

        ImageLinks imageLinks = currentArtist.getLinks();
        ArtworksLink artworksLink = imageLinks.getArtworksLink();
        String artworkLink = artworksLink.getHref();
        Log.d(TAG, "temp, href of artworks: " + artworkLink);
        initArtworksByArtistsViewModel(artworkLink);
    }

    private void initArtworksByArtistsViewModel(String artworksLink) {
        artistViewModel.initArtworksByArtistData(artworksLink);
        artistViewModel.getArtworksByArtistsData().observe(this, this::setupArtworksByArtist);
    }

    private void setupArtworksByArtist(List<Artwork> artworksList) {
        ArtworksByArtistAdapter artworksByArtist = new ArtworksByArtistAdapter(artworksList, null);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        artworksByArtistRv.setLayoutManager(gridLayoutManager);
        artworksByArtistRv.setAdapter(artworksByArtist);

        for (Artwork currentArtwork : artworksList) {
            displaySecondImage(currentArtwork);
            break;
        }
    }

    private void displaySecondImage(Artwork currentArtwork) {
        ImageLinks imageLinksObject = currentArtwork.getLinks();
        MainImage mainImageObject = imageLinksObject.getImage();
        List<String> imageVersionList = currentArtwork.getImageVersions();
        showsDetailViewModel.getArtworkLargeImage(imageVersionList, mainImageObject).observe(this, s -> {
            if (s != null) {
                // Set the large image with Picasso
                Picasso.get()
                        .load(s)
                        .placeholder(R.color.color_primary)
                        .error(R.color.color_error)
                        .into(secondImage);

                Zoomy.Builder builder = new Zoomy.Builder(this)
                        .target(secondImage)
                        .enableImmersiveMode(false)
                        .animateZooming(false);
                builder.register();
            }
        });
    }

    private void initArtistsViewModel(String artistLink) {
        artistViewModel.initArtistDataFromArtwork(artistLink);
        artistViewModel.getArtistDataFromArtwork().observe(this, this::setupArtistsList);
    }

    private void setupArtistsList(List<Artist> artistList) {
        ArtistListAdapter artistListAdapter = new ArtistListAdapter(artistList, null);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        artworksByArtistRv.setLayoutManager(gridLayoutManager);
        artworksByArtistRv.setAdapter(artistListAdapter);
    }

    private void initArtworkViewModel(String artworkLink) {
        artworksViewModel.initArtworkData(artworkLink);
        artworksViewModel.getArtworkFromLink().observe(this, this::setupArtworkInfoUi);
    }

    private void setupArtworkInfoUi(Artwork currentArtwork) {
        if (currentArtwork == null) {
            Log.d(TAG, "temp, Artwork is null");
            return;
        }
        emptyField = getString(R.string.not_applicable);
        artworkCardView.setVisibility(View.VISIBLE);

        String artworkTitle = currentArtwork.getTitle();
        artworkNameTextView.setText(Utils.isNullOrEmpty(artworkTitle) ? emptyField : artworkTitle);

        String medium = currentArtwork.getMedium();
        artworkMedium.setText(Utils.isNullOrEmpty(medium) ? emptyField : medium);

        String category = currentArtwork.getCategory();
        artworkCategory.setText(Utils.isNullOrEmpty(category) ? emptyField : category);

        String date = currentArtwork.getDate();
        artworkDate.setText(Utils.isNullOrEmpty(date) ? emptyField : date);

        String museum = currentArtwork.getCollectingInstitution();
        artworkMuseum.setText(Utils.isNullOrEmpty(museum) ? emptyField : museum);

        Dimensions dimensionObject = currentArtwork.getDimensions();
        if (dimensionObject != null) {
            CmSize cmSizeObject = dimensionObject.getCmSize();
            String dimensCmString = cmSizeObject.getText();
            dimensCm.setText(Utils.isNullOrEmpty(dimensCmString) ? emptyField : dimensCmString);

            InSize inSizeObject = dimensionObject.getInSize();
            String dimensInString = inSizeObject.getText();
            dimensIn.setText(Utils.isNullOrEmpty(dimensInString) ? emptyField : dimensInString);
        }

        String addInfo = currentArtwork.getAdditionalInformation();
        if (!Utils.isNullOrEmpty(addInfo)) {
            artworkInfoMarkdown.setVisibility(View.VISIBLE);
            artworkInfoLabel.setVisibility(View.VISIBLE);
            final Markwon markwon = Markwon.create(this);
            markwon.setMarkdown(artworkInfoMarkdown, addInfo);
        }

        displaySecondImage(currentArtwork);

        ImageLinks imageLinksObject = currentArtwork.getLinks();
        // Get the artist link
        ArtistsLink artistsLink = imageLinksObject.getArtists();
        Log.d(TAG, "artistsLink is =" + artistsLink);
    }
}
