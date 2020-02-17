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

package dev.iotarho.artplace.app.ui;

import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.facebook.imagepipeline.request.Postprocessor;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import dev.iotarho.artplace.app.R;
import jp.wasabeef.fresco.processors.BlurPostprocessor;

public class FavDetailActivity extends AppCompatActivity {

    private static final String ARTWORK_ID_KEY = "artwork_id";
    private static final String ARTWORK_TITLE_KEY = "artwork_title";
    private static final String ARTWORK_ARTIST_KEY = "artwork_artist";
    private static final String ARTWORK_CATEGORY_KEY = "artwork_category";
    private static final String ARTWORK_MEDIUM_KEY = "artwork_medium";
    private static final String ARTWORK_DATE_KEY = "artwork_date";
    private static final String ARTWORK_MUSEUM_KEY = "artwork_museum";
    private static final String ARTWORK_IMAGE_KEY = "artwork_image";
    private static final String ARTWORK_DIMENS_CM_KEY = "artwork_dimens_cm";
    private static final String ARTWORK_DIMENS_INCH_KEY = "artwork_dimens_inch";

    @BindView(R.id.fav_detail_appbar)
    AppBarLayout appBarLayout;
    @BindView(R.id.fav_detail_toolbar)
    Toolbar toolbar;
    @BindView(R.id.fav_detail_collapsing_toolbar)
    CollapsingToolbarLayout collapsingToolbarLayout;
    @BindView(R.id.fav_detail_coordinator)
    CoordinatorLayout coordinatorLayout;
    @BindView(R.id.fav_detail_artwork_image)
    ImageView favImage;
    @BindView(R.id.fav_detail_title)
    TextView favTitle;
    @BindView(R.id.fav_detail_artist)
    TextView favArtist;
    @BindView(R.id.fav_detail_date)
    TextView favDate;
    @BindView(R.id.fav_detail_category)
    TextView favCategory;
    @BindView(R.id.fav_detail_medium)
    TextView favMedium;
    @BindView(R.id.fav_detail_museum)
    TextView favMuseum;
    @BindView(R.id.blurry_image)
    SimpleDraweeView blurryImage;
    @BindView(R.id.fav_artwork_dimens_cm)
    TextView favDimensCm;
    @BindView(R.id.fav_artwork_dimens_in)
    TextView favDimensIn;

    private Postprocessor mPostprocessor;
    private ImageRequest mImageRequest;
    private PipelineDraweeController mController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize Fresco
        Fresco.initialize(this);
        setContentView(R.layout.activity_fav_detail);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if (toolbar != null) {
            toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.color_primary),
                    PorterDuff.Mode.SRC_ATOP);
        }

        if (getIntent().getExtras() != null) {

            String favIdString = getIntent().getStringExtra(ARTWORK_ID_KEY);
            String favTitleString = getIntent().getStringExtra(ARTWORK_TITLE_KEY);
            String favArtistString = getIntent().getStringExtra(ARTWORK_ARTIST_KEY);
            String favDateString = getIntent().getStringExtra(ARTWORK_DATE_KEY);
            String favImageString = getIntent().getStringExtra(ARTWORK_IMAGE_KEY);
            String favCategoryString = getIntent().getStringExtra(ARTWORK_CATEGORY_KEY);
            String favMediumString = getIntent().getStringExtra(ARTWORK_MEDIUM_KEY);
            String favMuseumString = getIntent().getStringExtra(ARTWORK_MUSEUM_KEY);
            String favDimensInchString = getIntent().getStringExtra(ARTWORK_DIMENS_INCH_KEY);
            String favDimensCmString = getIntent().getStringExtra(ARTWORK_DIMENS_CM_KEY);

            collapsingToolbarLayout.setTitle(favTitleString);
            collapsingToolbarLayout.setCollapsedTitleTextColor(getResources().getColor(R.color.color_primary));

            favTitle.setText(favTitleString);
            favArtist.setText(favArtistString);
            favDate.setText(favDateString);
            favMedium.setText(favMediumString);
            favMuseum.setText(favMuseumString);
            favCategory.setText(favCategoryString);
            favDimensIn.setText(favDimensInchString);
            favDimensCm.setText(favDimensCmString);

            // Initialize Blur Post Processor
            // Tutorial:https://android.jlelse.eu/android-image-blur-using-fresco-vs-picasso-ea095264abbf
            mPostprocessor = new BlurPostprocessor(this, 20);

            // Instantiate Image Request using Post Processor as parameter
            if (favImageString == null || favImageString.isEmpty()) {
                Picasso.get()
                        .load(R.color.color_primary)
                        .error(R.color.color_error)
                        .placeholder(R.color.color_primary)
                        .into(favImage);

            } else {

                mImageRequest = ImageRequestBuilder.newBuilderWithSource(Uri.parse(favImageString))
                        .setPostprocessor(mPostprocessor)
                        .build();

                // Instantiate Controller
                mController = (PipelineDraweeController) Fresco.newDraweeControllerBuilder()
                        .setImageRequest(mImageRequest)
                        .setOldController(blurryImage.getController())
                        .build();

                // Load the blurred image
                blurryImage.setController(mController);

                Picasso.get()
                        .load(Uri.parse(favImageString))
                        .error(R.color.color_error)
                        .placeholder(R.color.color_primary)
                        .into(favImage);
            }

        }
    }
}
