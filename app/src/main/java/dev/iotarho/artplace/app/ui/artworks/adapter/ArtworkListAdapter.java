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

package dev.iotarho.artplace.app.ui.artworks.adapter;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.paging.PagedList;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import dev.iotarho.artplace.app.R;
import dev.iotarho.artplace.app.callbacks.OnArtworkClickListener;
import dev.iotarho.artplace.app.callbacks.OnRefreshListener;
import dev.iotarho.artplace.app.model.artworks.Artwork;
import dev.iotarho.artplace.app.ui.NetworkStateItemViewHolder;
import dev.iotarho.artplace.app.utils.NetworkState;
import dev.iotarho.artplace.app.utils.Utils;

// Help from tutorial: https://proandroiddev.com/8-steps-to-implement-paging-library-in-android-d02500f7fffe
public class ArtworkListAdapter extends PagedListAdapter<Artwork, RecyclerView.ViewHolder> implements Filterable {

    private static final String TAG = ArtworkListAdapter.class.getSimpleName();

    private static final int TYPE_PROGRESS = 0;
    private static final int TYPE_ITEM = 1;

    private NetworkState mNetworkState;

    private List<Artwork> mArtworkList;
    private OnArtworkClickListener mClickHandler;
    private OnRefreshListener mRefreshHandler;


    public ArtworkListAdapter(OnArtworkClickListener clickHandler, OnRefreshListener onRefreshListener) {
        super(Artwork.DIFF_CALLBACK);
        mClickHandler = clickHandler;
        mRefreshHandler = onRefreshListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());

        if (viewType == TYPE_PROGRESS) {
            View view = layoutInflater.inflate(R.layout.network_state_item, parent, false);
            return new NetworkStateItemViewHolder(view, mRefreshHandler);

        } else {
            View view = layoutInflater.inflate(R.layout.artwork_item, parent, false);
            return new ArtworkItemViewHolder(view, mClickHandler);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ArtworkItemViewHolder) {
            // This gets the Item from the PagedList
            if (getItem(position) != null) {
                Artwork currentArtwork = mArtworkList.get(position);
                ((ArtworkItemViewHolder) holder).bindTo(currentArtwork);
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
        if (hasExtraRow() && position == getItemCount() - 1) {
            return TYPE_PROGRESS;
        } else {
            return TYPE_ITEM;
        }
    }

    // Implement this method, as without the filtered list is producing an error
    @Override
    public int getItemCount() {
        if (mArtworkList != null) {
            return mArtworkList.size();
        }
        return super.getItemCount();
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
        } else if (newExtraRow && previousState != newNetworkState) {
            notifyItemChanged(getItemCount() - 1);
        }
    }

    @Override
    public void onCurrentListChanged(@Nullable PagedList<Artwork> previousList, @Nullable PagedList<Artwork> currentList) {
        super.onCurrentListChanged(previousList, currentList);

        mArtworkList = currentList;
        notifyDataSetChanged();
    }

    /*@Override
    public void onCurrentListChanged(@Nullable PagedList<Artwork> currentList) {
        super.onCurrentListChanged(currentList);

        mArtworkList = currentList;
        notifyDataSetChanged();
    }*/

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String queryWord = constraint.toString().toLowerCase();
                List<Artwork> filteredList = new ArrayList<>();

                FilterResults filterResults = new FilterResults();

                if (Utils.isNullOrEmpty(queryWord)) {
                    mArtworkList = getCurrentList();
                    filterResults.values = mArtworkList;
                    if (mArtworkList != null) {
                        filterResults.count = mArtworkList.size();
                    }
                } else {
                    // Add a constrain for the artist name and the category
                    for (Artwork currentArtwork : mArtworkList) {

                        String artworkTitle = currentArtwork.getTitle().toLowerCase();
                        String artworkArtist = currentArtwork.getSlug().toLowerCase();
                        String artworkCategory = currentArtwork.getCategory().toLowerCase();

                        if (artworkTitle.contains(queryWord) || artworkArtist.contains(queryWord)
                                || artworkCategory.contains(queryWord)) {
                            filteredList.add(currentArtwork);
                            mArtworkList = filteredList;
                        }
                    }
                    filterResults.values = filteredList;
                    filterResults.count = filteredList.size();
                    Log.d(TAG, "Filtered list: " + filteredList.size());
                }

                return filterResults;
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                mArtworkList = (List<Artwork>) results.values;
                //results.count = mArtworkList.size();
                Log.d(TAG, "Filtered list, publishResults: " + results.count);

                swapCatalogue(mArtworkList);
            }
        };
    }

    public void swapCatalogue(List<Artwork> artworkList) {
        mArtworkList = artworkList;
        notifyDataSetChanged();
    }
}
