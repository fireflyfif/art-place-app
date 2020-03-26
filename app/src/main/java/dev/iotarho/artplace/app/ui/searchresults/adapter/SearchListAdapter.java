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

import android.os.Parcelable;
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
import dev.iotarho.artplace.app.callbacks.OnRefreshListener;
import dev.iotarho.artplace.app.callbacks.OnResultClickListener;
import dev.iotarho.artplace.app.model.Thumbnail;
import dev.iotarho.artplace.app.model.search.LinksResult;
import dev.iotarho.artplace.app.model.search.Result;
import dev.iotarho.artplace.app.ui.NetworkStateItemViewHolder;
import dev.iotarho.artplace.app.utils.NetworkState;
import dev.iotarho.artplace.app.utils.Utils;

public class SearchListAdapter extends PagedListAdapter<Result, RecyclerView.ViewHolder> implements Filterable {

    private static final int TYPE_PROGRESS = 0;
    private static final int TYPE_ITEM = 1;

    private NetworkState mNetworkState;
    private OnRefreshListener mRefreshHandler;
    private OnResultClickListener mClickHandler;

    private List<Result> resultList;
    private Result objectResult;

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
            return new SearchResultViewHolder(view, mClickHandler);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof SearchResultViewHolder) {
            if (getItem(position) != null) {
                Result resultItem = resultList.get(position);
                ((SearchResultViewHolder) holder).bindTo(resultItem, position);
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

    @Override
    public int getItemCount() {
        if (resultList != null) {
            return resultList.size();
        }
        return super.getItemCount();
    }

    @Override
    public void onCurrentListChanged(@Nullable PagedList<Result> previousList, @Nullable PagedList<Result> currentList) {
        super.onCurrentListChanged(previousList, currentList);

        resultList = currentList;
        notifyDataSetChanged();
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

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                List<Result> filteredList = new ArrayList<>();
                FilterResults filterResults = new FilterResults();

                resultList = getCurrentList();

                for (Result currentResult : resultList) {
                    LinksResult linksResult = currentResult.getLinks();
                    Thumbnail thumbnail = linksResult.getThumbnail();
                    String thumbnailLink = thumbnail.getHref();

                    if (Utils.isNullOrEmpty(thumbnailLink) || thumbnailLink.equals(Thumbnail.NO_IMAGE)) {
                        filteredList.remove(currentResult);
                        Log.d("SearchListAdapter", "Removing an item without a thumbnail: " + currentResult.getTitle());
                        resultList = filteredList;
                    } else  {
                        filteredList.add(currentResult);
                        resultList = filteredList;
                    }
                }
                filterResults.values = filteredList;
                filterResults.count = filteredList.size();
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                resultList = (List<Result>) results.values;
                Log.d("SearchListAdapter", "Filtered list, publishResults: " + results.count);
                updateList(resultList);
            }
        };
    }

    public void updateList(List<Result> resultList) {
        this.resultList = resultList;
        Log.d("SearchListAdapter", "updateList, resultList: " + resultList.size() );
        notifyDataSetChanged();
    }
}
