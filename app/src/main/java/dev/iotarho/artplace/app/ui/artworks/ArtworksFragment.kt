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
package dev.iotarho.artplace.app.ui.artworks

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.paging.PagedList
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.snackbar.Snackbar
import dev.iotarho.artplace.app.R
import dev.iotarho.artplace.app.callbacks.OnArtistClickHandler
import dev.iotarho.artplace.app.callbacks.OnArtworkClickListener
import dev.iotarho.artplace.app.callbacks.OnRefreshListener
import dev.iotarho.artplace.app.callbacks.SnackMessageListener
import dev.iotarho.artplace.app.databinding.FragmentArtworksBinding
import dev.iotarho.artplace.app.model.artists.Artist
import dev.iotarho.artplace.app.model.artworks.Artwork
import dev.iotarho.artplace.app.ui.artistdetail.ArtistDetailActivity
import dev.iotarho.artplace.app.ui.artworkdetail.ArtworkDetailActivity
import dev.iotarho.artplace.app.ui.artworks.adapter.TrendyArtistsAdapter
import dev.iotarho.artplace.app.utils.*

class ArtworksFragment : Fragment(), OnArtworkClickListener, OnRefreshListener, SnackMessageListener, SwipeRefreshLayout.OnRefreshListener, OnArtistClickHandler {
    private var binding: FragmentArtworksBinding? = null
    private lateinit var trendyArtistsAdapter: TrendyArtistsAdapter
    private lateinit var trendyArtistsViewModel: TrendyArtistsViewModel
    private lateinit var preferenceUtils: PreferenceUtils

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preferenceUtils = PreferenceUtils.getInstance()

        // Add a menu to the current Fragment
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        // Inflate the layout for this fragment
        binding = FragmentArtworksBinding.inflate(layoutInflater, container, false)
        setupUi()
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.refreshLayout?.setOnRefreshListener(this)
    }

    private fun setRecyclerView() {
        val columnCount = resources.getInteger(R.integer.list_column_count)
        val staggeredGridLayoutManager = StaggeredGridLayoutManager(columnCount, StaggeredGridLayoutManager.VERTICAL)
        binding?.artworksRv?.layoutManager = staggeredGridLayoutManager

        // Set the PagedListAdapter
        trendyArtistsAdapter = TrendyArtistsAdapter(this, this)
    }

    private fun setupUi() {
        setRecyclerView()
        val trendyArtistViewModelFactory = Injection.provideTrendyViewModelFactory()
        trendyArtistsViewModel = ViewModelProvider(viewModelStore, trendyArtistViewModelFactory)[TrendyArtistsViewModel::class.java]
        trendyArtistsViewModel.trendyArtistLiveData.observe(viewLifecycleOwner, { artist ->
            trendyArtistsAdapter.submitList(artist)
            trendyArtistsAdapter.swapCatalogue(artist)
        })
        trendyArtistsViewModel.networkState.observe(viewLifecycleOwner, { networkState: NetworkState? -> networkState?.let { trendyArtistsAdapter.setNetworkState(it) } })
        trendyArtistsViewModel.initialLoading.observe(viewLifecycleOwner, { networkState: NetworkState? ->
            if (networkState != null) {
                binding?.progressBar?.visibility = View.VISIBLE

                // When the NetworkStatus is Successful
                // hide both the Progress Bar and the error message
                if (networkState.status == NetworkState.Status.SUCCESS) {
                    binding?.progressBar?.visibility = View.INVISIBLE
                }
                // When the NetworkStatus is Failed
                // show the error message and hide the Progress Bar
                if (networkState.status == NetworkState.Status.FAILED) {
                    binding?.progressBar?.visibility = View.INVISIBLE
                    binding?.coordinatorLayout?.let {
                        Snackbar.make(it, R.string.snackbar_no_network_connection,
                                Snackbar.LENGTH_LONG).show()
                    }
                }
            }
        })
        binding?.artworksRv?.adapter = trendyArtistsAdapter
    }

    @Synchronized
    fun refreshTrendyArtists() {
        setRecyclerView()
        trendyArtistsViewModel.refreshTrendyArtistsLiveData().observe(viewLifecycleOwner, { artists ->
            trendyArtistsAdapter.submitList(artists)
            trendyArtistsAdapter.swapCatalogue(artists)
        })
        binding?.artworksRv?.adapter = trendyArtistsAdapter
    }

    override fun onArtworkClick(artwork: Artwork, position: Int) {
        val bundle = Bundle()
        bundle.putParcelable(ARTWORK_PARCEL_KEY, artwork)
        val intent = Intent(context, ArtworkDetailActivity::class.java)
        intent.putExtras(bundle)
        startActivity(intent)
    }

    override fun onRefreshConnection() {
        Log.d(TAG, "onRefreshConnection is now triggered")
        RetrieveNetworkConnectivity(this).execute()
    }

    override fun showSnackMessage(resultMessage: String) {
        // TODO: Show the AppBar so that the Refresh icon be visible!!
        binding?.coordinatorLayout?.let { Snackbar.make(it, resultMessage, Snackbar.LENGTH_LONG).show() }
        refreshTrendyArtists()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.artworks_menu, menu)
        // Make the icon with a dynamic tint
        // source: https://stackoverflow.com/a/29916353/8132331
        var drawable = menu.findItem(R.id.action_refresh).icon
        drawable = DrawableCompat.wrap(drawable)
        DrawableCompat.setTint(drawable, ContextCompat.getColor(requireActivity(), R.color.color_on_surface))
        menu.findItem(R.id.action_refresh).icon = drawable
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_refresh -> {
                refreshTrendyArtists()
                return true
            }
            R.id.action_dark_mode -> {
                preferenceUtils.saveThemePrefs(1)
                ThemeUtils.applyTheme(preferenceUtils.themeFromPrefs)
                return true
            }
            R.id.action_light_mode -> {
                preferenceUtils.saveThemePrefs(0)
                ThemeUtils.applyTheme(preferenceUtils.themeFromPrefs)
                return true
            }
            else -> {
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onRefresh() {
        refreshTrendyArtists()
        if (binding?.refreshLayout?.isRefreshing == true) {
            binding?.refreshLayout?.isRefreshing = false
        }
    }

    override fun onArtistClick(artist: Artist) {
        val bundle = Bundle()
        bundle.putParcelable(ARTIST_PARCEL_KEY, artist)
        val intent = Intent(context, ArtistDetailActivity::class.java)
        intent.putExtras(bundle)
        startActivity(intent)
    }

    companion object {
        private val TAG = ArtworksFragment::class.java.simpleName
        private const val ARTIST_PARCEL_KEY = "artist_key"
        private const val ARTWORK_PARCEL_KEY = "artwork_key"

        fun newInstance(): ArtworksFragment {
            return ArtworksFragment()
        }
    }
}