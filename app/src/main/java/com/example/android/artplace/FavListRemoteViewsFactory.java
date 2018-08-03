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

package com.example.android.artplace;

import android.app.Application;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.example.android.artplace.database.entity.FavoriteArtworks;
import com.example.android.artplace.repository.FavArtRepository;

import java.util.List;

public class FavListRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    private static final String TAG = FavListRemoteViewsFactory.class.getSimpleName();

    private List<FavoriteArtworks> mFavList;
    private Application mApplication;

    public FavListRemoteViewsFactory(Application application){
        mApplication = application;
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "Widget: onCreate triggered now");
    }

    @Override
    public void onDataSetChanged() {
        Log.d(TAG, "Widget: onDataSetChanged triggered now");

        // Get the data here from the Repository on a background thread
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                mFavList = FavArtRepository.getInstance(mApplication).getFavArtworksList();

                Log.d(TAG, "Widget: Get the fav list: " + mFavList.size());
            }
        });

    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "Widget: onDestroy triggered now");
    }

    @Override
    public int getCount() {
        if (mFavList != null) {
            Log.d(TAG, "Widget: getCount the fav list: " + mFavList.size());
            return mFavList.size();
        }
        return 0;
    }

    @Override
    public RemoteViews getViewAt(int position) {

        Log.d(TAG, "Widget: getViewAt triggered now");

        if (mFavList == null || mFavList.size() > 0) {
            return null;
        }

        RemoteViews views = new RemoteViews(mApplication.getPackageName(),
                R.layout.widget_fav_item);

        String titleString = mFavList.get(position).getArtworkTitle();
        views.setTextViewText(R.id.widget_title, titleString);
        Log.d(TAG, "Widget: Get the title: " + titleString);

        String artistString = mFavList.get(position).getArtworkSlug();
        views.setTextViewText(R.id.widget_artist, artistString);
        Log.d(TAG, "Widget: Get the artist: " + artistString);

        String dateString = mFavList.get(position).getArtworkDate();
        views.setTextViewText(R.id.widget_date, dateString);
        Log.d(TAG, "Widget: Get the date: " + dateString);

        String categoryString = mFavList.get(position).getArtworkCategory();
        views.setTextViewText(R.id.widget_category, categoryString);
        Log.d(TAG, "Widget: Get the category: " + categoryString);

        return views;

    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
