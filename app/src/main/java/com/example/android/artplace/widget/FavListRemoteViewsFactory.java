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

package com.example.android.artplace.widget;

import android.app.Application;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.example.android.artplace.AppExecutors;
import com.example.android.artplace.R;
import com.example.android.artplace.database.entity.FavoriteArtworks;
import com.example.android.artplace.repository.FavArtRepository;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.List;

public class FavListRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    private static final String TAG = FavListRemoteViewsFactory.class.getSimpleName();

    private List<FavoriteArtworks> mFavList;
    private Application mApplication;
    private int mAppWidgetId;

    public FavListRemoteViewsFactory(Application application, Intent intent){
        mApplication = application;
        mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
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

        if (mFavList == null || mFavList.size() == 0) {
            return null;
        }

        RemoteViews views = new RemoteViews(mApplication.getPackageName(),
                R.layout.widget_fav_item);

        String idString = mFavList.get(position).getArtworkId();
        Log.d(TAG, "Widget: Get the id: " + idString);

        Bundle extras = new Bundle();
        extras.putInt(ArtPlaceWidget.EXTRA_ITEM, position);

        Intent fillIntent = new Intent();
        fillIntent.putExtras(extras);
        views.setOnClickFillInIntent(R.id.widget_thumbnail, fillIntent);

        String thumbnailString = mFavList.get(position).getArtworkThumbnailPath();
        // Set the thumbnail on the widget view
        // SO post: https://stackoverflow.com/a/27851642/8132331
        try {
            Bitmap bitmap;

            if (thumbnailString == null || thumbnailString.isEmpty()) {
                bitmap = Picasso.get()
                        .load(R.color.colorPrimary)
                        .placeholder(R.color.colorPrimary)
                        .resize(300, 300)
                        .centerCrop()
                        .error(R.mipmap.ic_launcher).get();
            } else {
                bitmap = Picasso.get()
                        .load(thumbnailString)
                        .placeholder(R.color.colorPrimary)
                        .resize(300, 300)
                        .centerCrop()
                        .error(R.mipmap.ic_launcher).get();
            }

            views.setImageViewBitmap(R.id.widget_thumbnail, bitmap);

        } catch (IOException e) {
            e.printStackTrace();
        }

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
