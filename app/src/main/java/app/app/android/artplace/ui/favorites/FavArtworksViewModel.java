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

package app.app.android.artplace.ui.favorites;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;
import android.util.Log;

import app.app.android.artplace.callbacks.ResultFromDbCallback;
import app.app.android.artplace.database.entity.FavoriteArtworks;
import app.app.android.artplace.repository.FavArtRepository;

import java.util.List;

public class FavArtworksViewModel extends AndroidViewModel {

    private static final String TAG = FavArtworksViewModel.class.getSimpleName();
    private static final int PAGE_SIZE = 20;

    private LiveData<PagedList<FavoriteArtworks>> mFavArtworkList;
    private FavArtRepository mRepository;


    public FavArtworksViewModel(Application application) {
        super(application);

        mRepository = FavArtRepository.getInstance(application);

        init(application);
        Log.d(TAG, "FavArtworksViewModel called");
    }

    /*
     Method for initializing the DataSourceFactory and for building the LiveData
    */
    private void init(Application application) {
        PagedList.Config pagedListConfig = new PagedList.Config.Builder()
                .setEnablePlaceholders(true)
                .setPrefetchDistance(PAGE_SIZE)
                .setPageSize(PAGE_SIZE)
                .build();

        mFavArtworkList = new LivePagedListBuilder<>(FavArtRepository.getInstance(application)
                .getAllFavArtworks(),
                pagedListConfig).build();
    }

    public LiveData<PagedList<FavoriteArtworks>> getFavArtworkList() {
        return mFavArtworkList;
    }

    public List<FavoriteArtworks> getFavListForWidget() {
        return mRepository.getFavArtworksList();
    }

    public LiveData<PagedList<FavoriteArtworks>> refreshFavArtworkList(Application application) {

        PagedList.Config pagedListConfig = new PagedList.Config.Builder()
                .setEnablePlaceholders(true)
                .setPrefetchDistance(PAGE_SIZE)
                .setPageSize(PAGE_SIZE)
                .build();

        mFavArtworkList = new LivePagedListBuilder<>(FavArtRepository.getInstance(application)
                .getAllFavArtworks(),
                pagedListConfig).build();

        Log.d(TAG, "refreshFavArtworkList is called.");
        return mFavArtworkList;
    }

    // TODO: Remove this method, because we don't need to call it from here
    public void insertItem(FavoriteArtworks favArtwork) {
        mRepository.insertItem(favArtwork);
    }

    // TODO: Remove this method, because we don't need to call it from here
    public void getItemById(String artworkId, ResultFromDbCallback resultFromDbCallback) {
        mRepository.executeGetItemById(artworkId, resultFromDbCallback);
    }

    public void deleteItem(String artworkId) {
        mRepository.deleteItem(artworkId);
    }

    public void deleteAllItems() {
        mRepository.deleteAllItems();
    }

    @Override
    protected void onCleared() {
        // Destroy the database instance
        //ArtworksDatabase.destroyInstance();
        super.onCleared();
    }
}
