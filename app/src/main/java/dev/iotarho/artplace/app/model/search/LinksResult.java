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

package dev.iotarho.artplace.app.model.search;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import dev.iotarho.artplace.app.model.Self;
import dev.iotarho.artplace.app.model.Thumbnail;
import dev.iotarho.artplace.app.model.artworks.MainImage;

public class LinksResult implements Parcelable {

    @SerializedName("self")
    private Self self;

    @SerializedName("permalink")
    private Permalink permalink;

    @SerializedName("thumbnail")
    private Thumbnail thumbnail;

    @SerializedName("images")
    private ImagesObject images; // TODO: This needs to be moved from here

    @SerializedName("image")
    private MainImage image;

    public Self getSelf() {
        return self;
    }

    public Permalink getPermalink() {
        return permalink;
    }

    public Thumbnail getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(Thumbnail thumbnail) {
        this.thumbnail = thumbnail;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.self, flags);
        dest.writeParcelable(this.permalink, flags);
        dest.writeParcelable(this.thumbnail, flags);
        dest.writeParcelable(this.images, flags);
        dest.writeParcelable(this.image, flags);
    }

    public LinksResult() {
    }

    protected LinksResult(Parcel in) {
        this.self = in.readParcelable(Self.class.getClassLoader());
        this.permalink = in.readParcelable(Permalink.class.getClassLoader());
        this.thumbnail = in.readParcelable(Thumbnail.class.getClassLoader());
        this.images = in.readParcelable(ImagesObject.class.getClassLoader());
        this.image = in.readParcelable(MainImage.class.getClassLoader());
    }

    public static final Creator<LinksResult> CREATOR = new Creator<LinksResult>() {
        @Override
        public LinksResult createFromParcel(Parcel source) {
            return new LinksResult(source);
        }

        @Override
        public LinksResult[] newArray(int size) {
            return new LinksResult[size];
        }
    };

    public ImagesObject getImages() {
        return images;
    }

    public MainImage getImage() {
        return image;
    }
}
