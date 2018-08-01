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

package com.example.android.artplace.ui.favoriteArtworks;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.arch.paging.PagedList;
import android.content.DialogInterface;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.artplace.ArtPlaceApp;
import com.example.android.artplace.R;
import com.example.android.artplace.callbacks.OnFavItemClickListener;
import com.example.android.artplace.database.entity.FavoriteArtworks;
import com.example.android.artplace.repository.FavArtRepository;
import com.example.android.artplace.ui.favoriteArtworks.adapter.FavArtworkListAdapter;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import org.w3c.dom.Text;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FavArtworksActivity extends AppCompatActivity implements OnFavItemClickListener {

    private static final String TAG = FavArtworksActivity.class.getSimpleName();

    @BindView(R.id.fav_artworks_rv)
    RecyclerView favArtworksRv;
    @BindView(R.id.fav_empty_text)
    TextView emptyText;
    @BindView(R.id.fav_progress_bar)
    ProgressBar favProgressBar;
    @BindView(R.id.fav_appbar)
    AppBarLayout appBarLayout;
    @BindView(R.id.fav_toolbar)
    Toolbar toolbar;
    @BindView(R.id.fav_coordinator_layout)
    CoordinatorLayout coordinatorLayout;

    private FavArtworkListAdapter mAdapter;
    private PagedList<FavoriteArtworks> mFavoriteArtworksList;
    private Tracker mTracker;
    private FavArtworksViewModel mFavArtworksViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fav_artworks);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Obtain the shared Tracker instance.
        // source: https://developers.google.com/analytics/devguides/collection/android/v4/
        ArtPlaceApp application = (ArtPlaceApp) getApplication();
        mTracker = application.getDefaultTracker();

        mFavArtworksViewModel = ViewModelProviders.of(this).get(FavArtworksViewModel.class);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        favArtworksRv.setLayoutManager(layoutManager);

        mAdapter = new FavArtworkListAdapter(this, this);
        emptyText.setVisibility(View.INVISIBLE);

        // Show the whole list of Favorite Artwirks via the ViewModel
        getFavArtworks();

        favArtworksRv.setAdapter(mAdapter);

        // Delete a single item by swiping to left or right
        deleteItemBySwiping();

    }

    private void getFavArtworks() {
        mFavArtworksViewModel.getFavArtworkList().observe(this, new Observer<PagedList<FavoriteArtworks>>() {

            @Override
            public void onChanged(@Nullable PagedList<FavoriteArtworks> favoriteArtworks) {

                if (favoriteArtworks != null && favoriteArtworks.size() > 0) {
                    // Hide the Progress Bar and the Empty Text View
                    favProgressBar.setVisibility(View.INVISIBLE);
                    emptyText.setVisibility(View.INVISIBLE);

                    Log.d(TAG, "Submit artworks to the db " + favoriteArtworks.size());
                    mAdapter.submitList(favoriteArtworks);
                } else {
                    // Show the empty message when there is no items added to favorites
                    emptyText.setVisibility(View.VISIBLE);

                    // Hide the Progress Bar
                    favProgressBar.setVisibility(View.INVISIBLE);
                    Log.e(TAG, "No artworks added to the database");
                }

                mFavoriteArtworksList = favoriteArtworks;
            }
        });
    }

    private void deleteItemBySwiping() {
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            // Called when the user swipes left or right on a ViewHolder
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();

                FavoriteArtworks favArtwork = mAdapter.getFavoriteAtPosition(position);
                String itemId = favArtwork.getArtworkId();
                // Delete the item by swiping it
                mFavArtworksViewModel.deleteItem(itemId);
                Snackbar.make(coordinatorLayout, "This item was deleted!", Snackbar.LENGTH_SHORT).show();
                //Toast.makeText(FavArtworksActivity.this, "This item was deleted!", Toast.LENGTH_SHORT).show();
            }
        }).attachToRecyclerView(favArtworksRv);
    }

    @Override
    protected void onResume() {
        super.onResume();

        mTracker.setScreenName(TAG);
        // Send initial screen screen view hit.
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

        getFavArtworks();
    }

    @Override
    public void onFavItemClick(FavoriteArtworks favArtworks) {
        // TODO: Make the Intent to the DetailActivity
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.fav_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:

                return false;
            case R.id.action_delete_all:
                // Show warning dialog to the user before deleting all data from the db
                deleteItemsWithConfirmation();
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteItemsWithConfirmation() {

        if (mFavoriteArtworksList.size() > 0) {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Are you sure you want to delete all artworks?");
            builder.setTitle("Delete All");
            builder.setIcon(R.drawable.ic_refresh_24dp);

            builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mFavArtworksViewModel.deleteAllItems();
                    getFavArtworks();
                }
            });

            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                }
            });

            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
    }


}