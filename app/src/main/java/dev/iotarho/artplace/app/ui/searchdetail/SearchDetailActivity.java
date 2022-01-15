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
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.card.MaterialCardView;
import com.ms.square.android.expandabletextview.ExpandableTextView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindDimen;
import butterknife.BindView;
import butterknife.ButterKnife;
import dev.iotarho.artplace.app.R;
import dev.iotarho.artplace.app.callbacks.OnArtistClickHandler;
import dev.iotarho.artplace.app.callbacks.OnArtworkClickListener;
import dev.iotarho.artplace.app.callbacks.OnResultClickListener;
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
import dev.iotarho.artplace.app.ui.artistdetail.ArtistDetailActivity;
import dev.iotarho.artplace.app.ui.artistdetail.ArtistDetailViewModelFactory;
import dev.iotarho.artplace.app.ui.artistdetail.ArtistForGenreListAdapter;
import dev.iotarho.artplace.app.ui.artistdetail.ArtistsDetailViewModel;
import dev.iotarho.artplace.app.ui.artworkdetail.adapter.ArtworksByArtistAdapter;
import dev.iotarho.artplace.app.ui.artworks.ArtworksViewModel;
import dev.iotarho.artplace.app.ui.artworks.ArtworksViewModelFactory;
import dev.iotarho.artplace.app.ui.searchresults.datasource.SearchResultsLogic;
import dev.iotarho.artplace.app.utils.ArtistInfoUtils;
import dev.iotarho.artplace.app.utils.ImageUtils;
import dev.iotarho.artplace.app.utils.Injection;
import dev.iotarho.artplace.app.utils.StringUtils;
import dev.iotarho.artplace.app.utils.Utils;
import io.noties.markwon.Markwon;

import static dev.iotarho.artplace.app.ui.artistdetail.ArtistDetailActivity.ARTIST_EXTRA_KEY;
import static dev.iotarho.artplace.app.ui.artistdetail.ArtistDetailActivity.ARTIST_URL_KEY;
import static dev.iotarho.artplace.app.utils.Constants.SearchFragment.ARTIST_TYPE;
import static dev.iotarho.artplace.app.utils.Constants.SearchFragment.ARTWORK_TYPE;
import static dev.iotarho.artplace.app.utils.Constants.SearchFragment.GENE_TYPE;
import static dev.iotarho.artplace.app.utils.Constants.SearchFragment.SHOW_TYPE;


public class SearchDetailActivity extends AppCompatActivity implements OnResultClickListener, OnArtistClickHandler, OnArtworkClickListener {

    private static final String RESULT_PARCEL_KEY = "results_key";
    private static final String TAG = SearchDetailActivity.class.getSimpleName();
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
    @BindView(R.id.appbar)
    AppBarLayout appBarLayout;
    @BindView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout collapsingToolbarLayout;
    @BindView(R.id.artwork_title_layout)
    LinearLayout bgLayout;
    @BindView(R.id.card_view_content)
    CardView cardView;
    @BindView(R.id.read_more)
    TextView readMoreButton;

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
    ExpandableTextView artistBio;
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

    // Search List Views
    @BindView(R.id.similar_artworks_cardview)
    CardView searchContentCardView;
    @BindView(R.id.similar_artworks_label)
    TextView cardLabel;
    @BindView(R.id.similar_artworks_rv)
    RecyclerView searchResultsRv;

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
    private PorterDuffColorFilter colorFilter;
    private String titleString;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_detail);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        ShowDetailViewModelFactory showDetailViewModelFactory = Injection.provideShowDetailViewModel();
        showsDetailViewModel = new ViewModelProvider(getViewModelStore(), showDetailViewModelFactory).get(ShowsDetailViewModel.class);
        ArtistDetailViewModelFactory artistDetailViewModelFactory = Injection.provideArtistDetailViewModel();
        artistViewModel = new ViewModelProvider(getViewModelStore(), artistDetailViewModelFactory).get(ArtistsDetailViewModel.class);
        ArtworksViewModelFactory artworksViewModelFactory = Injection.provideArtworksViewModelFactory();
        artworksViewModel = new ViewModelProvider(getViewModelStore(), artworksViewModelFactory).get(ArtworksViewModel.class);

        if (getIntent().getExtras() != null) {
            if (getIntent().hasExtra(RESULT_PARCEL_KEY)) {
                Bundle receivedBundle = getIntent().getExtras();
                Result result = receivedBundle.getParcelable(RESULT_PARCEL_KEY);
                if (result != null) {
                    setupOverviewUi(result);
                }
            }
        }
    }

    private void setupOverviewUi(Result results) {
        titleString = results.getTitle();
        String typeString = results.getType();
        String descriptionString = results.getDescription();
        contentDescription.setText(descriptionString != null ? descriptionString : "");

        if (Utils.isNullOrEmpty(descriptionString)) {
            contentDescription.setVisibility(View.GONE);
        }
        Utils.setCollapsingToolbar(titleString, appBarLayout, collapsingToolbarLayout, mGeneratedLightColor);
        contentTitle.setText(titleString);
        contentType.setText(getString(R.string.genre_name, typeString));

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

        if (typeString.equals(SHOW_TYPE)) {
            showCardView.setVisibility(View.VISIBLE);
            initShowsContentViewModel(selfLinkString);
        }

        if (typeString.equals(ARTIST_TYPE)) {
            initArtistContentViewModel(selfLinkString);
        }

        if (typeString.equals(ARTWORK_TYPE)) {
            // Initialize the ViewModel
            initArtworkViewModel(selfLinkString); // self links for artworks return most of the time "Artwork Not Found" (this was confirmed also on Postman))
            // get the artist name from the titleString
            if (titleString.contains(",")) {
                String artistNameFromTitle = titleString.substring(0, titleString.indexOf(","));
                makeNewSearchFromArtist(artistNameFromTitle);
                String remainder = titleString.substring(titleString.indexOf(",") + 1);
                Log.d(TAG, "temp, artistNameFromTitle= " + artistNameFromTitle + " remainder= " + remainder);
            }
        }

        if (typeString.equals(GENE_TYPE)) {
            contentType.setText(getString(R.string.genre_name, getString(R.string.genre_type)));
            initGenreContent(selfLinkString);
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

    private void makeNewSearchFromArtist(String artistNameFromTitle) {
        //make a new call to search only for this string + "artist" type
        artistViewModel.initSearchArtists(artistNameFromTitle, ARTIST_TYPE);
        artistViewModel.getSearchArtistsList().observe(this, this::setupSearchArtistList);
        cardLabel.setText(getString(R.string.search_results_title, artistNameFromTitle));
        searchContentCardView.setVisibility(View.VISIBLE);
    }

    private void setupSearchArtistList(List<Result> artistSearch) {
        List<Result> filteredList = SearchResultsLogic.getFilteredResults(artistSearch);
        SearchArtistListAdapter searchArtistListAdapter = new SearchArtistListAdapter(filteredList, this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.HORIZONTAL, false);
        searchResultsRv.setLayoutManager(layoutManager);
        // Set the Adapter on the RecyclerView
        searchResultsRv.setAdapter(searchArtistListAdapter);
    }

    private void initGenreContent(String selfLinkString) {
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
        List<String> imageVersionList = geneResult.getImageVersions();
        MainImage mainImageObject = imageLinksObject.getImage();
        ImageUtils.displayImage(mainImageObject, imageVersionList, secondImage);
        ImageUtils.setupZoomyImage(this, secondImage);

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
                        artistBio.setText(bio);
                        artistBio.setVisibility(View.VISIBLE);
                        artistBioLabel.setVisibility(View.VISIBLE);
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
                        setUIColorsFromImage();
                    }

                    @Override
                    public void onError(Exception e) {
                    }
                });
    }

    private void setUIColorsFromImage() {
        // Get the image as a bitmap
        Bitmap bitmap = ((BitmapDrawable) contentImage.getDrawable()).getBitmap();
        // Get a color from the bitmap by using the Palette library
        Palette palette = Palette.from(bitmap).generate();
        mGeneratedLightColor = palette.getLightVibrantColor(mLightMutedColor);
        cardView.setCardBackgroundColor(mGeneratedLightColor);
        // set the color of the back button
        colorFilter = new PorterDuffColorFilter(mGeneratedLightColor, PorterDuff.Mode.SRC_ATOP);
        if ((toolbar.getNavigationIcon()) != null) {
            toolbar.getNavigationIcon().setColorFilter(colorFilter);
        }
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

        LinksResult linksResult = showContent.getLinks();
        MainImage mainImageObject = linksResult.getImage();
        List<String> imageVersionList = showContent.getImageVersions();
        ImageUtils.displayImage(mainImageObject, imageVersionList, secondImage);
        ImageUtils.setupZoomyImage(this, secondImage);
    }

    private void initArtistContentViewModel(String receivedArtistUrlString) {
        artistViewModel.initArtistData(receivedArtistUrlString);
        artistViewModel.getArtistData().observe(this, this::setupArtistUi);
    }

    private void setupArtistUi(Artist currentArtist) {
        ArtistInfoUtils.setupArtistUi(currentArtist, artistCardView, artistName, artistHomeTown,
                hometownLabel, artistLifespan, artistDivider, artistLocation, locationLabel, artistNationality,
                artistNationalityLabel, artistBio, artistBioLabel);
        ArtistInfoUtils.displayArtistImage(currentArtist, secondImage);
        ImageUtils.setupZoomyImage(this, secondImage);

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
        ArtworksByArtistAdapter artworksByArtist = new ArtworksByArtistAdapter(artworksList, null, this);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        artworksByArtistRv.setLayoutManager(gridLayoutManager);
        artworksByArtistRv.setAdapter(artworksByArtist);
    }

    private void initArtistsViewModel(String artistLink) {
        artistViewModel.initArtistDataFromArtwork(artistLink);
        artistViewModel.getArtistDataFromArtwork().observe(this, this::setupArtistsForGenreList);
    }

    private void setupArtistsForGenreList(List<Artist> artistList) {
        ArtistForGenreListAdapter artistForGenreListAdapter = new ArtistForGenreListAdapter(artistList, null, this);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        artworksByArtistRv.setLayoutManager(gridLayoutManager);
        artworksByArtistRv.setAdapter(artistForGenreListAdapter);
    }

    private void initArtworkViewModel(String artworkLink) {
        artworksViewModel.initArtworkData(artworkLink);
        artworksViewModel.getArtworkFromLink().observe(this, this::setupArtworkInfoUi);
    }

    private void setupArtworkInfoUi(Artwork currentArtwork) {
        if (currentArtwork == null) {
            Log.e(TAG, "Artwork is null");
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

        ImageLinks imageLinksObject = currentArtwork.getLinks();
        List<String> imageVersionList = currentArtwork.getImageVersions();
        MainImage mainImageObject = imageLinksObject.getImage();
        ImageUtils.displayImage(mainImageObject, imageVersionList, secondImage);
        ImageUtils.setupZoomyImage(this, secondImage);

        // Get the artist link
        ArtistsLink artistsLink = imageLinksObject.getArtists();
        Log.d(TAG, "artistsLink is =" + artistsLink);
    }

    @Override
    public void onResultClick(Result result) {
        Intent intent = new Intent(this, ArtistDetailActivity.class);
        // Send the name of the artwork as extra
        LinksResult links = result.getLinks();
        Self self = links.getSelf();
        String link = self.getHref();
        Log.d(TAG, "self: " + link);
        intent.putExtra(ARTIST_URL_KEY, link);
        startActivity(intent);
    }

    @Override
    public void onArtistClick(Artist artist) {
        Intent intent = new Intent(this, ArtistDetailActivity.class);
        intent.putExtra(ARTIST_EXTRA_KEY, artist);
        startActivity(intent);
    }

    @Override
    public void onArtworkClick(Artwork artwork, int position) {

    }
}
