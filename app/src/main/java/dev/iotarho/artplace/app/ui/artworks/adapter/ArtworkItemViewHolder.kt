package dev.iotarho.artplace.app.ui.artworks.adapter

import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import dev.iotarho.artplace.app.R
import dev.iotarho.artplace.app.callbacks.OnArtworkClickListener
import dev.iotarho.artplace.app.databinding.ArtworkItemBinding
import dev.iotarho.artplace.app.model.artworks.Artwork
import dev.iotarho.artplace.app.utils.StringUtils
import dev.iotarho.artplace.app.utils.Utils

class ArtworkItemViewHolder(private val binding: ArtworkItemBinding, clickListener: OnArtworkClickListener) : RecyclerView.ViewHolder(binding.root), View.OnClickListener {

    private val mClickHandler: OnArtworkClickListener = clickListener
    // allows this viewholder to keep a reference to the bound item
    private var mCurrentItem: Artwork? = null
    private var mLastPosition = -1

    companion object {
        private val TAG = ArtworkItemViewHolder::class.java.simpleName
    }

    init {
        itemView.setOnClickListener(this)
    }

    fun bindTo(artwork: Artwork, position: Int) {

        // Important point from this SO post: https://stackoverflow.com/a/40749134/8132331
        mCurrentItem = artwork // Keeps a reference to the current item

        // Get the thumbnail from the json tree
        if (artwork.links != null) {
            val currentImageLink = artwork.links
            var artworkThumbnailString: String? = null
            // Handle case where no thumbnail is provided!!!
            try {
                val currentThumbnail = currentImageLink.thumbnail
                artworkThumbnailString = currentThumbnail.href
                Log.d(TAG, "Link to the thumbnail: $artworkThumbnailString")
            } catch (e: Exception) {
                Log.e(TAG, "Error obtaining thumbnail from the JSON: " + e.message)
            }
            if (artworkThumbnailString == null) {

                // Handle case where no "image_version" is provided by the JSON
                if (artwork.imageVersions != null) {
                    val imageVersionList = artwork.imageVersions
                    val versionMediumString: String
                    // Check if the list with image version contains "medium"
                    if (imageVersionList.contains("medium") || imageVersionList.contains("medium_rectangle")) {

                        // Set the version to "medium"
                        versionMediumString = "medium"
                        Log.d(TAG, "Version of the image: $versionMediumString")
                    } else {

                        // Set the sixth String from the version list
                        versionMediumString = imageVersionList[0]
                        Log.d(TAG, "Version of the image: $versionMediumString")
                    }
                    val mainImage = currentImageLink.image
                    val artworkImgLinkString = mainImage.href

                    // Replace the {image_version} from the artworkImgLinkString with
                    // the wanted version, e.g. "large"
                    artworkThumbnailString = artworkImgLinkString.replace("\\{.*?\\}".toRegex(), versionMediumString)
                    Log.d(TAG, "New link to the image: $artworkThumbnailString")
                }
            }
            Log.d(TAG, "artworkThumbnailString: $artworkThumbnailString")

            // Get the title from the response
            val artworkTitleString = artwork.title
            binding.artworkTitle.text = artworkTitleString

            // Extract the Artist name from the Slug
            val artistNameString = StringUtils.getArtistNameFromSlug(artwork)
            Log.d(TAG, "Name of the artist: $artistNameString")
            binding.artworkArtist.text = artistNameString

            // Set the image with Picasso
            // Check first if the url in null or empty
            if (Utils.isNullOrEmpty(artworkThumbnailString)) {
                // If it's empty or null -> set the placeholder
                binding.artworkThumbnail.setImageResource(R.color.color_on_surface)
            } else {
                // If it's not empty -> load the image
                Picasso.get()
                        .load(Uri.parse(artworkThumbnailString))
                        .placeholder(R.color.color_surface)
                        .error(R.color.color_error)
                        .into(binding.artworkThumbnail, object : Callback {
                            override fun onSuccess() {
                                val bitmap = (binding.artworkThumbnail.drawable as BitmapDrawable).bitmap
                                binding.artworkThumbnail.setImageBitmap(bitmap)

                                // Set the item animator here
                                setItemAnimator(itemView, position)
                            }

                            override fun onError(e: Exception) {}
                        })
            }
        }
    }

    override fun onClick(view: View) {
        // Using the mCurrentItem make the position to update even after filtering the list
        mClickHandler.onArtworkClick(mCurrentItem, this.layoutPosition)
        Log.d(TAG, "onClick position: " + this.layoutPosition)
    }

    private fun setItemAnimator(view: View, position: Int) {
        if (position > mLastPosition) {
            val animation = AnimationUtils
                    .loadAnimation(view.context, R.anim.item_animation_from_bottom)
            view.startAnimation(animation)
            mLastPosition = position
        }
    }
}