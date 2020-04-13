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

package dev.iotarho.artplace.app.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import dev.iotarho.artplace.app.model.artworks.ArtistsLink;
import dev.iotarho.artplace.app.model.artworks.MainImage;
import dev.iotarho.artplace.app.model.genes.Genes;
import dev.iotarho.artplace.app.model.search.Permalink;

public class ImageLinks implements Parcelable {

    /*
    Default image thumbnail.
    Doesn't need a size, it's always set to "medium"
     */
    @SerializedName("thumbnail")
    private Thumbnail thumbnail;

    /*
    Curied image location.
     */
    @SerializedName("image")
    private MainImage image;

    /*
    An external location on the artsy.net website.
    Used for sharing outside the app
     */
    @SerializedName("permalink")
    private Permalink permalink;

    @SerializedName("artworks")
    private ArtworksLink artworksLink;

    /*
    Genes are like genres in the Artsy API
     */
    @SerializedName("genes")
    private Genes genes;

    @SerializedName("similar_artworks")
    private SimilarArtworksLink similarArtworks;

    @SerializedName("artists")
    private ArtistsLink artists;

    public ArtworksLink getArtworksLink() {
        return artworksLink;
    }

    public Thumbnail getThumbnail() {
        return thumbnail;
    }

    public MainImage getImage() {
        return image;
    }

    public ArtistsLink getArtists() {
        return artists;
    }

    public Permalink getPermalink() {
        return permalink;
    }

    public Genes getGenes() {
        return genes;
    }

    public SimilarArtworksLink getSimilarArtworks() {
        return similarArtworks;
    }

    public ImageLinks() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.thumbnail, flags);
        dest.writeParcelable(this.image, flags);
        dest.writeParcelable(this.permalink, flags);
        dest.writeParcelable(this.artworksLink, flags);
        dest.writeParcelable(this.genes, flags);
        dest.writeParcelable(this.similarArtworks, flags);
        dest.writeParcelable(this.artists, flags);
    }

    protected ImageLinks(Parcel in) {
        this.thumbnail = in.readParcelable(Thumbnail.class.getClassLoader());
        this.image = in.readParcelable(MainImage.class.getClassLoader());
        this.permalink = in.readParcelable(Permalink.class.getClassLoader());
        this.artworksLink = in.readParcelable(ArtworksLink.class.getClassLoader());
        this.genes = in.readParcelable(Genes.class.getClassLoader());
        this.similarArtworks = in.readParcelable(SimilarArtworksLink.class.getClassLoader());
        this.artists = in.readParcelable(ArtistsLink.class.getClassLoader());
    }

    public static final Creator<ImageLinks> CREATOR = new Creator<ImageLinks>() {
        @Override
        public ImageLinks createFromParcel(Parcel source) {
            return new ImageLinks(source);
        }

        @Override
        public ImageLinks[] newArray(int size) {
            return new ImageLinks[size];
        }
    };
}
