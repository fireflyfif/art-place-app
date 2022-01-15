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
package dev.iotarho.artplace.app.ui.artworks.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.paging.PagedList
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.RecyclerView
import dev.iotarho.artplace.app.R
import dev.iotarho.artplace.app.callbacks.OnArtworkClickListener
import dev.iotarho.artplace.app.callbacks.OnRefreshListener
import dev.iotarho.artplace.app.databinding.ArtworkItemBinding
import dev.iotarho.artplace.app.model.artworks.Artwork
import dev.iotarho.artplace.app.ui.NetworkStateItemViewHolder
import dev.iotarho.artplace.app.utils.NetworkState
import dev.iotarho.artplace.app.utils.Utils
import java.util.*

// Help from tutorial: https://proandroiddev.com/8-steps-to-implement-paging-library-in-android-d02500f7fffe
class ArtworkListAdapter(private val mClickHandler: OnArtworkClickListener, private val mRefreshHandler: OnRefreshListener) : PagedListAdapter<Artwork?, RecyclerView.ViewHolder>(Artwork.DIFF_CALLBACK), Filterable {
    private var mNetworkState: NetworkState? = null
    private var mArtworkList: List<Artwork?>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return if (viewType == TYPE_PROGRESS) {
            val view = layoutInflater.inflate(R.layout.network_state_item, parent, false)
            NetworkStateItemViewHolder(view, mRefreshHandler)
        } else {
            val binding = ArtworkItemBinding.inflate(layoutInflater, parent, false)
            ArtworkItemViewHolder(binding, mClickHandler)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ArtworkItemViewHolder) {
            // This gets the Item from the PagedList
            if (getItem(position) != null) {
                val currentArtwork = mArtworkList?.get(position)
                currentArtwork?.let { holder.bindTo(it, position) }
            }
        } else {
            (holder as NetworkStateItemViewHolder).bindView(mNetworkState)
        }
    }

    private fun hasExtraRow(): Boolean {
        return mNetworkState != null && mNetworkState !== NetworkState.LOADED
    }

    override fun getItemViewType(position: Int): Int {
        return if (hasExtraRow() && position == itemCount - 1) {
            TYPE_PROGRESS
        } else {
            TYPE_ITEM
        }
    }

    // Implement this method, as without the filtered list is producing an error
    override fun getItemCount(): Int {
        return if (mArtworkList != null) mArtworkList?.size ?: 0 else 0
    }

    fun setNetworkState(newNetworkState: NetworkState) {
        val previousState = mNetworkState
        val previousExtraRow = hasExtraRow()
        mNetworkState = newNetworkState
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

    override fun onCurrentListChanged(previousList: PagedList<Artwork?>?, currentList: PagedList<Artwork?>?) {
        super.onCurrentListChanged(previousList, currentList)
        mArtworkList = currentList
        notifyDataSetChanged()
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence): FilterResults {
                val queryWord = constraint.toString().toLowerCase()
                val filteredList: MutableList<Artwork?> = ArrayList()
                val filterResults = FilterResults()
                if (Utils.isNullOrEmpty(queryWord)) {
                    mArtworkList = currentList
                    filterResults.values = mArtworkList
                    if (mArtworkList != null) {
                        filterResults.count = mArtworkList?.size ?: 0
                    }
                } else {
                    // Add a constrain for the artist name and the category
                    for (currentArtwork in mArtworkList!!) {
                        val artworkTitle = currentArtwork?.title?.toLowerCase()
                        val artworkArtist = currentArtwork?.slug?.toLowerCase()
                        val artworkCategory = currentArtwork?.category?.toLowerCase()
                        if (artworkTitle?.contains(queryWord) == true || artworkArtist?.contains(queryWord) == true
                                || artworkCategory?.contains(queryWord) == true) {
                            filteredList.add(currentArtwork)
                            mArtworkList = filteredList
                        }
                    }
                    filterResults.values = filteredList
                    filterResults.count = filteredList.size
                    Log.d(TAG, "Filtered list: " + filteredList.size)
                }
                return filterResults
            }

            override fun publishResults(constraint: CharSequence, results: FilterResults) {
                mArtworkList = results.values as List<Artwork?>
                //results.count = mArtworkList.size();
                Log.d(TAG, "Filtered list, publishResults: " + results.count)
                swapCatalogue(mArtworkList)
            }
        }
    }

    fun swapCatalogue(artworkList: List<Artwork?>?) {
        mArtworkList = artworkList
        notifyDataSetChanged()
    }

    companion object {
        private val TAG = ArtworkListAdapter::class.java.simpleName
        private const val TYPE_PROGRESS = 0
        private const val TYPE_ITEM = 1
    }
}