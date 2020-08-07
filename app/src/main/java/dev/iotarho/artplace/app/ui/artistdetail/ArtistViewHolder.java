package dev.iotarho.artplace.app.ui.artistdetail;

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
import dev.iotarho.artplace.app.callbacks.OnArtistClickHandler;
import dev.iotarho.artplace.app.model.ImageLinks;
import dev.iotarho.artplace.app.model.Thumbnail;
import dev.iotarho.artplace.app.model.artists.Artist;
import dev.iotarho.artplace.app.utils.Utils;


public class ArtistViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    @BindView(R.id.similar_artwork_thumbnail)
    ImageView similarArtworkThumbnail;

    @BindView(R.id.similar_artwork_title)
    TextView similarTitle;

    @BindView(R.id.similar_artwork_artist)
    TextView similarArtist;

    private OnArtistClickHandler clickHandler;
    private Artist currentArtist;

    public ArtistViewHolder(View itemView, OnArtistClickHandler clickHandler) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        itemView.setOnClickListener(this);
        this.clickHandler = clickHandler;
    }

    public void bindTo(Artist artist) {
        if (artist != null) {
            currentArtist = artist;

            ImageLinks imageLinks = artist.getLinks();
            String artworkThumbnailString = null;
            try {
                Thumbnail thumbnail = imageLinks.getThumbnail();
                artworkThumbnailString = thumbnail.getHref();
            } catch (Exception e) {
                Log.e("ArtworksViewHolder", "Error obtaining thumbnail from JSON: " + e.getMessage());
            }

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
        clickHandler.onArtistClick(currentArtist);
    }
}
