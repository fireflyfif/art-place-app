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

package dev.iotarho.artplace.app.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.annotation.RequiresApi;

import java.util.Objects;

import dev.iotarho.artplace.app.R;
import dev.iotarho.artplace.app.ui.mainactivity.MainActivity;

/**
 * Implementation of App Widget functionality.
 */
public class ArtPlaceWidget extends AppWidgetProvider {

    private static final String TAG = ArtPlaceWidget.class.getSimpleName();
    public static final String EXTRA_ITEM = "com.example.android.artplace.widget.EXTRA_ITEM";


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        Bundle options = appWidgetManager.getAppWidgetOptions(appWidgetId);
        int width = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH);

        // Construct the RemoteViews object
        RemoteViews views = getGridFavArtworks(context, appWidgetId);;

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);

        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.appwidget_fav_grid);
    }

    /**
     * Creates the RemoteViews to be displayed in the GridView mode widget
     * @param context The context
     * @return The REmoteViews of the GridView mode widget
     */
    private static RemoteViews getGridFavArtworks(Context context, int appWidgetId) {
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.art_place_widget);

        AppWidgetManager.getInstance(context);

        // Setup the intent to point to the FavoritesWidgetService
        Intent intent = new Intent(context, FavoritesWidgetService.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);

        // When intents are compared, the extras are ignored, so we need to embed the extras
        // into the data so that the extras will not be ignored.
        // source: https://android.googlesource.com/platform/development/+/master/samples/StackWidget/src/com/example/android/stackwidget/StackWidgetProvider.java
        intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));

        // Set the Remote Adapter to the ListView
        views.setRemoteAdapter(R.id.appwidget_fav_grid, intent);

        Intent favIntent = new Intent(context, MainActivity.class);
        favIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        favIntent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, favIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        views.setPendingIntentTemplate(R.id.appwidget_fav_grid, pendingIntent);

        // Set empty view
        views.setEmptyView(R.id.appwidget_fav_grid, R.id.empty_msg_for_widget);

        Intent mainIntent = new Intent(context, MainActivity.class);
        PendingIntent mainPendingIntent = PendingIntent.getActivity(context, 0, mainIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.empty_widget_text, mainPendingIntent);

        return views;
    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.d(TAG, "Widget: onUpdate called");
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "Widget: onReceive called");

        if (intent != null) {
            if (Objects.requireNonNull(intent.getAction()).equals(AppWidgetManager.ACTION_APPWIDGET_UPDATE)) {
                AppWidgetManager manager = AppWidgetManager.getInstance(context);

                int[] appWidgetId = manager.getAppWidgetIds(new ComponentName(
                        context.getApplicationContext(), ArtPlaceWidget.class));

                int viewIndex = intent.getIntExtra(EXTRA_ITEM, 0);
                Log.d(TAG, "Current view Index:" + viewIndex);

                RemoteViews views = getGridFavArtworks(context, appWidgetId[viewIndex]);

                // Instruct the widget manager to update the widget
                manager.updateAppWidget(appWidgetId, views);
                manager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.appwidget_fav_grid);

                onUpdate(context, manager, appWidgetId);
            }
        }

    }
}

