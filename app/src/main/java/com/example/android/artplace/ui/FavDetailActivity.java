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

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.artplace.R;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.postprocessors.IterativeBoxBlurPostProcessor;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.facebook.imagepipeline.request.Postprocessor;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import butterknife.BindView;
import butterknife.ButterKnife;
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

        if (getIntent().getExtras() != null) {

            String favIdString = getIntent().getStringExtra(ARTWORK_ID_KEY);
            String favTitleString = getIntent().getStringExtra(ARTWORK_TITLE_KEY);
            String favArtistString = getIntent().getStringExtra(ARTWORK_ARTIST_KEY);
            String favDateString = getIntent().getStringExtra(ARTWORK_DATE_KEY);
            String favImageString = getIntent().getStringExtra(ARTWORK_IMAGE_KEY);
            String favCategoryString = getIntent().getStringExtra(ARTWORK_CATEGORY_KEY);
            String favMediumString = getIntent().getStringExtra(ARTWORK_MEDIUM_KEY);
            String favMuseumString = getIntent().getStringExtra(ARTWORK_MUSEUM_KEY);

            collapsingToolbarLayout.setTitle(favTitleString);

            favTitle.setText(favTitleString);
            favArtist.setText(favArtistString);
            favDate.setText(favDateString);
            favMedium.setText(favMediumString);
            favMuseum.setText(favMuseumString);
            favCategory.setText(favCategoryString);

            // Initialize Blur Post Processor
            // Tutorial:https://android.jlelse.eu/android-image-blur-using-fresco-vs-picasso-ea095264abbf
            mPostprocessor = new BlurPostprocessor(this, 20);

            // Instantiate Image Request using Post Processor as parameter
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
                    .error(R.drawable.placeholder)
                    .placeholder(R.drawable.placeholder)
                    .into(favImage);

            // Show CoordinatorLayout title only when collapsed
            // source: https://stackoverflow.com/a/32724422/8132331
            /*appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
                int scrollRange = -1;
                boolean isShown = true;

                @Override
                public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                    if (scrollRange == -1) {
                        scrollRange = appBarLayout.getTotalScrollRange();
                    }

                    if (scrollRange + verticalOffset == 0) {
                        collapsingToolbarLayout.setTitle(favTitleString);
                        isShown = true;
                    } else if (isShown) {
                        collapsingToolbarLayout.setTitle("");
                        isShown = false;
                    }
                }
            });*/

        }
    }
}
