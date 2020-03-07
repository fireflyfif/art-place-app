package dev.iotarho.artplace.app.ui.artworks.adapter;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import dev.iotarho.artplace.app.R;
import dev.iotarho.artplace.app.callbacks.OnArtworkClickListener;
import dev.iotarho.artplace.app.model.ImageLinks;
import dev.iotarho.artplace.app.model.Thumbnail;
import dev.iotarho.artplace.app.model.artworks.Artwork;
import dev.iotarho.artplace.app.model.artworks.MainImage;
import dev.iotarho.artplace.app.utils.StringUtils;
import dev.iotarho.artplace.app.utils.Utils;

public class ArtworkItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private static final String TAG = ArtworkItemViewHolder.class.getSimpleName();

    @BindView(R.id.artwork_cardview)
    CardView artworkCard;
    @BindView(R.id.artwork_thumbnail)
    ImageView artworkThumbnail;
    @BindView(R.id.artwork_title)
    TextView artworkTitle;
    @BindView(R.id.artwork_artist)
    TextView artworkArtist;

    private OnArtworkClickListener mClickHandler;
    private Artwork mCurrentItem; // allows this viewholder to keep a reference to the bound item
    private int mLastPosition = -1;

    public ArtworkItemViewHolder(View itemView, OnArtworkClickListener clickListener) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        mClickHandler = clickListener;
        itemView.setOnClickListener(this);
    }

    public void bindTo(Artwork artwork) {

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
            if (Utils.isNullOrEmpty(artworkThumbnailString)) {
                // If it's empty or null -> set the placeholder
                artworkThumbnail.setImageResource(R.color.color_on_surface);
            } else {
                // If it's not empty -> load the image
                Picasso.get()
                        .load(Uri.parse(artworkThumbnailString))
                        .placeholder(R.color.color_surface)
                        .error(R.color.color_error)
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

    private void setItemAnimator(View view, int position) {
        if (position > mLastPosition) {
            Animation animation = AnimationUtils
                    .loadAnimation(view.getContext(), R.anim.item_animation_from_bottom);
            view.startAnimation(animation);
            mLastPosition = position;
        }
    }
}
