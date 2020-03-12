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

package dev.iotarho.artplace.app.ui.favorites;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import dev.iotarho.artplace.app.R;
import dev.iotarho.artplace.app.callbacks.OnFavItemClickListener;
import dev.iotarho.artplace.app.database.entity.FavoriteArtworks;
import dev.iotarho.artplace.app.ui.FavDetailActivity;
import dev.iotarho.artplace.app.ui.favorites.adapter.FavArtworkListAdapter;

public class FavoritesFragment extends Fragment implements OnFavItemClickListener {

    private static final String TAG = FavoritesFragment.class.getSimpleName();
    private static final String ARG_FAV_TITLE = "fav_title";

    public static final String ARTWORK_ID_KEY = "artwork_id";
    private static final String ARTWORK_TITLE_KEY = "artwork_title";
    private static final String ARTWORK_ARTIST_KEY = "artwork_artist";
    private static final String ARTWORK_CATEGORY_KEY = "artwork_category";
    private static final String ARTWORK_MEDIUM_KEY = "artwork_medium";
    private static final String ARTWORK_DATE_KEY = "artwork_date";
    private static final String ARTWORK_MUSEUM_KEY = "artwork_museum";
    private static final String ARTWORK_IMAGE_KEY = "artwork_image";
    private static final String ARTWORK_DIMENS_CM_KEY = "artwork_dimens_cm";
    private static final String ARTWORK_DIMENS_INCH_KEY = "artwork_dimens_inch";

    @BindView(R.id.fav_artworks_rv)
    RecyclerView favArtworksRv;
    @BindView(R.id.fav_empty_text)
    TextView emptyText;
    @BindView(R.id.fav_progress_bar)
    ProgressBar favProgressBar;
    @BindView(R.id.fav_coordinator_layout)
    CoordinatorLayout coordinatorLayout;

    private FavArtworkListAdapter mAdapter;
    private PagedList<FavoriteArtworks> mFavoriteArtworksList;
    private FavArtworksViewModel mFavArtworksViewModel;
    private String mTitle;

    // Required empty public constructor
    public FavoritesFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Add a menu to the current Fragment
        setHasOptionsMenu(true);

        if (savedInstanceState != null) {
            mTitle = savedInstanceState.getString(ARG_FAV_TITLE);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(ARG_FAV_TITLE, mTitle);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_favorites, container, false);
        ButterKnife.bind(this, rootView);

        mFavArtworksViewModel = new ViewModelProvider(this).get(FavArtworksViewModel.class);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false);
        favArtworksRv.setLayoutManager(linearLayoutManager);

        mAdapter = new FavArtworkListAdapter(this);
        emptyText.setVisibility(View.INVISIBLE);

        // Show the whole list of Favorite Artworks via the ViewModel
        getFavArtworks();

        favArtworksRv.setAdapter(mAdapter);

        // Delete a single item by swiping to left or right
        deleteItemBySwiping();

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        refreshFavList();
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
                Log.d(TAG, "Number of fav artworks: " + favoriteArtworks);
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
                Snackbar.make(coordinatorLayout, R.string.snackbar_deleted_item, Snackbar.LENGTH_SHORT).show();
            }
        }).attachToRecyclerView(favArtworksRv);
    }

    private void refreshFavList() {

        mFavArtworksViewModel.refreshFavArtworkList(Objects.requireNonNull(getActivity()).getApplication()).observe(this, new Observer<PagedList<FavoriteArtworks>>() {
            @Override
            public void onChanged(@Nullable PagedList<FavoriteArtworks> favoriteArtworks) {
                mAdapter.submitList(favoriteArtworks);
                mFavoriteArtworksList = favoriteArtworks;
            }
        });
    }

    @Override
    public void onFavItemClick(FavoriteArtworks favArtworks) {

        // Set Intent to the DetailActivity
        Intent favIntent = new Intent(getActivity(), FavDetailActivity.class);
        favIntent.putExtra(ARTWORK_ID_KEY, favArtworks.getId());
        favIntent.putExtra(ARTWORK_IMAGE_KEY, favArtworks.getArtworkImagePath());
        favIntent.putExtra(ARTWORK_TITLE_KEY, favArtworks.getArtworkTitle());
        favIntent.putExtra(ARTWORK_ARTIST_KEY, favArtworks.getArtworkSlug());
        favIntent.putExtra(ARTWORK_CATEGORY_KEY, favArtworks.getArtworkCategory());
        favIntent.putExtra(ARTWORK_DATE_KEY, favArtworks.getArtworkDate());
        favIntent.putExtra(ARTWORK_MEDIUM_KEY, favArtworks.getArtworkMedium());
        favIntent.putExtra(ARTWORK_MUSEUM_KEY, favArtworks.getArtworkMuseum());
        favIntent.putExtra(ARTWORK_DIMENS_INCH_KEY, favArtworks.getArtworkDimensInch());
        favIntent.putExtra(ARTWORK_DIMENS_CM_KEY, favArtworks.getArtworkDimensCm());

        startActivity(favIntent);
    }

    private void deleteItemsWithConfirmation() {

        if (mFavoriteArtworksList.size() > 0) {

            AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getContext()));
            builder.setMessage(R.string.delete_all_message);
            builder.setTitle(R.string.delete_all_title);
            builder.setIcon(R.drawable.ic_delete_24dp);

            builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mFavArtworksViewModel.deleteAllItems();
                    // Swapping the data doesn't refresh the list?
                    mAdapter.swapData(mFavoriteArtworksList);
                    // Instead - Refresh the list
                    refreshFavList();
                }
            });

            builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.fav_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete_all:
                // Show warning dialog to the user before deleting all data from the db
                deleteItemsWithConfirmation();
                return true;

            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPause() {
        super.onPause();

        Log.d(TAG, "FavoritesFragment: onPause called");
    }


}
