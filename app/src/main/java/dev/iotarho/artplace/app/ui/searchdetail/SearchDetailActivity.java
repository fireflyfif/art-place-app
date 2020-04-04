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
import dev.iotarho.artplace.app.model.artworks.Artwork;
import dev.iotarho.artplace.app.model.artworks.CmSize;
import dev.iotarho.artplace.app.model.artworks.Dimensions;
import dev.iotarho.artplace.app.model.artworks.InSize;
import dev.iotarho.artplace.app.model.search.LinksResult;
import dev.iotarho.artplace.app.model.search.Permalink;
import dev.iotarho.artplace.app.model.search.Result;
import dev.iotarho.artplace.app.model.search.ShowContent;
import dev.iotarho.artplace.app.ui.artistdetail.ArtistsDetailViewModel;
import dev.iotarho.artplace.app.ui.artworkdetail.adapter.ArtworksByArtistAdapter;
import dev.iotarho.artplace.app.utils.StringUtils;
import dev.iotarho.artplace.app.utils.Utils;

public class SearchDetailActivity extends AppCompatActivity {

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

    // Artwork Card View
    @BindView(R.id.artwork_cardview)
    CardView artworkCardView;
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

    private ShowsDetailViewModel mShowsViewModel;
    private ArtistsDetailViewModel mArtistViewModel;
    private int mGeneratedLightColor;

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
        String descriptionString = null;
        if (results.getDescription() != null) {
            descriptionString = results.getDescription();
            contentDescription.setText(descriptionString);
            if (results.getDescription().isEmpty()) {
                contentDescription.setVisibility(View.GONE);
            }
        }
        Log.d(TAG, "Title: " + titleString + "\nType: " + typeString + "\nDescription: " + descriptionString);

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

        contentTitle.setText(titleString);
        contentType.setText(typeString);

        if (results.getLinks() == null) {
            return;
        }
        LinksResult linksResult = results.getLinks();
        if (linksResult.getThumbnail() != null) {
            Thumbnail thumbnail = linksResult.getThumbnail();
            String imageThumbnailString = thumbnail.getHref();

            if (Utils.isNullOrEmpty(imageThumbnailString)) {
                contentImage.setImageResource(R.color.color_on_secondary);
                secondImage.setImageResource(R.color.color_on_secondary);
            } else {
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
                                int darkMutedColor = palette.getDarkMutedColor(mLightMutedColor);
                                contentTitle.setTextColor(darkMutedColor);
                            }

                            @Override
                            public void onError(Exception e) {

                            }
                        });
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

                if (typeString.equals("artworks")) {
                    Artwork artwork = new Artwork();
                    Log.d(TAG, "artworks detail");

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

    /*private ImageLinks getImageLinks(ShowContent showContent) {
        LinksResult links = showContent.getLinks();
        ImagesObject images = links.getImages();


        ImageLinks imageLinksObject = showContent.getLinks();
        MainImage mainImageObject = imageLinksObject.getImage();
        if (showContent.getImageVersions() != null) {
            List<String> imageVersionList = showContent.getImageVersions();
            // Get the link for the current artwork,
            // e.g.: "https://d32dm0rphc51dk.cloudfront.net/rqoQ0ln0TqFAf7GcVwBtTw/{image_version}.jpg"
            String artworkImgLinkString = mainImageObject.getHref();
            // Replace the {image_version} from the artworkImgLinkString with
            // the wanted version, e.g. "large"
            largeArtworkLink = extractImageLink(getVersionImage(imageVersionList, IMAGE_LARGE), artworkImgLinkString);
            // Set the large image with Picasso
            Picasso.get()
                    .load(Uri.parse(largeArtworkLink))
                    .placeholder(R.color.color_on_surface)
                    .error(R.color.color_error)
                    .into(artworkImage);

            String squareImage = extractImageLink(getVersionImage(imageVersionList, IMAGE_SQUARE), artworkImgLinkString);
            makeImageBlurry(squareImage);
            // Set a click listener on the image
            openArtworkFullScreen();
        }
        return imageLinksObject;
    }*/

    private String extractImageLink(String stringFinal, String stringFull) {
        return stringFull.replaceAll("\\{.*?\\}", stringFinal);
    }


    private void setupArtworkInfoUi(Artwork currentArtwork, String emptyField) {
        String artworkTitle = currentArtwork.getTitle();
        artworkNameTextView.setText(Utils.isNullOrEmpty(artworkTitle) ? emptyField : artworkTitle);
        collapsingToolbarLayout.setTitle(artworkTitle);
        collapsingToolbarLayout.setCollapsedTitleTextColor(getResources().getColor(R.color.color_primary));

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
        if (Utils.isNullOrEmpty(addInfo)) { // hide the Additional Information if the field is empty
            artworkInfoMarkdown.setVisibility(View.GONE);
            artworkInfoLabel.setVisibility(View.GONE);
        } else {
            artworkInfoMarkdown.loadMarkdown(addInfo); // load the markdown text
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

//        ImageLinks imageLinksObject = getImageLinks(showContent);
    }

    private void initArtistContentViewModel(String receivedArtistUrlString) {
        mArtistViewModel = new ViewModelProvider(this).get(ArtistsDetailViewModel.class);
        mArtistViewModel.initArtistData(receivedArtistUrlString);

        mArtistViewModel.getArtistData().observe(this, artists -> {
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

        ImageLinks imageLinks = currentArtist.getLinks();
        ArtworksLink artworksLink = imageLinks.getArtworksLink();
        String href = artworksLink.getHref();
        Log.d(TAG, "temp, href of artworks: " + href);
        initArtworksByArtistsViewModel(href);
    }

    private void initArtworksByArtistsViewModel(String artworksLink) {
        mArtistViewModel.initArtworksByArtistData(artworksLink);
        mArtistViewModel.getArtworksByArtistsData().observe(this, this::setupArtworksByArtist);
    }

    private void setupArtworksByArtist(List<Artwork> artworksList) {
        ArtworksByArtistAdapter artworksByArtist = new ArtworksByArtistAdapter(artworksList, null);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        artworksByArtistRv.setLayoutManager(gridLayoutManager);
        artworksByArtistRv.setAdapter(artworksByArtist);
    }
}
