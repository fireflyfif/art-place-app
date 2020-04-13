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

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.card.MaterialCardView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Objects;

import br.tiagohm.markdownview.MarkdownView;
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
import dev.iotarho.artplace.app.model.search.LinksResult;
import dev.iotarho.artplace.app.model.search.Permalink;
import dev.iotarho.artplace.app.model.search.Result;
import dev.iotarho.artplace.app.model.search.ShowContent;
import dev.iotarho.artplace.app.ui.artistdetail.ArtistsDetailViewModel;
import dev.iotarho.artplace.app.ui.artworkdetail.adapter.ArtworksByArtistAdapter;
import dev.iotarho.artplace.app.ui.artworks.ArtworksViewModel;
import dev.iotarho.artplace.app.utils.StringUtils;
import dev.iotarho.artplace.app.utils.Utils;

public class SearchDetailActivity extends AppCompatActivity {

    private static final String RESULT_PARCEL_KEY = "results_key";
    private static final String TAG = SearchDetailActivity.class.getSimpleName();
    private static final String SHOW = "show";
    private static final String ARTIST = "artist";
    private static final String ARTWORK = "artwork";
    public static final String ARTIST_BIO = "ArtistBio__BioSpan-sc-14mck41-0 vDRHu";
    private int mLightMutedColor = 0xFFAAAAAA;
    private static final String IMAGE_LARGE = "large";
    private static final String IMAGE_SQUARE = "square";
    private static final String IMAGE_LARGER = "larger";

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
    MarkdownView artworkInfoMarkdown;
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

        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        PorterDuffColorFilter colorFilter = new PorterDuffColorFilter(getResources().getColor(R.color.color_primary),
                PorterDuff.Mode.SRC_ATOP);
        Objects.requireNonNull(toolbar.getNavigationIcon()).setColorFilter(colorFilter);

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
        String selfLinkString = Objects.requireNonNull(self.getHref());
        Log.d(TAG, "temp, Self Link: " + selfLinkString);

        showsDetailViewModel = new ViewModelProvider(this).get(ShowsDetailViewModel.class);

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

    // Scrape the Artsy website for additional information
    private void getBioFromReadMoreLink(String readMoreLink) {
        showsDetailViewModel.initBioFromWeb(readMoreLink);
        showsDetailViewModel.getBioFromWeb().observe(this, bio -> {
                    if (!Utils.isNullOrEmpty(bio) && !Utils.isNullOrEmpty(artistBiography)) {
                        // TODO: what to do when there is already a bio from API and it differs from the one on the site
                        Log.d(TAG, "temp, artistBio = " + bio);
                        artistBio.setVisibility(View.VISIBLE);
                        artistBioLabel.setVisibility(View.VISIBLE);
                        artistBio.setText(bio);
                    }
                }
        );
    }

    private void getThumbnail(LinksResult linksResult) {
        if (linksResult.getThumbnail() != null) {
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
        showsDetailViewModel.getResultSelfLink().observe(this, showContent -> {
            if (showContent != null) {
                setupShowsContentUi(showContent);
            }
        });
    }

    private void setupShowsContentUi(ShowContent showContent) {
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
            showStartDate.setText(StringUtils.getDate(startDate));

            if (showContent.getStartAt().isEmpty()) {
                showStartDate.setVisibility(View.GONE);
                showStartDateLabel.setVisibility(View.GONE);
            }
        }

        if (showContent.getEndAt() != null) {
            String endDate = showContent.getEndAt();
            showEndDate.setText(StringUtils.getDate(endDate));

            if (showContent.getEndAt().isEmpty()) {
                showEndDate.setVisibility(View.GONE);
                showEndDateLabel.setVisibility(View.GONE);
            }
        }
    }

    private void initArtistContentViewModel(String receivedArtistUrlString) {
        artistViewModel = new ViewModelProvider(this).get(ArtistsDetailViewModel.class);
        artistViewModel.initArtistData(receivedArtistUrlString);

        artistViewModel.getArtistData().observe(this, artists -> {
            if (artists != null) {
                setupArtistUi(artists);
            }
        });
    }

    private void setupArtistUi(Artist currentArtist) {
        if (currentArtist == null) {
            // TODO: Meet necessary criteria for showing up artist card
            Log.d(TAG, "temp, Artist is null");
            return;
        }
        artistCardView.setVisibility(View.VISIBLE);
        emptyField = getString(R.string.not_applicable);
        // Name
        String artistNameString = currentArtist.getName();
        artistName.setText(Utils.isNullOrEmpty(artistNameString) ? emptyField : artistNameString);
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
        // Biography
        if (!Utils.isNullOrEmpty(currentArtist.getBiography())) {
            artistBio.setVisibility(View.VISIBLE);
            artistBioLabel.setVisibility(View.VISIBLE);
            artistBiography = currentArtist.getBiography();
            artistBio.setText(artistBiography);
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
    }

    private void initArtworkViewModel(String artworkLink) {
        artworksViewModel = new ViewModelProvider(this).get(ArtworksViewModel.class);
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
            artworkInfoMarkdown.loadMarkdown(addInfo); // load the markdown text
        }
        ImageLinks imageLinksObject = currentArtwork.getLinks();
        setImage(currentArtwork, imageLinksObject);

        // Get the artist link
        ArtistsLink artistsLink = imageLinksObject.getArtists();
        Log.d(TAG, "artistsLink is =" + artistsLink);
    }

    private void setImage(Artwork currentArtwork, ImageLinks imageLinksObject) {
        MainImage mainImageObject = imageLinksObject.getImage();
        if (currentArtwork.getImageVersions() != null) {
            List<String> imageVersionList = currentArtwork.getImageVersions();
            // Get the link for the current artwork,
            // e.g.: "https://d32dm0rphc51dk.cloudfront.net/rqoQ0ln0TqFAf7GcVwBtTw/{image_version}.jpg"
            String artworkImgLinkString = mainImageObject.getHref();
            // Replace the {image_version} from the artworkImgLinkString with
            // the wanted version, e.g. "large"
            String largeArtworkLink = extractImageLink(getVersionImage(imageVersionList, IMAGE_LARGE), artworkImgLinkString);
            Log.d(TAG, "temp, largeArtworkLink is " + largeArtworkLink);
            // Set the large image with Picasso
            Picasso.get()
                    .load(largeArtworkLink)
                    .placeholder(R.color.color_primary)
                    .error(R.color.color_error)
                    .into(secondImage);

            String squareImage = extractImageLink(getVersionImage(imageVersionList, IMAGE_SQUARE), artworkImgLinkString);
            // TODO: ...
        }
    }

    private String getVersionImage(List<String> imageVersionList, String version) {
        if (imageVersionList.contains(version)) {
            return imageVersionList.get(imageVersionList.indexOf(version));
        } else {
            return imageVersionList.get(0);  // Get the first one no matter what is the value
        }
    }

    private String extractImageLink(String stringFinal, String stringFull) {
        return stringFull.replaceAll("\\{.*?\\}", stringFinal);
    }
}
