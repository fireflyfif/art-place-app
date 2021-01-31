package dev.iotarho.artplace.app.ui.artworkdetail.adapter;

import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import dev.iotarho.artplace.app.R;
import dev.iotarho.artplace.app.callbacks.OnArtworkClickListener;
import dev.iotarho.artplace.app.model.ImageLinks;
import dev.iotarho.artplace.app.model.Thumbnail;
import dev.iotarho.artplace.app.model.artworks.Artwork;
import dev.iotarho.artplace.app.utils.StringUtils;
import dev.iotarho.artplace.app.utils.Utils;


public class ArtworksViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    @BindView(R.id.similar_artwork_thumbnail)
    ImageView similarArtworkThumbnail;

    @BindView(R.id.similar_artwork_title)
    TextView similarTitle;

    @BindView(R.id.similar_artwork_artist)
    TextView similarArtist;
    private OnArtworkClickListener onArtworkClickListener;
    private Artwork currentArtwork;

    public ArtworksViewHolder(View itemView, OnArtworkClickListener onArtworkClickListener) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        this.onArtworkClickListener = onArtworkClickListener;
        itemView.setOnClickListener(this);
    }

    public void bindTo(Artwork artwork) {
        currentArtwork = artwork;
        if (artwork != null) {
            ImageLinks imageLinks = artwork.getLinks();
            String artworkThumbnailString = null;
            try {
                Thumbnail thumbnail = imageLinks.getThumbnail();
                artworkThumbnailString = thumbnail.getHref();
            } catch (Exception e) {
                Log.e("ArtworksViewHolder", "Error obtaining thumbnail from JSON: " + e.getMessage());
            }

            String similarTitleString = artwork.getTitle();
            similarTitle.setText(similarTitleString);

            String artistNameString = StringUtils.getArtistNameFromSlug(artwork);
            Log.d("ArtworksViewHolder", "Name of the artist: " + artistNameString);
            similarArtist.setText(artistNameString);

            if (Utils.isNullOrEmpty(artworkThumbnailString)) {
                // If it's empty or null -> set the placeholder
                similarArtworkThumbnail.setImageResource(R.color.color_primary);
            } else {
                // If it's not empty -> load the image
                Picasso.get()
                        .load(Uri.parse(artworkThumbnailString))
                        .placeholder(R.color.color_primary)
                        .error(R.color.color_error)
                        .into(similarArtworkThumbnail);
            }

        }
    }

    @Override
    public void onClick(View v) {
        onArtworkClickListener.onArtworkClick(currentArtwork, this.getLayoutPosition());
    }
}
