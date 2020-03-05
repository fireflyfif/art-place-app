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

package dev.iotarho.artplace.app.model.artworks;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.recyclerview.widget.DiffUtil;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import dev.iotarho.artplace.app.model.ImageLinks;

public class Artwork implements Parcelable {

    public static DiffUtil.ItemCallback<Artwork> DIFF_CALLBACK = new DiffUtil.ItemCallback<Artwork>() {
        @Override
        public boolean areItemsTheSame(Artwork oldItem, Artwork newItem) {
            return oldItem.id.equals(newItem.id);
        }

        @Override
        public boolean areContentsTheSame(Artwork oldItem, Artwork newItem) {
            return oldItem.equals(newItem);
        }
    };

    @Override
    public boolean equals(Object obj) {
        if (obj.getClass() == getClass()) {
            return true;
        }

        Artwork artwork = (Artwork) obj;
        return artwork.id.equals(this.id);
    }

    /*
    Id of the Artwork,
    e.g. "4d8b92eb4eb68a1b2c000968"
     */
    @SerializedName("id")
    @Expose
    private String id;

    /*
    Name of the Artwork's creator,
    e.g. "gustav-klimt-der-kuss-the-kiss"
     */
    @SerializedName("slug")
    private String slug;

    /*
    The title of a piece,
    e.g. "Der Kuss (The Kiss)"
     */
    @SerializedName("title")
    private String title;

    /*
    The medium type, such as "painting",
    "photography", "video", etc. Generally u
    sed for broad filters.
    e.g. "Painting"
     */
    @SerializedName("category")
    private String category;

    /*
    Medium, materials and techniques used to make a piece,
    e.g. "Oil and gold leaf on canvas"
     */
    @SerializedName("medium")
    private String medium;

    /*
    Date, can be a range, a year or a period,
    e.g. "1907-1908"
     */
    @SerializedName("date")
    private String date;

    @SerializedName("dimensions")
    private Dimensions dimensions;

    /*
    The institution which holds the work in their permanent collection,
    e.g. "Österreichische Galerie Belvedere, Vienna"
     */
    @SerializedName("collecting_institution")
    private String collectingInstitution;

    /*
    Information about the artwork, such as link to Wikipedia's image resource,
    e.g. "[Image source](https://commons.wikimedia.org/wiki/File:Klimt_-_The_Kiss.jpg)"
     */
    @SerializedName("additional_information")
    private String additionalInformation;

    /*
    Available image versions.
    [Needed for the @{ImageLinks}]
    "image_versions": [ "square", "small",  "large_rectangle", "large", "tall", "medium", "medium_rectangle", "larger", "normalized" ]
     */
    @SerializedName("image_versions")
    private List<String> imageVersions = null;

    /*
    Links to the images for the current artwork
     */
    @SerializedName("_links")
    private ImageLinks links;


    public String  getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getMedium() {
        return medium;
    }

    public void setMedium(String medium) {
        this.medium = medium;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Dimensions getDimensions() {
        return dimensions;
    }

    public void setDimensions(Dimensions dimensions) {
        this.dimensions = dimensions;
    }

    public String getCollectingInstitution() {
        return collectingInstitution;
    }

    public void setCollectingInstitution(String collectingInstitution) {
        this.collectingInstitution = collectingInstitution;
    }

    public String getAdditionalInformation() {
        return additionalInformation;
    }

    public void setAdditionalInformation(String additionalInformation) {
        this.additionalInformation = additionalInformation;
    }

    public List<String> getImageVersions() {
        return imageVersions;
    }

    public void setImageVersions(List<String> imageVersions) {
        this.imageVersions = imageVersions;
    }

    public ImageLinks getLinks() {
        return links;
    }

    public void setLinks(ImageLinks links) {
        this.links = links;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.slug);
        dest.writeString(this.title);
        dest.writeString(this.category);
        dest.writeString(this.medium);
        dest.writeString(this.date);
        dest.writeParcelable(this.dimensions, flags);
        dest.writeString(this.collectingInstitution);
        dest.writeString(this.additionalInformation);
        dest.writeStringList(this.imageVersions);
        dest.writeParcelable(this.links, flags);
    }

    public Artwork() {
    }

    protected Artwork(Parcel in) {
        this.id = in.readString();
        this.slug = in.readString();
        this.title = in.readString();
        this.category = in.readString();
        this.medium = in.readString();
        this.date = in.readString();
        this.dimensions = in.readParcelable(Dimensions.class.getClassLoader());
        this.collectingInstitution = in.readString();
        this.additionalInformation = in.readString();
        this.imageVersions = in.createStringArrayList();
        this.links = in.readParcelable(ImageLinks.class.getClassLoader());
    }

    public static final Creator<Artwork> CREATOR = new Creator<Artwork>() {
        @Override
        public Artwork createFromParcel(Parcel source) {
            return new Artwork(source);
        }

        @Override
        public Artwork[] newArray(int size) {
            return new Artwork[size];
        }
    };
}
