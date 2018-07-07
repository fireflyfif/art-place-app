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

package com.example.android.artplace.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

class ImageLinks {

    /*
    Default image thumbnail.
    Doesn't need a size, it's always set to "medium"
     */
    @SerializedName("thumbnail")
    @Expose
    private Thumbnail thumbnail;

    /*
    Curied image location.
     */
    @SerializedName("image")
    @Expose
    private ArtworkImage image;

    /*
    Don't need this now
     */
    /*@SerializedName("self")
    @Expose
    private Self_ self;*/

    /*
    TODO: Implement later on
    An external location on the artsy.net website.
     */
    /*@SerializedName("permalink")
    @Expose
    private Permalink permalink;*/

    /*
    Don't need this now
     */
    /*@SerializedName("genes")
    @Expose
    private Genes genes;*/

    @SerializedName("artists")
    @Expose
    private ArtistsLink artists;

    /*
    Don't need this now
     */
    /*@SerializedName("similar_artworks")
    @Expose
    private SimilarArtworks similarArtworks;*/

    /*
    Don't need this now
     */
    /*@SerializedName("collection_users")
    @Expose
    private CollectionUsers collectionUsers;*/

    public Thumbnail getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(Thumbnail thumbnail) {
        this.thumbnail = thumbnail;
    }

    public ArtworkImage getImage() {
        return image;
    }

    public void setImage(ArtworkImage image) {
        this.image = image;
    }

    public ArtistsLink getArtists() {
        return artists;
    }

    public void setArtists(ArtistsLink artists) {
        this.artists = artists;
    }

}
