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

import java.util.List;

public class ShowContent implements Parcelable {

    @SerializedName("id")
    private String id;

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("updated_at")
    private String updatedAt;

    @SerializedName("name")
    private String name;

    @SerializedName("sortable_name")
    private String sortableName;

    @SerializedName("description")
    private String description;

    @SerializedName("press_release")
    private String pressRelease;

    @SerializedName("start_at")
    private String startAt;

    @SerializedName("end_at")
    private String endAt;

    @SerializedName("status")
    private String status;

    @SerializedName("is_reference")
    private Boolean isReference;

    @SerializedName("is_solo_show")
    private Boolean isSoloShow;

    @SerializedName("is_group_show")
    private Boolean isGroupShow;

    @SerializedName("is_institutional_show")
    private Boolean isInstitutionalShow;

    @SerializedName("is_fair_booth")
    private Boolean isFairBooth;

    @SerializedName("image_versions")
    private List<String> imageVersions = null;

    @SerializedName("_links")
    private LinksResult links;

    public ShowContent() {
    }

    public String getId() {
        return id;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public String getName() {
        return name;
    }

    public String getSortableName() {
        return sortableName;
    }

    public String getDescription() {
        return description;
    }

    public String getPressRelease() {
        return pressRelease;
    }

    public String getStartAt() {
        return startAt;
    }

    public String getEndAt() {
        return endAt;
    }

    public String getStatus() {
        return status;
    }

    public Boolean getIsReference() {
        return isReference;
    }

    public Boolean getIsSoloShow() {
        return isSoloShow;
    }

    public Boolean getIsGroupShow() {
        return isGroupShow;
    }

    public Boolean getIsInstitutionalShow() {
        return isInstitutionalShow;
    }

    public Boolean getIsFairBooth() {
        return isFairBooth;
    }

    public List<String> getImageVersions() {
        return imageVersions;
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

    protected ShowContent(Parcel in) {
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

    public static final Creator<ShowContent> CREATOR = new Creator<ShowContent>() {
        @Override
        public ShowContent createFromParcel(Parcel source) {
            return new ShowContent(source);
        }

        @Override
        public ShowContent[] newArray(int size) {
            return new ShowContent[size];
        }
    };
}
