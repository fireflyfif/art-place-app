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

package dev.iotarho.artplace.app.ui.searchresults.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import dev.iotarho.artplace.app.R;
import dev.iotarho.artplace.app.callbacks.OnRefreshListener;
import dev.iotarho.artplace.app.callbacks.OnResultClickListener;
import dev.iotarho.artplace.app.model.Thumbnail;
import dev.iotarho.artplace.app.model.search.LinksResult;
import dev.iotarho.artplace.app.model.search.Result;
import dev.iotarho.artplace.app.ui.NetworkStateItemViewHolder;
import dev.iotarho.artplace.app.utils.NetworkState;

public class SearchListAdapter extends PagedListAdapter<Result, RecyclerView.ViewHolder> {

    private static final String TAG = SearchListAdapter.class.getSimpleName();

    private static final int TYPE_PROGRESS = 0;
    private static final int TYPE_ITEM = 1;

    private NetworkState mNetworkState;
    private OnRefreshListener mRefreshHandler;
    private OnResultClickListener mClickHandler;

    public SearchListAdapter(OnResultClickListener clickHandler, OnRefreshListener refreshListener) {
        super(Result.DIFF_CALLBACK);
        mClickHandler = clickHandler;
        mRefreshHandler = refreshListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());

        if (viewType == TYPE_PROGRESS) {
            View view = layoutInflater.inflate(R.layout.network_state_item, parent, false);
            return new NetworkStateItemViewHolder(view, mRefreshHandler);
        } else {
            View view = layoutInflater.inflate(R.layout.search_result_item, parent, false);
            return new SearchResultViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof SearchResultViewHolder) {
            if (getItem(position) != null) {
                ((SearchResultViewHolder) holder).bindTo(getItem(position), position);
            }
        } else {
            ((NetworkStateItemViewHolder) holder).bindView(mNetworkState);
        }
    }

    private boolean hasExtraRow() {
        return mNetworkState != null && mNetworkState != NetworkState.LOADED;
    }

    @Override
    public int getItemViewType(int position) {
        if (hasExtraRow() && position == getItemCount() -1) {
            return TYPE_PROGRESS;
        } else {
            return TYPE_ITEM;
        }
    }

    public void setNetworkState(NetworkState newNetworkState) {
        NetworkState previousState = mNetworkState;
        boolean previousExtraRow = hasExtraRow();
        mNetworkState = newNetworkState;
        boolean newExtraRow = hasExtraRow();

        if (previousExtraRow != newExtraRow) {
            if (previousExtraRow) {
                notifyItemRemoved(getItemCount());
            } else {
                notifyItemInserted(getItemCount());
            }
        } else if (newExtraRow && previousState != newNetworkState){
            notifyItemChanged(getItemCount() - 1);
        }
    }

    public class SearchResultViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

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
            itemView.setOnClickListener(this);
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
                                    .load(R.color.color_primary)
                                    .placeholder(R.color.color_primary)
                                    .error(R.color.color_error)
                                    .into(searchThumbnail);
                        } else {
                            // If it's not empty -> load the image
                            Picasso.get()
                                    .load(thumbnailPathString)
                                    .placeholder(R.color.color_primary)
                                    .error(R.color.color_error)
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

        @Override
        public void onClick(View v) {
            Result currentResult = getItem(getAdapterPosition());
            mClickHandler.onResultClick(currentResult);
        }
    }
}
