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

package dev.iotarho.artplace.app.utils;

import android.util.Log;

import java.text.Normalizer;

import dev.iotarho.artplace.app.model.artworks.Artwork;
import dev.iotarho.artplace.app.model.search.ShowContent;

// Helper method for extracting the name of the Artist from the slug received for each artwork response
// in the form of (e.g. "gustav-klimt-der-kuss-the-kiss")
public class StringUtils {

    private static final String TAG = StringUtils.class.getSimpleName();

    public static String getArtistNameFromSlug(Artwork artwork) {
        String artistNameFromSlug;

        // Get the title of the current artwork
        String artworkTitleString = artwork.getTitle();

        // The Slug contains the name of the artist as well as the name of the artwork
        // e.g. "gustav-klimt-der-kuss-the-kiss"
        String artworkSlug = artwork.getSlug();
        Log.d(TAG, "StringUtils: Retrieved Slug string: " + artworkSlug);

        // Remove all "-" from the slug
        String newSlugString = artworkSlug.replaceAll("-", " ").toLowerCase();
        Log.d(TAG, "StringUtils: New Slug string: " + newSlugString);

        // Clear the title of the artwork from any punctuations or characters that are not English letters
        String newTitleString = artworkTitleString
                .toLowerCase()
                .replaceAll("'", "")
                .replaceAll("\\.", "")
                .replaceAll(",", "")
                .replaceAll(":", "")
                .replaceAll("-", " ")
                .replaceAll("[()]", "");

        Log.d(TAG, "StringUtils: New title string: " + newTitleString);

        // Normalize the letters
        // Tutorial here: https://www.drillio.com/en/2011/java-remove-accent-diacritic/
        String normalizedTitleString = Normalizer.normalize(newTitleString, Normalizer.Form.NFD);
        String removedDiacriticsFromTitle = normalizedTitleString
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "").trim();

        if (newSlugString.contains(removedDiacriticsFromTitle)) {

            artistNameFromSlug = newSlugString
                    .replace(removedDiacriticsFromTitle, "").trim();
            Log.d(TAG, "StringUtils: Artist Name From Slug: " + artistNameFromSlug);

            // Check if the name is empty or contains any numbers
            if (artistNameFromSlug.equals(("")) || artistNameFromSlug.equals("[0-9]")) {
                artistNameFromSlug = "N/A";
            }

            // Return the name of the Artist
            return artistNameFromSlug;
        } else {
            // Return just "Artist"
            return "Artist";
        }
    }

    public static String getDate(String startDate) {
        // startDate = "2017-05-31T18:30:05+00:00"
        int indexOf = startDate.indexOf("T");
        if (indexOf == -1) {
            return "";
        }
        return startDate.substring(0, indexOf); //this will give 2017-05-31
    }
}
