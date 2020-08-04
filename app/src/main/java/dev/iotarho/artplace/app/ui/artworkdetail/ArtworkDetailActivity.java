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

package dev.iotarho.artplace.app.ui.artworkdetail;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.facebook.imagepipeline.request.Postprocessor;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.ms.square.android.expandabletextview.ExpandableTextView;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import dev.iotarho.artplace.app.R;
import dev.iotarho.artplace.app.callbacks.OnRefreshListener;
import dev.iotarho.artplace.app.database.entity.FavoriteArtworks;
import dev.iotarho.artplace.app.model.ArtworksLink;
import dev.iotarho.artplace.app.model.ImageLinks;
import dev.iotarho.artplace.app.model.SimilarArtworksLink;
import dev.iotarho.artplace.app.model.Thumbnail;
import dev.iotarho.artplace.app.model.artists.Artist;
import dev.iotarho.artplace.app.model.artworks.ArtistsLink;
import dev.iotarho.artplace.app.model.artworks.Artwork;
import dev.iotarho.artplace.app.model.artworks.CmSize;
import dev.iotarho.artplace.app.model.artworks.Dimensions;
import dev.iotarho.artplace.app.model.artworks.InSize;
import dev.iotarho.artplace.app.model.artworks.MainImage;
import dev.iotarho.artplace.app.model.search.Permalink;
import dev.iotarho.artplace.app.repository.FavArtRepository;
import dev.iotarho.artplace.app.ui.LargeArtworkActivity;
import dev.iotarho.artplace.app.ui.artistdetail.ArtistDetailActivity;
import dev.iotarho.artplace.app.ui.artistdetail.ArtistDetailViewModelFactory;
import dev.iotarho.artplace.app.ui.artistdetail.ArtistsDetailViewModel;
import dev.iotarho.artplace.app.ui.artworkdetail.adapter.ArtworksByArtistAdapter;
import dev.iotarho.artplace.app.ui.artworkdetail.adapter.SimilarArtworksAdapter;
import dev.iotarho.artplace.app.ui.searchdetail.ShowDetailViewModelFactory;
import dev.iotarho.artplace.app.ui.searchdetail.ShowsDetailViewModel;
import dev.iotarho.artplace.app.utils.ArtistDetailsUtils;
import dev.iotarho.artplace.app.utils.Injection;
import dev.iotarho.artplace.app.utils.Utils;
import io.noties.markwon.Markwon;
import jp.wasabeef.fresco.processors.BlurPostprocessor;

public class ArtworkDetailActivity extends AppCompatActivity implements OnRefreshListener {

    private static final String TAG = ArtworkDetailActivity.class.getSimpleName();
    private static final String ARTWORK_PARCEL_KEY = "artwork_key";
    private static final String ARTWORK_LARGER_IMAGE_KEY = "artwork_larger_link";
    private static final String ARTIST_URL_KEY = "artist_url";
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

    // Views of the Artwork
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
    @BindView(R.id.artwork_image)
    ImageView artworkImage;
    @BindView(R.id.artwork_dimens_cm)
    TextView dimensCm;
    @BindView(R.id.artwork_dimens_in)
    TextView dimensIn;
    @BindView(R.id.blurry_image)
    SimpleDraweeView blurryImage;
    @BindView(R.id.artwork_info_markdown)
    TextView artworkInfoMarkdown;
    @BindView(R.id.info_label)
    TextView artworkInfoLabel;

    // Views of the artist
    @BindView(R.id.artist_label)
    TextView artistLabel;
    @BindView(R.id.artist_cardview)
    MaterialCardView artistCard;
    @BindView(R.id.artist_name)
    TextView artistName;
    @BindView(R.id.hometown_label)
    TextView hometownLabel;
    @BindView(R.id.artist_home)
    TextView artistHomeTown;
    @BindView(R.id.artist_lifespan)
    TextView artistLifespan;
    @BindView(R.id.artist_location_label)
    TextView locationLabel;
    @BindView(R.id.artist_location)
    TextView artistLocation;
    @BindView(R.id.artist_nationality_label)
    TextView artistNationalityLabel;
    @BindView(R.id.artist_nationality)
    TextView artistNationality;
    @BindView(R.id.artist_bio)
    ExpandableTextView artistBio;
    @BindView(R.id.artist_bio_label)
    TextView artistBioLabel;

    // Similar Artworks Views
    @BindView(R.id.similar_artworks_cardview)
    CardView similarArtworksCardView;
    @BindView(R.id.similar_artworks_rv)
    RecyclerView similarArtworksRv;

    @BindView(R.id.artworks_by_artist_rv)
    RecyclerView artworksByArtistRv;

    @BindView(R.id.fav_button)
    FloatingActionButton mFavButton;

    private Artwork mArtworkObject;
    private String emptyField;
    private String artworkTitle;
    private String artworkId;
    private String artistUrl;
    private String permaLink;
    private String artworkThumbnail;
    private String artistNameString;
    private String dimensInString;
    private String dimensCmString;
    private String category;
    private String medium;
    private String date;
    private String museum;
    private String largeArtworkLink;
    private String artistBiography;

    private ArtistsDetailViewModel mArtistViewModel;
    private ShowsDetailViewModel showsDetailViewModel;
    private boolean mIsFavorite;

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

        if (savedInstanceState != null) {
            mIsFavorite = savedInstanceState.getBoolean(IS_FAV_SAVED_STATE);
        }

        // Initialize the ViewModels
        ArtistDetailViewModelFactory artistDetailViewModelFactory = Injection.provideArtistDetailViewModel();
        mArtistViewModel = new ViewModelProvider(getViewModelStore(), artistDetailViewModelFactory).get(ArtistsDetailViewModel.class);
        ShowDetailViewModelFactory showDetailViewModelFactory = Injection.provideShowDetailViewModel();
        showsDetailViewModel = new ViewModelProvider(getViewModelStore(), showDetailViewModelFactory).get(ShowsDetailViewModel.class);

        if (getIntent().getExtras() != null) {
            Bundle bundle = getIntent().getExtras();
            mArtworkObject = bundle.getParcelable(ARTWORK_PARCEL_KEY);

            if (mArtworkObject != null) {
                artworkCardView.setVisibility(View.VISIBLE);
                emptyField = getString(R.string.not_applicable);
                setupArtworkInfoUi(mArtworkObject, emptyField);

                artworkId = mArtworkObject.getId();
                Log.d(TAG, "Received artwork id: " + artworkId);

                // Check if the item exists in the db already or not!!!
                // TODO: Remove Repository instance from Activity!
                FavArtRepository.getInstance(getApplication()).executeGetItemById(artworkId, isFav -> {
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
                });
            }
        }

        clickFab();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(IS_FAV_SAVED_STATE, mIsFavorite);
    }

    private void setupArtworkInfoUi(Artwork currentArtwork, String emptyField) {
        artworkTitle = currentArtwork.getTitle();
        artworkNameTextView.setText(Utils.isNullOrEmpty(artworkTitle) ? emptyField : artworkTitle);
        collapsingToolbarLayout.setTitle(artworkTitle);
        collapsingToolbarLayout.setCollapsedTitleTextColor(getResources().getColor(R.color.color_primary));

        medium = currentArtwork.getMedium();
        artworkMedium.setText(Utils.isNullOrEmpty(medium) ? emptyField : medium);

        category = currentArtwork.getCategory();
        artworkCategory.setText(Utils.isNullOrEmpty(category) ? emptyField : category);

        date = currentArtwork.getDate();
        artworkDate.setText(Utils.isNullOrEmpty(date) ? emptyField : date);

        museum = currentArtwork.getCollectingInstitution();
        artworkMuseum.setText(Utils.isNullOrEmpty(museum) ? emptyField : museum);

        Dimensions dimensionObject = currentArtwork.getDimensions();
        if (dimensionObject != null) {
            CmSize cmSizeObject = dimensionObject.getCmSize();
            dimensCmString = cmSizeObject.getText();
            dimensCm.setText(Utils.isNullOrEmpty(dimensCmString) ? emptyField : dimensCmString);

            InSize inSizeObject = dimensionObject.getInSize();
            dimensInString = inSizeObject.getText();
            dimensIn.setText(Utils.isNullOrEmpty(dimensInString) ? emptyField : dimensInString);
        }

        String addInfo = currentArtwork.getAdditionalInformation();
        if (!Utils.isNullOrEmpty(addInfo)) {
            artworkInfoMarkdown.setVisibility(View.VISIBLE);
            artworkInfoLabel.setVisibility(View.VISIBLE);
            final Markwon markwon = Markwon.create(this);
            markwon.setMarkdown(artworkInfoMarkdown, addInfo);
        }

        ImageLinks imageLinksObject = getImageLinks(currentArtwork);
        if (imageLinksObject.getArtists() != null) {
            ArtistsLink artistsLinkObject = imageLinksObject.getArtists();
            artistUrl = artistsLinkObject.getHref();
            Log.d(TAG, "artistUrl = " + artistUrl);

            // Initialize the artist ViewModel
            initArtistViewModel(artistUrl);
            String artworkId = currentArtwork.getId();
        }

        // Get the Permalink for sharing it outside the app
        Permalink permalinkForShare = imageLinksObject.getPermalink();
        permaLink = permalinkForShare.getHref();
        Log.d(TAG, "temp, permaLink: " + permaLink);
        getBioFromReadMoreLink(permaLink);

        SimilarArtworksLink similarArtworksLinkObject = imageLinksObject.getSimilarArtworks();
        String similarArtworksLink = similarArtworksLinkObject.getHref();
        Log.d(TAG, "Similar Artworks link: " + similarArtworksLink);
        initSimilarViewModel(similarArtworksLink);
    }

    private ImageLinks getImageLinks(Artwork currentArtwork) {
        ImageLinks imageLinksObject = currentArtwork.getLinks();
        Thumbnail thumbnail = imageLinksObject.getThumbnail();
        artworkThumbnail = thumbnail.getHref();

        largeArtworkLink = ArtistDetailsUtils.getLargeImageUrl(currentArtwork, imageLinksObject);
        // Set the large image with Picasso
        Picasso.get()
                .load(Uri.parse(largeArtworkLink))
                .placeholder(R.color.color_on_surface)
                .error(R.color.color_error)
                .into(artworkImage);

        String squareImage = ArtistDetailsUtils.getSquareImageUrl(currentArtwork, imageLinksObject);
        makeImageBlurry(squareImage);
        // Set a click listener on the image
        openArtworkFullScreen();
        return imageLinksObject;
    }

    private void openArtworkFullScreen() {
        artworkImage.setOnClickListener(v -> {
            Intent largeImageIntent = new Intent(ArtworkDetailActivity.this,
                    LargeArtworkActivity.class);
            largeImageIntent.putExtra(ARTWORK_LARGER_IMAGE_KEY, largeArtworkLink);
            startActivity(largeImageIntent);
        });
    }

    /**
     * Make the image blurry with the help of SimpleDraweeView View
     * Tutorial:https://android.jlelse.eu/android-image-blur-using-fresco-vs-picasso-ea095264abbf
     */
    private void makeImageBlurry(String linkString) {
        // Initialize Blur Post Processor
        mPostprocessor = new BlurPostprocessor(this, 20);

        // Instantiate Image Request using Post Processor as parameter
        mImageRequest = ImageRequestBuilder.newBuilderWithSource(Uri.parse(linkString))
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

    /**
     * Method for initializing the ViewModel of the Artist
     *
     * @param artistLink is the given link to the artist
     */
    private void initArtistViewModel(String artistLink) {
        mArtistViewModel.initArtistDataFromArtwork(artistLink);
        mArtistViewModel.getArtistDataFromArtwork().observe(this, artists -> {
            if (artists != null && artists.size() != 0) {
                artistLabel.setVisibility(View.VISIBLE);
                artistCard.setVisibility(View.VISIBLE);
                for (Artist currentArtist : artists) {
                    setupArtistUI(currentArtist);
                }
            }
        });
    }

    private void setupArtistUI(Artist currentArtist) {
        emptyField = getString(R.string.not_applicable);

        // Get the name of the artist
        artistNameString = currentArtist.getName();
        if (!Utils.isNullOrEmpty(artistNameString)) {
            artistNameButton.setVisibility(View.VISIBLE);
            artistName.setText(artistNameString);
            artistNameButton.setText(artistNameString);
            artistNameButton.setOnClickListener(v -> {
                Intent intent = new Intent(ArtworkDetailActivity.this, ArtistDetailActivity.class);
                // Send the name of the artwork as extra
                intent.putExtra(ARTWORK_TITLE_KEY, artworkTitle);
                intent.putExtra(ARTIST_URL_KEY, artistUrl);
                startActivity(intent);
            });
        }
        // Home town
        if (!Utils.isNullOrEmpty(currentArtist.getHometown())) {
            artistHomeTown.setVisibility(View.VISIBLE);
            hometownLabel.setVisibility(View.VISIBLE);
            artistHomeTown.setText(currentArtist.getHometown());
        }
        // Birth and dead date
        if (!Utils.isNullOrEmpty(currentArtist.getBirthday()) || !Utils.isNullOrEmpty(currentArtist.getDeathday())) {
            String artistBirthString = currentArtist.getBirthday();
            String artistDeathString = currentArtist.getDeathday();

            String lifespanConcatString = artistBirthString + " - " + artistDeathString;
            artistLifespan.setText(lifespanConcatString);
        }
        // Location
        if (!Utils.isNullOrEmpty(currentArtist.getLocation())) {
            artistLocation.setVisibility(View.VISIBLE);
            locationLabel.setVisibility(View.VISIBLE);
            artistLocation.setText(currentArtist.getLocation());
        }
        // Nationality
        if (!Utils.isNullOrEmpty(currentArtist.getNationality())) {
            artistNationality.setVisibility(View.VISIBLE);
            artistNationalityLabel.setVisibility(View.VISIBLE);
            artistNationality.setText(currentArtist.getNationality());
        }

        // Get the list of image versions first
        List<String> imageVersionList = currentArtist.getImageVersions();
        // Get the first entry from this list, which corresponds to "large"
        String largeVersion = "large";
        String versionString = ArtistDetailsUtils.getVersionImage(imageVersionList, largeVersion);

        ImageLinks imageLinksObject = currentArtist.getLinks();
        MainImage mainImageObject = imageLinksObject.getImage();
        // Get the link for the current artist,
        // e.g.: "https://d32dm0rphc51dk.cloudfront.net/rqoQ0ln0TqFAf7GcVwBtTw/{image_version}.jpg"
        String artistImgLinkString = mainImageObject.getHref();
        // Replace the {image_version} from the artworkImgLinkString with
        // the wanted version, e.g. "large"
        String newArtistLinkString = artistImgLinkString
                .replaceAll("\\{.*?\\}", versionString);
        // TODO: We are not doing anything with this image link
        Log.d(TAG, "temp, newArtistLinkString: " + newArtistLinkString);

        if (currentArtist.getBiography() != null) {
            artistBiography = currentArtist.getBiography();
            artistBio.setText(artistBiography);

            if (currentArtist.getBiography().isEmpty()) {
                artistBio.setVisibility(View.GONE);
                artistBioLabel.setVisibility(View.GONE);
            }
        } else {
            artistBio.setVisibility(View.GONE);
            artistBioLabel.setVisibility(View.GONE);
        }

        ImageLinks imageLinks = currentArtist.getLinks();
        ArtworksLink artworksLink = imageLinks.getArtworksLink();
        String href = artworksLink.getHref();
        Log.d(TAG, "temp, href of artworks: " + href);
        initArtworksByArtistsViewModel(href);
    }

    /**
     * Method for initializing the ViewModel of the Similar Artworks
     *
     * @param similarArtLink is the given link to the similar artworks
     */
    private void initSimilarViewModel(String similarArtLink) {
        mArtistViewModel.initSimilarArtworksData(similarArtLink);
        mArtistViewModel.getSimilarArtworksData().observe(this, artworkList -> {
            if (artworkList != null) {
                setupSimilarArtworksUI(artworkList);
            }
        });
    }

    private void setupSimilarArtworksUI(List<Artwork> artworkList) {
        similarArtworksCardView.setVisibility(View.VISIBLE);
        SimilarArtworksAdapter similarArtworksAdapter = new SimilarArtworksAdapter(artworkList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.HORIZONTAL, false);
        similarArtworksRv.setLayoutManager(layoutManager);
        similarArtworksRv.setAdapter(similarArtworksAdapter);
    }

    private void clickFab() {
        mFavButton.setOnClickListener(this::setIconOnFab);
    }

    private void initArtworksByArtistsViewModel(String artworksLink) {
        mArtistViewModel.initArtworksByArtistData(artworksLink);
        mArtistViewModel.getArtworksByArtistsData().observe(this, this::setupArtworksByArtist);
    }

    private void setupArtworksByArtist(List<Artwork> artworksList) {
        ArtworksByArtistAdapter artworksByArtist = new ArtworksByArtistAdapter(artworksList, this);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        artworksByArtistRv.setLayoutManager(gridLayoutManager);
        artworksByArtistRv.setAdapter(artworksByArtist);
    }

    /**
     * Set the icons on the FAB according to the clicks
     *
     * @param view the returned view of the button
     */
    private void setIconOnFab(View view) {
        int tagValue = (int) view.getTag();

        switch (tagValue) {
            case (FAV_TAG):
                // Delete from the db
                deleteItemFromFav(artworkId);
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
    private void deleteItemFromFav(String artworkId) {
        FavArtRepository.getInstance(getApplication()).deleteItem(artworkId);
        Snackbar snack = Snackbar.make(coordinatorLayout, R.string.snackbar_item_removed, Snackbar.LENGTH_LONG);
        View view = snack.getView();
        TextView tv = view.findViewById(com.google.android.material.R.id.snackbar_text);
        tv.setTextColor(getResources().getColor(R.color.color_text_contrast));
        view.setBackgroundColor(getResources().getColor(R.color.color_snackbar_bg));
        snack.show();
    }

    /**
     * Method for adding an item to the database
     */
    private void addArtworkToFavorites() {
        FavoriteArtworks favArtwork = new FavoriteArtworks(artworkId, artworkTitle, artistNameString,
                category, medium, date, museum, artworkThumbnail, largeArtworkLink, dimensInString, dimensCmString);

        FavArtRepository.getInstance(getApplication()).insertItem(favArtwork);
        Snackbar snack = Snackbar.make(coordinatorLayout, R.string.snackbar_item_added, Snackbar.LENGTH_LONG);
        View view = snack.getView();
        TextView tv = view.findViewById(com.google.android.material.R.id.snackbar_text);
        tv.setTextColor(getResources().getColor(R.color.color_text_contrast));
        view.setBackgroundColor(getResources().getColor(R.color.color_snackbar_bg));
        snack.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.artwork_detail_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_share) {// Share the Permalink here
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            if (!Utils.isNullOrEmpty(permaLink)) {
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT, permaLink);
                startActivity(shareIntent);
                Log.d(TAG, "Shared permalink: " + permaLink);
            } else {
                Toast.makeText(this, "Nothing to share.", Toast.LENGTH_SHORT).show();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRefreshConnection() {
        // TODO: implement how to behave on refresh
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
}

