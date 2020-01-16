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

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.paging.PagedList;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import dev.iotarho.artplace.app.R;
import dev.iotarho.artplace.app.callbacks.OnArtworkClickListener;
import dev.iotarho.artplace.app.callbacks.OnRefreshListener;
import dev.iotarho.artplace.app.model.ImageLinks;
import dev.iotarho.artplace.app.model.Thumbnail;
import dev.iotarho.artplace.app.model.artworks.Artwork;
import dev.iotarho.artplace.app.model.artworks.MainImage;
import dev.iotarho.artplace.app.utils.NetworkState;
import dev.iotarho.artplace.app.utils.StringUtils;

// Help from tutorial: https://proandroiddev.com/8-steps-to-implement-paging-library-in-android-d02500f7fffe
public class ArtworkListAdapter extends PagedListAdapter<Artwork, RecyclerView.ViewHolder>
        implements Filterable {

    private static final String TAG = ArtworkListAdapter.class.getSimpleName();

    private static final int TYPE_PROGRESS = 0;
    private static final int TYPE_ITEM = 1;

    private Context mContext;
    private NetworkState mNetworkState;

    private List<Artwork> mArtworkList;

    private OnArtworkClickListener mClickHandler;
    private OnRefreshListener mRefreshHandler;

    private int mMutedColor = 0xFF333333;
    private int mLightMutedColor = 0xFFAAAAAA;
    private int mLastPosition = -1;


    public ArtworkListAdapter(Context context, OnArtworkClickListener clickHandler,
                              OnRefreshListener onRefreshListener) {
        super(Artwork.DIFF_CALLBACK);
        mContext = context;
        mClickHandler = clickHandler;
        mRefreshHandler = onRefreshListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());

        if (viewType == TYPE_PROGRESS) {
            View view = layoutInflater.inflate(R.layout.network_state_item, parent, false);
            return new NetworkStateItemViewHolder(view);

        } else {
            View view = layoutInflater.inflate(R.layout.artwork_item, parent, false);
            return new ArtworkItemViewHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        //position = holder.getAdapterPosition();
        Log.d(TAG, "Get the position from onBindViewHolder(): " + position);
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
    public void onCurrentListChanged(@Nullable PagedList<Artwork> currentList) {
        super.onCurrentListChanged(currentList);

        mArtworkList = currentList;
        notifyDataSetChanged();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String queryWord = constraint.toString().toLowerCase();
                List<Artwork> filteredList = new ArrayList<>();

                FilterResults filterResults = new FilterResults();

                if (TextUtils.isEmpty(queryWord)) {
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


    public class ArtworkItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.artwork_cardview)
        CardView artworkCard;
        @BindView(R.id.artwork_thumbnail)
        ImageView artworkThumbnail;
        @BindView(R.id.artwork_title)
        TextView artworkTitle;
        @BindView(R.id.artwork_artist)
        TextView artworkArtist;

        private Artwork mCurrentItem; // allows this viewholder to keep a reference to the bound item

        private ArtworkItemViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(this);
        }

        private void bindTo(Artwork artwork) {

            // Important point from this SO post: https://stackoverflow.com/a/40749134/8132331
            mCurrentItem = artwork; // Keeps a reference to the current item

            // Get the thumbnail from the json tree
            if (artwork.getLinks() != null) {

                ImageLinks currentImageLink = artwork.getLinks();

                String artworkThumbnailString = null;
                // Handle case where no thumbnail is provided!!!
                try {
                    Thumbnail currentThumbnail = currentImageLink.getThumbnail();
                    artworkThumbnailString = currentThumbnail.getHref();

                    Log.d(TAG, "Link to the thumbnail: " + artworkThumbnailString);

                } catch (Exception e) {
                    Log.e(TAG, "Error obtaining thumbnail from the JSON: " + e.getMessage());
                }

                if (artworkThumbnailString == null) {

                    // Handle case where no "image_version" is provided by the JSON
                    if (artwork.getImageVersions() != null) {

                        List<String> imageVersionList = artwork.getImageVersions();

                        String versionMediumString;
                        // Check if the list with image version contains "medium"
                        if (imageVersionList.contains("medium") || imageVersionList.contains("medium_rectangle")) {

                            // Set the version to "medium"
                            versionMediumString = "medium";
                            Log.d(TAG, "Version of the image: " + versionMediumString);
                        } else {

                            // Set the sixth String from the version list
                            versionMediumString = imageVersionList.get(0);
                            Log.d(TAG, "Version of the image: " + versionMediumString);
                        }

                        MainImage mainImage = currentImageLink.getImage();
                        String artworkImgLinkString = mainImage.getHref();

                        // Replace the {image_version} from the artworkImgLinkString with
                        // the wanted version, e.g. "large"
                        artworkThumbnailString = artworkImgLinkString
                                .replaceAll("\\{.*?\\}", versionMediumString);

                        Log.d(TAG, "New link to the image: " + artworkThumbnailString);
                    }
                }

                Log.d(TAG, "artworkThumbnailString: " + artworkThumbnailString);

                // Get the title from the response
                String artworkTitleString = artwork.getTitle();
                artworkTitle.setText(artworkTitleString);

                // Extract the Artist name from the Slug
                String artistNameString = StringUtils.getArtistNameFromSlug(artwork);
                Log.d(TAG, "Name of the artist: " + artistNameString);
                artworkArtist.setText(artistNameString);

                // Set the image with Picasso
                // Check first if the url in null or empty
                if (artworkThumbnailString == null || artworkThumbnailString.isEmpty()) {

                    // If it's empty or null -> set the placeholder
                    Picasso.get()
                            .load(R.color.colorPrimary)
                            .placeholder(R.color.colorPrimary)
                            .error(R.color.colorPrimary)
                            .into(artworkThumbnail);
                } else {
                    // If it's not empty -> load the image
                    Picasso.get()
                            .load(Uri.parse(artworkThumbnailString))
                            .placeholder(R.color.colorPrimary)
                            .error(R.color.colorPrimary)
                            .into(artworkThumbnail, new Callback() {
                                @Override
                                public void onSuccess() {

                                    Bitmap bitmap = ((BitmapDrawable)
                                            artworkThumbnail.getDrawable()).getBitmap();
                                    artworkThumbnail.setImageBitmap(bitmap);

                                    // Set the item animator here
                                    //setItemAnimator(itemView, position);
                                }

                                @Override
                                public void onError(Exception e) {

                                }
                            });
                }
            }
        }

        @Override
        public void onClick(View view) {

            // Using the mCurrentItem make the position to update even after filtering the list
            mClickHandler.onArtworkClick(mCurrentItem, this.getLayoutPosition());
            Log.d(TAG, "onClick position: " + this.getLayoutPosition());
        }
    }

    private void setItemAnimator(View view, int position) {
        if (position > mLastPosition) {
            Animation animation = AnimationUtils
                    .loadAnimation(mContext, R.anim.item_animation_from_bottom);
            view.startAnimation(animation);
            mLastPosition = position;
        }
    }

    public class NetworkStateItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.network_state_layout)
        LinearLayout networkLayout;
        @BindView(R.id.network_state_pb)
        ProgressBar progressBar;
        @BindView(R.id.network_state_error_msg)
        TextView errorMessage;
        @BindView(R.id.network_state_refresh_bt)
        ImageButton refreshButton;

        private NetworkStateItemViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }

        private void bindView(NetworkState networkState) {

            if (networkState != null &&
                    networkState.getStatus() == NetworkState.Status.RUNNING) {
                networkLayout.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.VISIBLE);
                errorMessage.setVisibility(View.GONE);
                refreshButton.setVisibility(View.GONE);
            } else if (networkState != null &&
                    networkState.getStatus() == NetworkState.Status.SUCCESS) {
                networkLayout.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
                errorMessage.setVisibility(View.GONE);
                refreshButton.setVisibility(View.GONE);
            } else if (networkState != null &&
                    networkState.getStatus() == NetworkState.Status.FAILED) {
                networkLayout.setVisibility(View.VISIBLE);

                progressBar.setVisibility(View.GONE);
                errorMessage.setVisibility(View.VISIBLE);
                refreshButton.setVisibility(View.VISIBLE);
                // Set the click listener here
                refreshButton.setOnClickListener(this::onClick);
            } else {
                networkLayout.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
                errorMessage.setVisibility(View.GONE);
                refreshButton.setVisibility(View.GONE);
            }
        }

        @Override
        public void onClick(View v) {
            mRefreshHandler.onRefreshConnection();
        }
    }

}
