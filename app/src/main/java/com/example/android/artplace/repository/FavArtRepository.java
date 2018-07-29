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

package com.example.android.artplace.repository;

import android.app.Application;
import android.arch.paging.DataSource;
import android.os.AsyncTask;
import android.util.Log;

import com.example.android.artplace.AppExecutors;
import com.example.android.artplace.database.ArtworksDatabase;
import com.example.android.artplace.database.dao.FavArtworksDao;
import com.example.android.artplace.database.entity.FavoriteArtworks;


public class FavArtRepository {

    private static final String TAG = FavArtRepository.class.getSimpleName();
    private static final Object LOCK = new Object();
    private static FavArtRepository INSTANCE;
    private FavArtworksDao mFavArtworksDao;


    // TODO: Move to the Callbacks package
    public interface ResultFromDbCallback {
        void setResult(boolean isFav);
    }


    private FavArtRepository(Application application) {
        ArtworksDatabase artworksDatabase = ArtworksDatabase.getInstance(application);
        mFavArtworksDao = artworksDatabase.favArtworksDao();
    }

    public static FavArtRepository getInstance(Application application) {
        if (INSTANCE == null) {

            synchronized (LOCK) {
                if (INSTANCE == null) {
                    INSTANCE = new FavArtRepository(application);
                }
            }
        }
        return INSTANCE;
    }

    public DataSource.Factory<Integer, FavoriteArtworks> getAllFavArtworks() {
        // TODO: Check if the Dao class is not null
        return mFavArtworksDao.getAllArtworks();
    }

    public void deleteItem(final String artworkId) {
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                mFavArtworksDao.deleteArtwork(artworkId);
            }
        });
    }

    // TODO: Delete all list of favorite artworks

    public void insertItem(FavoriteArtworks favArtwork, String artworkId) {
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "Item is added to the db!");
                mFavArtworksDao.insertArtwork(favArtwork);
            }
        });
    }


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
