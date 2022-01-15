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
package dev.iotarho.artplace.app.ui.artistdetail

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dev.iotarho.artplace.app.R
import dev.iotarho.artplace.app.callbacks.OnArtistClickHandler
import dev.iotarho.artplace.app.callbacks.OnRefreshListener
import dev.iotarho.artplace.app.databinding.ArtworkByArtistItemBinding
import dev.iotarho.artplace.app.model.artists.Artist
import dev.iotarho.artplace.app.ui.NetworkStateItemViewHolder
import dev.iotarho.artplace.app.utils.NetworkState

class ArtistForGenreListAdapter(private val artistList: List<Artist>?, private val mRefreshHandler: OnRefreshListener, private val clickHandler: OnArtistClickHandler) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val mNetworkState: NetworkState? = null

    companion object {
        private val TAG = ArtistForGenreListAdapter::class.java.simpleName
        private const val TYPE_PROGRESS = 0
        private const val TYPE_ITEM = 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return if (viewType == TYPE_PROGRESS) {
            val view = layoutInflater.inflate(R.layout.network_state_item, parent, false)
            NetworkStateItemViewHolder(view, mRefreshHandler)
        } else {
            val binding = ArtworkByArtistItemBinding.inflate(layoutInflater, parent, false)
            ArtistViewHolder(binding, clickHandler)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ArtistViewHolder) {
            val artist = artistList?.get(position)
            holder.bindTo(artist)
        } else {
            (holder as NetworkStateItemViewHolder).bindView(mNetworkState)
        }
    }

    override fun getItemCount(): Int {
        if (artistList == null) {
            return 0
        }
        Log.d(TAG, "Size of the Similar Artworks list: $artistList")
        return artistList.size
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
}