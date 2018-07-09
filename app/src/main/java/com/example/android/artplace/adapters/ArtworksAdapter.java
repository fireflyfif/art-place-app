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
import com.example.android.artplace.model.Embedded;
import com.example.android.artplace.model.ImageLinks;
import com.example.android.artplace.model.Thumbnail;
import com.example.android.artplace.ui.MainActivity;
import com.squareup.picasso.Picasso;

import java.util.List;

import retrofit2.Callback;

public class ArtworksAdapter extends RecyclerView.Adapter<ArtworksAdapter.ArtworkViewHolder> {

    private static final String TAG = ArtworksAdapter.class.getSimpleName();

    private Callback<Embedded> mCallback;
    private Embedded mEmbedded;
    private List<Artwork> mArtworkList;

    public ArtworksAdapter(Callback<Embedded> callback, Embedded embedded, List<Artwork> artworkList) {
        mCallback = callback;
        mEmbedded = embedded;
        mArtworkList = artworkList;
    }

    @NonNull
    @Override
    public ArtworkViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.artwork_item, parent, false);

        return new ArtworkViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ArtworkViewHolder holder, int position) {

        Artwork currentArtwork = mArtworkList.get(position);

        // Get the thumbnail from the json tree
        ImageLinks currentImageLink = currentArtwork.getLinks();
        Thumbnail currentThumbnail = currentImageLink.getThumbnail();

        String artworkThumbnail = currentThumbnail.getHref();
        String artworkTitle = currentArtwork.getTitle();
        Log.d(TAG, "Artwork Title: " + artworkTitle);

        holder.artworkTitle.setText(artworkTitle);

        Picasso.get()
                .load(Uri.parse(artworkThumbnail))
                .into(holder.artworkThumbnail);
    }

    @Override
    public int getItemCount() {
        if (mArtworkList != null) {
            return mArtworkList.size();
        } else {
            return 0;
        }
    }

    public class ArtworkViewHolder extends RecyclerView.ViewHolder {

        ImageView artworkThumbnail;
        TextView artworkTitle;

        public ArtworkViewHolder(View itemView) {
            super(itemView);

            artworkThumbnail = itemView.findViewById(R.id.artwork_thumbnail);
            artworkTitle = itemView.findViewById(R.id.artwork_title);
        }
    }
}
