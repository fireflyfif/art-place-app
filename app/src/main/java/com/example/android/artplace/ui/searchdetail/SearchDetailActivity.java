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

package com.example.android.artplace.ui.searchdetail;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;


import com.example.android.artplace.R;
import com.example.android.artplace.model.Thumbnail;
import com.example.android.artplace.model.search.LinksResult;
import com.example.android.artplace.model.search.Result;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SearchDetailActivity extends AppCompatActivity {

    private static final String RESULT_PARCEL_KEY = "results_key";
    private static final String TAG = SearchDetailActivity.class.getSimpleName();

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

    private Result mResults;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_detail);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if (getIntent().getExtras() != null) {
            if (getIntent().hasExtra(RESULT_PARCEL_KEY)) {
                Bundle receivedBundle = getIntent().getExtras();
                mResults = receivedBundle.getParcelable(RESULT_PARCEL_KEY);

                if (mResults != null) {
                    String titleString = mResults.getTitle();
                    String typeString = mResults.getType();
                    String descriptionString = mResults.getDescription();
                    Log.d(TAG, "Title: " + titleString + "\nType: " + typeString + "\nDescription: " + descriptionString);

                    contentTitle.setText(titleString);
                    contentType.setText(typeString);
                    contentDescription.setText(descriptionString);

                    if (mResults.getLinks() != null) {
                        LinksResult linksResult = mResults.getLinks();

                        if (linksResult.getThumbnail() != null) {
                            Thumbnail thumbnail = linksResult.getThumbnail();
                            String imageThumbnailString = thumbnail.getHref();

                            if (imageThumbnailString != null || imageThumbnailString.isEmpty()) {
                                // Set the backdrop image
                                Picasso.get()
                                        .load(imageThumbnailString)
                                        .placeholder(R.color.colorPrimary)
                                        .error(R.color.colorPrimary)
                                        .into(contentImage);

                                // Set the second image
                                Picasso.get()
                                        .load(imageThumbnailString)
                                        .placeholder(R.color.colorPrimary)
                                        .error(R.color.colorPrimary)
                                        .into(secondImage);
                            } else {
                                // Set the backdrop image
                                Picasso.get()
                                        .load(R.color.colorPrimary)
                                        .placeholder(R.color.colorPrimary)
                                        .error(R.color.colorPrimary)
                                        .into(contentImage);

                                // Set the second image
                                Picasso.get()
                                        .load(imageThumbnailString)
                                        .placeholder(R.color.colorPrimary)
                                        .error(R.color.colorPrimary)
                                        .into(secondImage);
                            }

                        }

                    }
                }
            }
        }
    }
}
