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
package dev.iotarho.artplace.app.ui.mainactivity

import android.app.SearchManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import androidx.annotation.ColorRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.aurelhubert.ahbottomnavigation.AHBottomNavigation
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem
import dev.iotarho.artplace.app.R
import dev.iotarho.artplace.app.callbacks.OnRefreshListener
import dev.iotarho.artplace.app.databinding.ActivityMainBinding
import dev.iotarho.artplace.app.ui.artworks.ArtworksFragment
import dev.iotarho.artplace.app.ui.favorites.FavoritesFragment
import dev.iotarho.artplace.app.ui.mainactivity.adapters.BottomNavAdapter
import dev.iotarho.artplace.app.ui.searchresults.SearchFragment
import dev.iotarho.artplace.app.utils.PreferenceUtils
import dev.iotarho.artplace.app.utils.ThemeUtils

class MainActivity : AppCompatActivity(), OnRefreshListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var pagerAdapter: BottomNavAdapter
    private var mTitle: String? = null
    private var mPosition = 0
    private var query: String? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val window = this.window
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = ContextCompat.getColor(this, R.color.color_on_background)
        }
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val preferenceUtils = PreferenceUtils.getInstance()
        ThemeUtils.applyTheme(preferenceUtils.themeFromPrefs)
        if (savedInstanceState != null) {
            // Get the position of the selected Fragment
            mPosition = savedInstanceState.getInt(POSITION_KEY)
        }

        // Set the title on the toolbar according to
        // the position of the clicked Fragment
        setToolbarTitle(mPosition)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = mTitle

        binding.setupViewPager()
        binding.setupBottomNavStyle()

        // Add items to the Bottom Navigation
        binding.addBottomNavigationItems()
        binding.bottomNavigation.currentItem = mPosition
        binding.bottomNavigation.setOnTabSelectedListener { position: Int, wasSelected: Boolean ->
            binding.bottomNavigation.titleState = AHBottomNavigation.TitleState.ALWAYS_SHOW
            binding.viewPagerContent.currentItem = position
            mPosition = position
            // Set the title on the toolbar according to
            // the position of the clicked Fragment
            setToolbarTitle(position)
            true
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // Save the position of the selected Fragment
        outState.putInt(POSITION_KEY, mPosition)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent) // intent saved by the activity is updated in case you call getIntent() in the future
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent) {
        if (Intent.ACTION_SEARCH == intent.action) {
            query = intent.getStringExtra(SearchManager.QUERY)
            Log.d(TAG, "handleIntent, queryString: $query")
            SearchFragment.newInstanceWithExtra(query)
        }
    }

    override fun onSearchRequested(): Boolean {
        val appData = Bundle()
        appData.putString("search_word_from_intent", "query")
        Log.d(TAG, "onSearchRequested called")
        startSearch(null, false, appData, false)
        return true
    }

    /**
     * Method for setting the title on the Toolbar for each Fragment
     *
     * @param position of the current Fragment
     */
    private fun setToolbarTitle(position: Int) {
        when (position) {
            0 -> {
                mTitle = getString(R.string.title_artworks)
                binding.toolbar.title = mTitle
            }
            1 -> {
                mTitle = getString(R.string.title_search)
                binding.toolbar.title = mTitle
            }
            2 -> {
                mTitle = getString(R.string.title_favorites)
                binding.toolbar.title = mTitle
            }
            else -> {
            }
        }
    }

    private fun ActivityMainBinding.addBottomNavigationItems() {
        val artworksItem = AHBottomNavigationItem(
                getString(R.string.title_artworks), R.drawable.ic_red_dot)
        val artistsItem = AHBottomNavigationItem(
                getString(R.string.search), R.drawable.ic_red_dot)
        val favoritesItem = AHBottomNavigationItem(
                getString(R.string.title_favorites), R.drawable.ic_red_dot)
        bottomNavigation.addItem(artworksItem)
        bottomNavigation.addItem(artistsItem)
        bottomNavigation.addItem(favoritesItem)
    }

    private fun ActivityMainBinding.setupBottomNavStyle() {
        // Set default background color for AHBottomNavigation
        bottomNavigation.defaultBackgroundColor = fetchColor(R.color.color_on_surface)

        // Change colors for AHBottomNavigation
        bottomNavigation.accentColor = fetchColor(R.color.color_primary)
        bottomNavigation.inactiveColor = fetchColor(R.color.color_secondary)

        // Manage titles for AHBottomNavigation
        bottomNavigation.titleState = AHBottomNavigation.TitleState.SHOW_WHEN_ACTIVE

        // Use colored navigation with circle reveal effect
        bottomNavigation.isColored = false

        // Hide the navigation when the user scroll the Rv
        bottomNavigation.isBehaviorTranslationEnabled = true
    }

    private fun ActivityMainBinding.setupViewPager() {
        viewPagerContent.setPagingEnabled(false)
        // Set the offset to 3 so that the Pager Adapter keeps in memory
        // all 3 Fragments and doesn't load it
        viewPagerContent.offscreenPageLimit = 3
        pagerAdapter = BottomNavAdapter(supportFragmentManager).apply {
            addFragments(ArtworksFragment.newInstance())
            addFragments(SearchFragment.newInstance())
            addFragments(FavoritesFragment.newInstance())
        }
        viewPagerContent.adapter = pagerAdapter
    }

    override fun onRefreshConnection() {
        setAppBarVisible()
    }

    override fun onResume() {
        super.onResume()
        // Show the AppBarLayout every time after resume
        setAppBarVisible()
    }

    private fun setAppBarVisible() {
        // Set the AppBarLayout to expanded
        binding.appbarMain.setExpanded(true, true)
    }

    /**
     * Simple method for fetching colors
     * source: https://android.jlelse.eu/ultimate-guide-to-bottom-navigation-on-android-75e4efb8105f
     *
     * @param color to fetch
     * @return int color value
     */
    private fun fetchColor(@ColorRes color: Int): Int {
        return ContextCompat.getColor(this, color)
    }

    companion object {
        private val TAG = MainActivity::class.java.simpleName
        private const val POSITION_KEY = "position"
        const val SEARCH_WORD_EXTRA = "search_word_from_intent"
    }
}