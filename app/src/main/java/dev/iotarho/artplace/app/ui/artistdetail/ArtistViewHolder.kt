package dev.iotarho.artplace.app.ui.artistdetail

import android.net.Uri
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import dev.iotarho.artplace.app.R
import dev.iotarho.artplace.app.callbacks.OnArtistClickHandler
import dev.iotarho.artplace.app.databinding.ArtworkByArtistItemBinding
import dev.iotarho.artplace.app.model.artists.Artist
import dev.iotarho.artplace.app.utils.Utils

class ArtistViewHolder(private val binding: ArtworkByArtistItemBinding, clickHandler: OnArtistClickHandler) : RecyclerView.ViewHolder(binding.root), View.OnClickListener {

    private val clickHandler: OnArtistClickHandler
    private var currentArtist: Artist? = null

    init {
        itemView.setOnClickListener(this)
        this.clickHandler = clickHandler
    }

    fun bindTo(artist: Artist?) {
        if (artist != null) {
            currentArtist = artist
            val imageLinks = artist.links
            var artworkThumbnailString: String? = null
            try {
                val thumbnail = imageLinks.thumbnail
                artworkThumbnailString = thumbnail.href
            } catch (e: Exception) {
                Log.e("ArtworksViewHolder", "Error obtaining thumbnail from JSON: " + e.message)
            }
            if (Utils.isNullOrEmpty(artworkThumbnailString)) {
                // If it's empty or null -> set the placeholder
                binding.similarArtworkThumbnail.setImageResource(R.color.color_primary)
            } else {
                // If it's not empty -> load the image
                Picasso.get()
                        .load(Uri.parse(artworkThumbnailString))
                        .placeholder(R.color.color_primary)
                        .error(R.color.color_error)
                        .into(binding.similarArtworkThumbnail)
            }
            binding.similarArtworkTitle.text = artist.name
            binding.similarArtworkArtist.text = artist.hometown
        }
    }

    override fun onClick(v: View) {
        clickHandler.onArtistClick(currentArtist)
    }
}