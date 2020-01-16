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

package dev.iotarho.artplace.app.ui.artistdetail;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import dev.iotarho.artplace.app.model.artists.Artist;
import dev.iotarho.artplace.app.model.artworks.Artwork;
import dev.iotarho.artplace.app.repository.ArtsyRepository;
import dev.iotarho.artplace.app.utils.TokenManager;

public class ArtistsDetailViewModel extends ViewModel {

    private LiveData<List<Artist>> mArtistListData;
    private LiveData<Artist> mArtistData;
    private LiveData<List<Artwork>> mSimilarArtworkLink;

    public ArtistsDetailViewModel() {
    }

    public void initArtistDataFromArtwork(String artistUrl) {
        if (mArtistListData != null) {
            return;
        }
        mArtistListData = ArtsyRepository.getInstance().getArtistFromLink(artistUrl);
    }

    public void initArtistData(String artistUrl) {
        if (mArtistData != null) {
            return;
        }
        mArtistData = ArtsyRepository.getInstance().getArtistInfoFromLink(artistUrl);
    }

    public void initSimilarArtworksData(String similarArtUrl) {
        if (mSimilarArtworkLink != null) {
            return;
        }
        mSimilarArtworkLink = ArtsyRepository.getInstance().getSimilarArtFromLink(similarArtUrl);
    }

    public LiveData<List<Artist>> getArtistDataFromArtwork() {
        return mArtistListData;
    }

    public LiveData<Artist> getArtistData() {
        return mArtistData;
    }

    public LiveData<List<Artwork>> getSimilarArtworksData() {
        return mSimilarArtworkLink;
    }

}
