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

package dev.iotarho.artplace.app.repository;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

import androidx.paging.DataSource;

import java.util.List;

import dev.iotarho.artplace.app.AppExecutors;
import dev.iotarho.artplace.app.callbacks.ResultFromDbCallback;
import dev.iotarho.artplace.app.database.ArtworksDatabase;
import dev.iotarho.artplace.app.database.dao.FavArtworksDao;
import dev.iotarho.artplace.app.database.entity.FavoriteArtworks;


public class FavArtRepository {

    private static final String TAG = FavArtRepository.class.getSimpleName();
    private static final Object LOCK = new Object();
    private static FavArtRepository INSTANCE;
    private FavArtworksDao mFavArtworksDao;


    private FavArtRepository(Application application) {
        ArtworksDatabase artworksDatabase = ArtworksDatabase.getInstance(application);
        mFavArtworksDao = artworksDatabase.favArtworksDao();
    }

    public synchronized static void createInstance(Application application) {
        if (INSTANCE == null) {
            // if there is no instance available, create a new one
            synchronized (LOCK) {
                if (INSTANCE == null) {
                    INSTANCE = new FavArtRepository(application);
                }
            }
        }
    }

    public static FavArtRepository getInstance() {
        // Double check locking pattern
        if (INSTANCE == null) {
            throw new IllegalStateException("FavArtRepository instance not initialized");
        }
        return INSTANCE;
    }

    public List<FavoriteArtworks> getFavArtworksList() {
        return mFavArtworksDao.allArtworks();
    }

    public DataSource.Factory<Integer, FavoriteArtworks> getAllFavArtworks() {

        return mFavArtworksDao.getAllArtworks();
    }

    // Delete a single item from the database
    public void deleteItem(final String artworkId) {
        AppExecutors.getInstance().diskIO().execute(() -> {
            Log.d(TAG, "Item is deleted from the db!");
            mFavArtworksDao.deleteArtwork(artworkId);
        });
    }

    // Delete all list of favorite artworks
    // Create a warning dialog for the user before allowing them to delete all data
    public void deleteAllItems() {
        AppExecutors.getInstance().diskIO().execute(() -> mFavArtworksDao.deleteAllData());
    }

    // Insert a new item into the database
    public void insertItem(FavoriteArtworks favArtwork) {
        AppExecutors.getInstance().diskIO().execute(() -> {
            Log.d(TAG, "Item is added to the db!");
            mFavArtworksDao.insertArtwork(favArtwork);
        });
    }


    // Get an item by Id from the database
    public void executeGetItemById(String artworkId, ResultFromDbCallback resultFromDbCallback) {
        new getItemById(artworkId, mFavArtworksDao, resultFromDbCallback).execute();
    }


    // Query the item on a background thread via AsyncTask
    private static class getItemById extends AsyncTask<Void, Void, Boolean> {
        private String artworkId;
        private FavArtworksDao favArtworksDao;
        private ResultFromDbCallback resultFromDbCallback;

        private getItemById(String artworkId, FavArtworksDao favArtworksDao, ResultFromDbCallback resultFromDbCallback) {
            this.artworkId = artworkId;
            this.favArtworksDao = favArtworksDao;
            this.resultFromDbCallback = resultFromDbCallback;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            boolean isFav = artworkId.equals(favArtworksDao.getItemById(artworkId));
            Log.d(TAG, "From doInBackground: Item exists in the db: " + isFav);

            return isFav;
        }

        @Override
        protected void onPostExecute(Boolean isFav) {
            resultFromDbCallback.setResult(isFav);
        }
    }

}
