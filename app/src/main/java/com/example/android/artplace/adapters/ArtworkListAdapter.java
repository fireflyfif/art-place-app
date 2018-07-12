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

package com.example.android.artplace.adapters;

import android.arch.paging.PagedListAdapter;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.artplace.R;
import com.example.android.artplace.model.Artwork;
import com.example.android.artplace.model.ImageLinks;
import com.example.android.artplace.model.Thumbnail;
import com.example.android.artplace.utils.NetworkState;
import com.squareup.picasso.Picasso;

import java.util.List;

// Help from tutorial: https://proandroiddev.com/8-steps-to-implement-paging-library-in-android-d02500f7fffe
public class ArtworkListAdapter extends PagedListAdapter<Artwork, RecyclerView.ViewHolder> {

    private static final String TAG = ArtworkListAdapter.class.getSimpleName();

    private static final int TYPE_PROGRESS = 0;
    private static final int TYPE_ITEM = 1;

    private Context mContext;
    private NetworkState mNetworkState;
    private List<Artwork> mArtworkList;

    public ArtworkListAdapter(Context context) {
        super(Artwork.DIFF_CALLBACK);
        mContext = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.artwork_item, parent, false);

        return new ArtworkItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if (getItem(position) != null) {
            ((ArtworkItemViewHolder)holder).bindTo(mArtworkList.get(position));
        }

    }

    @Override
    public int getItemCount() {
        return super.getItemCount();
    }

    private boolean hasExtraRow() {
        if (mNetworkState != null && mNetworkState != NetworkState.LOADED) {
            return  true;
        } else {
            return false;
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

    public class ArtworkItemViewHolder extends RecyclerView.ViewHolder {

        private ImageView artworkThumbnail;
        private TextView artworkTitle;

        public ArtworkItemViewHolder(View itemView) {
            super(itemView);

            artworkThumbnail = itemView.findViewById(R.id.artwork_thumbnail);
            artworkTitle = itemView.findViewById(R.id.artwork_title);
        }

        public void bindTo(Artwork artwork) {

            // Get the thumbnail from the json tree
            ImageLinks currentImageLink = artwork.getLinks();
            Thumbnail currentThumbnail = currentImageLink.getThumbnail();

            String artworkThumbnailString = currentThumbnail.getHref();
            String artworkTitleString = artwork.getTitle();
            Log.d(TAG, "Artwork Title: " + artworkTitle);

            artworkTitle.setText(artworkTitleString);

            Picasso.get()
                    .load(Uri.parse(artworkThumbnailString))
                    .into(artworkThumbnail);
        }
    }
}
