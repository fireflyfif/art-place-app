package dev.iotarho.artplace.app.ui.artworks.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagedList
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.RecyclerView
import dev.iotarho.artplace.app.R
import dev.iotarho.artplace.app.callbacks.OnArtistClickHandler
import dev.iotarho.artplace.app.callbacks.OnRefreshListener
import dev.iotarho.artplace.app.databinding.ArtworkByArtistItemBinding
import dev.iotarho.artplace.app.model.artists.Artist
import dev.iotarho.artplace.app.ui.NetworkStateItemViewHolder
import dev.iotarho.artplace.app.ui.artistdetail.ArtistViewHolder
import dev.iotarho.artplace.app.utils.NetworkState

class TrendyArtistsAdapter(private val clickHandler: OnArtistClickHandler, private val onRefreshListener: OnRefreshListener) : PagedListAdapter<Artist?, RecyclerView.ViewHolder>(Artist.DIFF_CALLBACK) {
    private var networkState: NetworkState? = null
    private var artistList: List<Artist?>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return if (viewType == TYPE_PROGRESS) {
            val view = layoutInflater.inflate(R.layout.network_state_item, parent, false)
            NetworkStateItemViewHolder(view, onRefreshListener)
        } else {
            val binding = ArtworkByArtistItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            ArtistViewHolder(binding, clickHandler)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ArtistViewHolder) {
            val artist = getItem(position)
            holder.bindTo(artist)
        } else {
            (holder as NetworkStateItemViewHolder).bindView(networkState)
        }
    }

    private fun hasExtraRow(): Boolean {
        return networkState != null && networkState !== NetworkState.LOADED
    }

    override fun getItemViewType(position: Int): Int {
        return if (hasExtraRow() && position == itemCount - 1) {
            TYPE_PROGRESS
        } else {
            TYPE_ITEM
        }
    }

    override fun getItem(position: Int): Artist? {
        return super.getItem(position)
    }

    override fun getItemCount(): Int {
        return if (artistList != null) artistList?.size ?: 0 else 0
    }

    fun setNetworkState(newNetworkState: NetworkState) {
        val previousState = networkState
        val previousExtraRow = hasExtraRow()
        networkState = newNetworkState
        val newExtraRow = hasExtraRow()
        if (previousExtraRow != newExtraRow) {
            if (previousExtraRow) {
                notifyItemRemoved(itemCount)
            } else {
                notifyItemInserted(itemCount)
            }
        } else if (newExtraRow && previousState !== newNetworkState) {
            notifyItemChanged(itemCount - 1)
        }
    }

    override fun onCurrentListChanged(previousList: PagedList<Artist?>?, currentList: PagedList<Artist?>?) {
        super.onCurrentListChanged(previousList, currentList)
        artistList = currentList
        notifyDataSetChanged()
    }

    fun swapCatalogue(artistList: List<Artist?>?) {
        this.artistList = artistList
        notifyDataSetChanged()
    }

    companion object {
        private const val TYPE_PROGRESS = 0
        private const val TYPE_ITEM = 1
    }
}