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

package app.app.android.artplace.database;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import android.content.Context;
import android.util.Log;

import app.app.android.artplace.database.dao.FavArtworksDao;
import app.app.android.artplace.database.entity.FavoriteArtworks;

// Helper tutorial: https://medium.com/@ajaysaini.official/building-database-with-room-persistence-library-ecf7d0b8f3e9
@Database(entities = {FavoriteArtworks.class}, version = 1, exportSchema = false)
public abstract class ArtworksDatabase extends RoomDatabase {

    private static final String TAG = ArtworksDatabase.class.getSimpleName();
    private static ArtworksDatabase INSTANCE;
    private static final Object LOCK = new Object();
    private static final String ARTPLACE_DB_NAME = "artplace.db";

    // Reference the DAO from the database class
    public abstract FavArtworksDao favArtworksDao();

    public static ArtworksDatabase getInstance(Context context) {
        if (INSTANCE == null) {

            synchronized (LOCK) {
                INSTANCE = create(context);
            }
        }

        Log.d(TAG, "Getting the database instance");
        return INSTANCE;
    }

    private static ArtworksDatabase create(Context context) {
        Log.d(TAG, "Creating new database instance");
        RoomDatabase.Builder<ArtworksDatabase> databaseBuilder =
                Room.databaseBuilder(context.getApplicationContext(),
                        ArtworksDatabase.class, ARTPLACE_DB_NAME);

        return (databaseBuilder.build());
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }

}
