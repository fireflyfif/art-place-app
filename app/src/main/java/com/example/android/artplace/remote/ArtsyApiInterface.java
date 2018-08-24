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

package com.example.android.artplace.remote;

import com.example.android.artplace.model.artists.ArtistWrapperResponse;
import com.example.android.artplace.model.artworks.ArtworkWrapperResponse;
import com.example.android.artplace.model.search.SearchWrapperResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface ArtsyApiInterface {

    /**
     * Endpoint for fetching Artworks
     * link: https://api.artsy.net/api/artworks?size=10 + header with token
     *
     * @param itemSize displayed items to the user
     * @return a call to the Artsy Response
     */
    @GET("/api/artworks")
    Call<ArtworkWrapperResponse> getArtsyResponse(@Query("size") int itemSize);

    /**
     * Make call according to the url that is received from the json response
     * @param nextUrl is dynamic link for next page with items
     * @param itemSize is the size of requested items at once
     * @return the response for the next page
     */
    @GET
    Call<ArtworkWrapperResponse> getNextLink(@Url String nextUrl, @Query("size") int itemSize);

    @GET
    Call<ArtistWrapperResponse> getArtistLink(@Url String artistLink);

    /**
     * Endpoint for fetching Artist of the current Artwork
     */
    @GET("/api/artists")
    Call<ArtistWrapperResponse> getArtist(@Query("artwork_id") String artworkId);

    /**
     * Endpoint for Search results
     */
    @GET("/api/search")
    Call<SearchWrapperResponse> getSearchResults(@Query("q") String queryWord, @Query("size") int itemSize, @Query("type") String type);

}
