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

package com.example.android.artplace.ui.artworks.datasource;

import android.arch.lifecycle.MutableLiveData;
import android.arch.paging.DataSource;

import com.example.android.artplace.ArtPlaceApp;
import com.example.android.artplace.model.artworks.Artwork;
import com.example.android.artplace.repository.ArtsyRepository;
import com.example.android.artplace.utils.TokenManager;

public class ArtworkDataSourceFactory extends DataSource.Factory<Long, Artwork> {

    private MutableLiveData<ArtworkDataSource> mArtworksDataSourceLiveData;
    private ArtworkDataSource mDataSource;
    private ArtPlaceApp mArtPlaceApp;
    private ArtsyRepository mRepository;


    // TODO: Remove the ArtPlaceApp dependency
    public ArtworkDataSourceFactory(ArtsyRepository repository) {
        //mArtPlaceApp = artPlaceApp;
        mRepository = repository;
        mArtworksDataSourceLiveData = new MutableLiveData<>();
    }

    @Override
    public DataSource<Long, Artwork> create() {
        mDataSource = new ArtworkDataSource(mArtPlaceApp, mRepository);
        mArtworksDataSourceLiveData.postValue(mDataSource);

        return mDataSource;
    }

    public MutableLiveData<ArtworkDataSource> getArtworksDataSourceLiveData() {
        return mArtworksDataSourceLiveData;
    }
}
