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

package dev.iotarho.artplace.app.ui.favorites.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.paging.PagedList;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import dev.iotarho.artplace.app.R;
import dev.iotarho.artplace.app.callbacks.OnFavItemClickListener;
import dev.iotarho.artplace.app.database.entity.FavoriteArtworks;
import dev.iotarho.artplace.app.utils.Utils;

public class FavArtworkListAdapter extends PagedListAdapter<FavoriteArtworks, RecyclerView.ViewHolder> {

    private List<FavoriteArtworks> mFavArtworkList;

    private OnFavItemClickListener mCallback;

    public FavArtworkListAdapter(OnFavItemClickListener onFavItemClickListener) {
        super(FavoriteArtworks.DIFF_CALLBACK);
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

    public FavoriteArtworks getFavoriteAtPosition(int position) {
        return mFavArtworkList.get(position);
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

    public class FavArtworksViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.fav_artwork_thumbnail)
        ImageView favThumbnail;

        @BindView(R.id.fav_artwork_title)
        TextView favTitle;

        public FavArtworksViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(this);
        }

        public void bindTo(FavoriteArtworks favArtwork) {
            if (favArtwork != null) {
                String favTitleString = favArtwork.getArtworkTitle();
                favTitle.setText(favTitleString);

                if (!Utils.isNullOrEmpty(favArtwork.getArtworkImagePath())) {
                    String favThumbnailPathString = favArtwork.getArtworkImagePath();
                    Picasso.get()
                            .load(Uri.parse(favThumbnailPathString))
                            .placeholder(R.color.color_primary)
                            .error(R.color.color_error)
                            .into(favThumbnail);
                } else {
                    favThumbnail.setImageResource(R.color.color_primary);
                }
            }
        }

        @Override
        public void onClick(View v) {
            FavoriteArtworks favArtwork = getItem(getAdapterPosition());
            mCallback.onFavItemClick(favArtwork);
        }
    }
}
