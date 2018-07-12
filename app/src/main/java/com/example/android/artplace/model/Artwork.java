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

import android.support.v7.util.DiffUtil;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Artwork {

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
    @Expose
    private String slug;

    /*
    Timestamp of when the record was created,
    e.g. "2010-12-20T19:48:55+00:00"
     */
    @SerializedName("created_at")
    @Expose
    private String createdAt;

    @SerializedName("updated_at")
    @Expose
    private String updatedAt;

    /*
    The title of a piece,
    e.g. "Der Kuss (The Kiss)"
     */
    @SerializedName("title")
    @Expose
    private String title;

    /*
    The medium type, such as "painting",
    "photography", "video", etc. Generally u
    sed for broad filters.
    e.g. "Painting"
     */
    @SerializedName("category")
    @Expose
    private String category;

    /*
    Medium, materials and techniques used to make a piece,
    e.g. "Oil and gold leaf on canvas"
     */
    @SerializedName("medium")
    @Expose
    private String medium;

    /*
    Date, can be a range, a year or a period,
    e.g. "1907-1908"
     */
    @SerializedName("date")
    @Expose
    private String date;

    /*
    Whether the work is visible on artsy.net.
     */
    @SerializedName("published")
    @Expose
    private Boolean published;

    @SerializedName("website")
    @Expose
    private String website;

    /*
    The institution which holds the work in their permanent collection,
    e.g. "Österreichische Galerie Belvedere, Vienna"
     */
    @SerializedName("collecting_institution")
    @Expose
    private String collectingInstitution;

    /*
    Information about the artwork, such as link to Wikipedia's image resource,
    e.g. "[Image source](https://commons.wikimedia.org/wiki/File:Klimt_-_The_Kiss.jpg)"
     */
    @SerializedName("additional_information")
    @Expose
    private String additionalInformation;

    /*
    Available image versions.
    [Needed for the @{ImageLinks}]
    "image_versions": [
                    "square",
                    "small",
                    "large_rectangle",
                    "large",
                    "tall",
                    "medium",
                    "medium_rectangle",
                    "larger",
                    "normalized"
                ]
      TODO: Hardcode the version of the image to be always: "large"
     */
    @SerializedName("image_versions")
    @Expose
    private List<String> imageVersions = null;

    /*
    Links to the images for the current artwork
     */
    @SerializedName("_links")
    @Expose
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

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
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

    public Boolean getPublished() {
        return published;
    }

    public void setPublished(Boolean published) {
        this.published = published;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
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

    public static DiffUtil.ItemCallback<Artwork> DIFF_CALLBACK = new DiffUtil.ItemCallback<Artwork>() {
        @Override
        public boolean areItemsTheSame(Artwork oldItem, Artwork newItem) {
            return oldItem.id == newItem.id;
        }

        @Override
        public boolean areContentsTheSame(Artwork oldItem, Artwork newItem) {
            return oldItem.equals(newItem);
        }
    };

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        Artwork artwork = (Artwork) obj;
        return artwork.id == this.id;
    }
}
