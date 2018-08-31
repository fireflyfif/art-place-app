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

package com.example.android.artplace.ui.searchresults.adapter;

import android.arch.paging.PagedListAdapter;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.artplace.R;
import com.example.android.artplace.model.Thumbnail;
import com.example.android.artplace.model.search.LinksResult;
import com.example.android.artplace.model.search.Result;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SearchListAdapter extends PagedListAdapter<Result, RecyclerView.ViewHolder> {

    private static final String TAG = SearchListAdapter.class.getSimpleName();

    private Context mContext;

    public SearchListAdapter(Context context) {
        super(Result.DIFF_CALLBACK);
        mContext = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());

        View view = layoutInflater.inflate(R.layout.search_result_item, parent, false);


        return new SearchResultViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof SearchResultViewHolder) {
            if (getItem(position) != null) {
                ((SearchResultViewHolder) holder).bindTo(getItem(position), position);
            }
        }
    }

    public class SearchResultViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.search_cardview)
        CardView cardView;
        @BindView(R.id.search_thumbnail)
        ImageView searchThumbnail;
        @BindView(R.id.search_title)
        TextView searchTitle;
        @BindView(R.id.search_type)
        TextView searchType;

        public SearchResultViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }

        private void bindTo(Result result, int position) {

            if (result != null) {

                if (result.getLinks() != null) {
                    LinksResult linksResult = result.getLinks();

                    if (linksResult.getThumbnail() != null) {
                        Thumbnail thumbnail = linksResult.getThumbnail();
                        String thumbnailPathString = thumbnail.getHref();
                        Log.d(TAG, "Current thumbnail string: " + thumbnailPathString);

                        if (thumbnailPathString == null || thumbnailPathString.isEmpty()) {
                            // If it's empty or null -> set the placeholder
                            Picasso.get()
                                    .load(R.drawable.placeholder)
                                    .placeholder(R.drawable.placeholder)
                                    .error(R.drawable.placeholder)
                                    .into(searchThumbnail);
                        } else {
                            // If it's not empty -> load the image
                            Picasso.get()
                                    .load(thumbnailPathString)
                                    .placeholder(R.drawable.placeholder)
                                    .error(R.drawable.placeholder)
                                    .into(searchThumbnail);
                        }

                    }

                }


                String titleString = result.getTitle();
                searchTitle.setText(titleString);
                Log.d(TAG, "Current search title: " + titleString);

                String typeString = result.getType();
                searchType.setText(typeString);
                Log.d(TAG, "Current search type: " + typeString);
            }

        }
    }
}