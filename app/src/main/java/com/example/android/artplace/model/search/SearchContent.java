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

package com.example.android.artplace.model.search;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SearchContent implements Parcelable {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("sortable_name")
    @Expose
    private String sortableName;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("press_release")
    @Expose
    private String pressRelease;
    @SerializedName("start_at")
    @Expose
    private String startAt;
    @SerializedName("end_at")
    @Expose
    private String endAt;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("is_reference")
    @Expose
    private Boolean isReference;
    @SerializedName("is_solo_show")
    @Expose
    private Boolean isSoloShow;
    @SerializedName("is_group_show")
    @Expose
    private Boolean isGroupShow;
    @SerializedName("is_institutional_show")
    @Expose
    private Boolean isInstitutionalShow;
    @SerializedName("is_fair_booth")
    @Expose
    private Boolean isFairBooth;
    @SerializedName("image_versions")
    @Expose
    private List<String> imageVersions = null;
    @SerializedName("_links")
    @Expose
    private LinksResult links;

    public SearchContent() {}



    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSortableName() {
        return sortableName;
    }

    public void setSortableName(String sortableName) {
        this.sortableName = sortableName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPressRelease() {
        return pressRelease;
    }

    public void setPressRelease(String pressRelease) {
        this.pressRelease = pressRelease;
    }

    public String getStartAt() {
        return startAt;
    }

    public void setStartAt(String startAt) {
        this.startAt = startAt;
    }

    public String getEndAt() {
        return endAt;
    }

    public void setEndAt(String endAt) {
        this.endAt = endAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Boolean getIsReference() {
        return isReference;
    }

    public void setIsReference(Boolean isReference) {
        this.isReference = isReference;
    }

    public Boolean getIsSoloShow() {
        return isSoloShow;
    }

    public void setIsSoloShow(Boolean isSoloShow) {
        this.isSoloShow = isSoloShow;
    }

    public Boolean getIsGroupShow() {
        return isGroupShow;
    }

    public void setIsGroupShow(Boolean isGroupShow) {
        this.isGroupShow = isGroupShow;
    }

    public Boolean getIsInstitutionalShow() {
        return isInstitutionalShow;
    }

    public void setIsInstitutionalShow(Boolean isInstitutionalShow) {
        this.isInstitutionalShow = isInstitutionalShow;
    }

    public Boolean getIsFairBooth() {
        return isFairBooth;
    }

    public void setIsFairBooth(Boolean isFairBooth) {
        this.isFairBooth = isFairBooth;
    }

    public List<String> getImageVersions() {
        return imageVersions;
    }

    public void setImageVersions(List<String> imageVersions) {
        this.imageVersions = imageVersions;
    }

    public LinksResult getLinks() {
        return links;
    }

    public void setLinks(LinksResult links) {
        this.links = links;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.createdAt);
        dest.writeString(this.updatedAt);
        dest.writeString(this.name);
        dest.writeString(this.sortableName);
        dest.writeString(this.description);
        dest.writeString(this.pressRelease);
        dest.writeString(this.startAt);
        dest.writeString(this.endAt);
        dest.writeString(this.status);
        dest.writeValue(this.isReference);
        dest.writeValue(this.isSoloShow);
        dest.writeValue(this.isGroupShow);
        dest.writeValue(this.isInstitutionalShow);
        dest.writeValue(this.isFairBooth);
        dest.writeStringList(this.imageVersions);
        dest.writeParcelable(this.links, flags);
    }

    protected SearchContent(Parcel in) {
        this.id = in.readString();
        this.createdAt = in.readString();
        this.updatedAt = in.readString();
        this.name = in.readString();
        this.sortableName = in.readString();
        this.description = in.readString();
        this.pressRelease = in.readString();
        this.startAt = in.readString();
        this.endAt = in.readString();
        this.status = in.readString();
        this.isReference = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.isSoloShow = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.isGroupShow = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.isInstitutionalShow = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.isFairBooth = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.imageVersions = in.createStringArrayList();
        this.links = in.readParcelable(LinksResult.class.getClassLoader());
    }

    public static final Parcelable.Creator<SearchContent> CREATOR = new Parcelable.Creator<SearchContent>() {
        @Override
        public SearchContent createFromParcel(Parcel source) {
            return new SearchContent(source);
        }

        @Override
        public SearchContent[] newArray(int size) {
            return new SearchContent[size];
        }
    };
}
