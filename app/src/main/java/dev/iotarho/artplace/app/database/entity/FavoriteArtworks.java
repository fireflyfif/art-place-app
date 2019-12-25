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

package dev.iotarho.artplace.app.database.entity;

import androidx.recyclerview.widget.DiffUtil;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "fav_artworks")
public class FavoriteArtworks {

    public static DiffUtil.ItemCallback<FavoriteArtworks> DIFF_CALLBACK = new DiffUtil.ItemCallback<FavoriteArtworks>() {

        @Override
        public boolean areItemsTheSame(FavoriteArtworks oldItem, FavoriteArtworks newItem) {
            return oldItem.id == newItem.id;
        }

        @Override
        public boolean areContentsTheSame(FavoriteArtworks oldItem, FavoriteArtworks newItem) {
            return oldItem.equals(newItem);
        }
    };

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo(name = "artwork_id")
    private String artworkId;

    @ColumnInfo(name = "title")
    private String artworkTitle;

    @ColumnInfo(name = "slug")
    private String artworkSlug;

    @ColumnInfo(name = "category")
    private String artworkCategory;

    @ColumnInfo(name = "medium")
    private String artworkMedium;

    @ColumnInfo(name = "date")
    private String artworkDate;

    @ColumnInfo(name = "museum")
    private String artworkMuseum;

    @ColumnInfo(name = "thumbnail")
    private String artworkThumbnailPath;

    @ColumnInfo(name = "image")
    private String artworkImagePath;

    @ColumnInfo(name = "inch")
    private String artworkDimensInch;

    @ColumnInfo(name = "cm")
    private String artworkDimensCm;


    public FavoriteArtworks(String artworkId, String artworkTitle, String artworkSlug, String artworkCategory,
                            String artworkMedium, String artworkDate, String artworkMuseum,
                            String artworkThumbnailPath, String artworkImagePath, String artworkDimensInch,
                            String artworkDimensCm) {
        this.artworkId = artworkId;
        this.artworkTitle = artworkTitle;
        this.artworkSlug = artworkSlug;
        this.artworkCategory = artworkCategory;
        this.artworkMedium = artworkMedium;
        this.artworkDate = artworkDate;
        this.artworkMuseum = artworkMuseum;
        this.artworkThumbnailPath = artworkThumbnailPath;
        this.artworkImagePath = artworkImagePath;
        this.artworkDimensInch = artworkDimensInch;
        this.artworkDimensCm = artworkDimensCm;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getArtworkId() {
        return artworkId;
    }

    public void setArtworkId(String artworkId) {
        this.artworkId = artworkId;
    }

    public String getArtworkTitle() {
        return artworkTitle;
    }

    public void setArtworkTitle(String artworkTitle) {
        this.artworkTitle = artworkTitle;
    }

    public String getArtworkSlug() {
        return artworkSlug;
    }

    public void setArtworkSlug(String artworkSlug) {
        this.artworkSlug = artworkSlug;
    }

    public String getArtworkCategory() {
        return artworkCategory;
    }

    public void setArtworkCategory(String artworkCategory) {
        this.artworkCategory = artworkCategory;
    }

    public String getArtworkMedium() {
        return artworkMedium;
    }

    public void setArtworkMedium(String artworkMedium) {
        this.artworkMedium = artworkMedium;
    }

    public String getArtworkDate() {
        return artworkDate;
    }

    public void setArtworkDate(String artworkDate) {
        this.artworkDate = artworkDate;
    }

    public String getArtworkMuseum() {
        return artworkMuseum;
    }

    public void setArtworkMuseum(String artworkMuseum) {
        this.artworkMuseum = artworkMuseum;
    }

    public String getArtworkThumbnailPath() {
        return artworkThumbnailPath;
    }

    public void setArtworkThumbnailPath(String artworkThumbnailPath) {
        this.artworkThumbnailPath = artworkThumbnailPath;
    }

    public String getArtworkImagePath() {
        return artworkImagePath;
    }

    public void setArtworkImagePath(String artworkImagePath) {
        this.artworkImagePath = artworkImagePath;
    }

    public String getArtworkDimensInch() {
        return artworkDimensInch;
    }

    public void setArtworkDimensInch(String artworkDimensInch) {
        this.artworkDimensInch = artworkDimensInch;
    }

    public String getArtworkDimensCm() {
        return artworkDimensCm;
    }

    public void setArtworkDimensCm(String artworkDimensCm) {
        this.artworkDimensCm = artworkDimensCm;
    }
}
