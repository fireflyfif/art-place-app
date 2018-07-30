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

package com.example.android.artplace.ui.favoriteArtworks.adapter;

import android.arch.paging.PagedList;
import android.arch.paging.PagedListAdapter;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.artplace.R;
import com.example.android.artplace.callbacks.OnFavItemClickListener;
import com.example.android.artplace.database.entity.FavoriteArtworks;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FavArtworkListAdapter extends PagedListAdapter<FavoriteArtworks, RecyclerView.ViewHolder> {

    private Context mContext;
    private List<FavoriteArtworks> mFavArtworkList;

    private OnFavItemClickListener mCallback;

    public FavArtworkListAdapter(Context context, OnFavItemClickListener onFavItemClickListener) {
        super(FavoriteArtworks.DIFF_CALLBACK);
        mContext = context;
        mCallback = onFavItemClickListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.fav_artwork_item, parent, false);

        return new FavArtworksViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if (getItem(position) != null) {
            ((FavArtworksViewHolder) holder).bindTo(mFavArtworkList.get(position));
        }
    }

    @Override
    public int getItemCount() {
        if (mFavArtworkList != null) {
            return mFavArtworkList.size();
        }

        return super.getItemCount();
    }

    @Override
    public void onCurrentListChanged(@Nullable PagedList<FavoriteArtworks> currentList) {
        mFavArtworkList = currentList;

        notifyDataSetChanged();

        super.onCurrentListChanged(currentList);
    }

    public void swapData(List<FavoriteArtworks> favArtworkList) {
        mFavArtworkList = favArtworkList;
        notifyDataSetChanged();
    }

    public class FavArtworksViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.fav_artwork_thumbnail)
        ImageView favThumbnail;
        @BindView(R.id.fav_artwork_title)
        TextView favTitle;
        @BindView(R.id.fav_artwork_category)
        TextView favCategory;
        @BindView(R.id.fav_artwork_medium)
        TextView favMedium;
        @BindView(R.id.fav_artwork_year)
        TextView favYear;

        public FavArtworksViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }

        public void bindTo(FavoriteArtworks favArtwork) {
            if (favArtwork != null) {
                String favTitleString = favArtwork.getArtworkTitle();
                favTitle.setText(favTitleString);

                String favCategoryString = favArtwork.getArtworkCategory();
                favCategory.setText(favCategoryString);

                String favMediumString = favArtwork.getArtworkMedium();
                favMedium.setText(favMediumString);

                String favYearString = favArtwork.getArtworkDate();
                favYear.setText(favYearString);

                String favThumbnailPathString = favArtwork.getArtworkThumbnailPath();
                Picasso.get()
                        .load(Uri.parse(favThumbnailPathString))
                        .placeholder(R.drawable.movie_video_02)
                        .error(R.drawable.movie_video_02)
                        .into(favThumbnail);
            }
        }
    }
}
