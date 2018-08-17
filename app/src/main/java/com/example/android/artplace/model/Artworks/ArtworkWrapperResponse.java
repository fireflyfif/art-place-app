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

package com.example.android.artplace.model.Artworks;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.android.artplace.model.Links;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

// Model class for the response of the Artsy API
public class ArtworkWrapperResponse implements Parcelable {

    @SerializedName("total_count")
    @Expose
    private Integer totalCount;

    @SerializedName("_links")
    @Expose
    private Links links;

    @SerializedName("_embedded")
    @Expose
    private EmbeddedArtworks embeddedArtworks;

    public Integer getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }

    public Links getLinks() {
        return links;
    }

    public void setLinks(Links links) {
        this.links = links;
    }

    public EmbeddedArtworks getEmbeddedArtworks() {
        return embeddedArtworks;
    }

    public void setEmbeddedArtworks(EmbeddedArtworks embeddedArtworks) {
        this.embeddedArtworks = embeddedArtworks;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.totalCount);
        dest.writeParcelable(this.links, flags);
        dest.writeParcelable(this.embeddedArtworks, flags);
    }

    public ArtworkWrapperResponse() {
    }

    protected ArtworkWrapperResponse(Parcel in) {
        this.totalCount = (Integer) in.readValue(Integer.class.getClassLoader());
        this.links = in.readParcelable(Links.class.getClassLoader());
        this.embeddedArtworks = in.readParcelable(EmbeddedArtworks.class.getClassLoader());
    }

    public static final Creator<ArtworkWrapperResponse> CREATOR = new Creator<ArtworkWrapperResponse>() {
        @Override
        public ArtworkWrapperResponse createFromParcel(Parcel source) {
            return new ArtworkWrapperResponse(source);
        }

        @Override
        public ArtworkWrapperResponse[] newArray(int size) {
            return new ArtworkWrapperResponse[size];
        }
    };
}
