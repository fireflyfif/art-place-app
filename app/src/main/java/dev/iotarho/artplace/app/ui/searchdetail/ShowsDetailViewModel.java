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

package dev.iotarho.artplace.app.ui.searchdetail;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;

import dev.iotarho.artplace.app.AppExecutors;
import dev.iotarho.artplace.app.model.search.ShowContent;
import dev.iotarho.artplace.app.repository.ArtsyRepository;

import static dev.iotarho.artplace.app.ui.searchdetail.SearchDetailActivity.ARTIST_BIO;

public class ShowsDetailViewModel extends ViewModel {

    private LiveData<ShowContent> showContentData;
    private MutableLiveData<String> artistBiographyData;

    public ShowsDetailViewModel() {
    }

    public void initSearchLink(String selfLink) {
        if (showContentData != null) {
            return;
        }
        showContentData = ArtsyRepository.getInstance().getSearchContentLink(selfLink);
    }

    public void initBioFromWeb(String webLink) {
        loadBioFromWeb(webLink);
    }

    public LiveData<ShowContent> getResultSelfLink() {
        return showContentData;
    }

    public LiveData<String> getBioFromWeb() {
        return artistBiographyData;
    }

    private MutableLiveData<String> loadBioFromWeb(String webLink) {
        if (artistBiographyData == null) {
            artistBiographyData = new MutableLiveData<>("");
        }

        AppExecutors.getInstance().networkIO().execute(() -> {
            try {
                Document doc = Jsoup.connect(webLink).get();
                String title = doc.title();
                Elements links = doc.select("a[href]");
                Elements fresnelContainer = doc.getElementsByClass(ARTIST_BIO);
                artistBiographyData.postValue(fresnelContainer.text());
                Log.d("ShowsDetailViewModel", "temp, artistBio = " + fresnelContainer.text());
            } catch (IOException e) {
                Log.e("ShowsDetailViewModel", "error when connecting to web link");
            }
        });
        return artistBiographyData;
    }
}
