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

package app.app.android.artplace.ui.artworkdetail.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.palette.graphics.Palette;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import app.app.android.artplace.model.ImageLinks;
import app.app.android.artplace.model.Thumbnail;
import app.app.android.artplace.model.artworks.Artwork;
import app.app.android.artplace.utils.StringUtils;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import dev.iotarho.artplace.app.R;

public class SimilarArtworksAdapter extends RecyclerView.Adapter<SimilarArtworksAdapter.SimilarViewHolder> {

    private static final String TAG = SimilarArtworksAdapter.class.getSimpleName();
    private List<Artwork> mArtworkList;
    private Context mContext;

    private int mMutedColor = 0xFF333333;

    public SimilarArtworksAdapter(Context context, List<Artwork> artworkList) {
        mContext = context;
        mArtworkList = artworkList;
    }

    @NonNull
    @Override
    public SimilarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.similar_artwork_item, parent, false);

        return new SimilarViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SimilarViewHolder holder, int position) {
        Artwork artwork = mArtworkList.get(position);
        holder.bindTo(artwork);
    }

    @Override
    public int getItemCount() {
        if (mArtworkList == null) {
            return 0;
        }
        Log.d(TAG, "Size of the Similar Artworks list: " + mArtworkList);
        return mArtworkList.size();
    }

    public class SimilarViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.similar_artwork_thumbnail)
        ImageView similarArtworkThumbnail;
        @BindView(R.id.similar_artwork_bg)
        ImageView similarBackground;
        @BindView(R.id.similar_artwork_title)
        TextView similarTitle;
        @BindView(R.id.similar_artwork_artist)
        TextView similarArtist;

        public SimilarViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        private void bindTo(Artwork artwork) {
            if (artwork != null) {
                ImageLinks imageLinks = artwork.getLinks();
                String artworkThumbnailString = null;
                try {
                    Thumbnail thumbnail = imageLinks.getThumbnail();
                    artworkThumbnailString = thumbnail.getHref();
                } catch (Exception e) {
                    Log.e(TAG, "Error obtaining thumbnail from JSON: " + e.getMessage());
                }

                String similarTitleString = artwork.getTitle();
                similarTitle.setText(similarTitleString);

                String artistNameString = StringUtils.getArtistNameFromSlug(artwork);
                Log.d(TAG, "Name of the artist: " + artistNameString);
                similarArtist.setText(artistNameString);

                if (artworkThumbnailString == null || TextUtils.isEmpty(artworkThumbnailString)) {
                    // If it's empty or null -> set the placeholder
                    Picasso.get()
                            .load(R.color.colorPrimary)
                            .placeholder(R.color.colorPrimary)
                            .error(R.color.colorPrimary)
                            .into(similarArtworkThumbnail);
                } else {
                    // If it's not empty -> load the image
                    Picasso.get()
                            .load(Uri.parse(artworkThumbnailString))
                            .placeholder(R.color.colorPrimary)
                            .error(R.color.colorPrimary)
                            .into(similarArtworkThumbnail, new Callback() {
                                @Override
                                public void onSuccess() {
                                    Bitmap bitmap = ((BitmapDrawable) similarArtworkThumbnail
                                            .getDrawable()).getBitmap();
                                    similarArtworkThumbnail.setImageBitmap(bitmap);

                                    Palette palette = Palette.from(bitmap).generate();
                                    int generatedMutedColor = palette.getMutedColor(mMutedColor);

                                    similarBackground.setBackgroundColor(generatedMutedColor);
                                }

                                @Override
                                public void onError(Exception e) {

                                }
                            });
                }

            }
        }
    }
}
