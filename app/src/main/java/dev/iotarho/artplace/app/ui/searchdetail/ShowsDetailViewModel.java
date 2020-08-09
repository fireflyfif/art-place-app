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

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.List;

import dev.iotarho.artplace.app.AppExecutors;
import dev.iotarho.artplace.app.model.artworks.MainImage;
import dev.iotarho.artplace.app.model.genes.GeneContent;
import dev.iotarho.artplace.app.model.search.ShowContent;
import dev.iotarho.artplace.app.repository.ArtsyRepository;
import dev.iotarho.artplace.app.utils.StringUtils;


public class ShowsDetailViewModel extends ViewModel {

    public static final String ARTIST_BIO_CSS_QUERY = "span[class^=\"ArtistBio\"]";


    private LiveData<ShowContent> showContentData = new MutableLiveData<>();
    private LiveData<GeneContent> geneResultData = new MutableLiveData<>();
    private MutableLiveData<String> artistBiographyData = new MutableLiveData<>();
    private MutableLiveData<String> artworkImageData = new MutableLiveData<>();
    private ArtsyRepository artsyRepository;

    public ShowsDetailViewModel(ArtsyRepository artsyRepository) {
        this.artsyRepository = artsyRepository;
    }

    public void initSearchLink(String selfLink) {
        showContentData = artsyRepository.getSearchContentLink(selfLink);
    }

    public void initGenesResultFromLink(String selfLink) {
        geneResultData = artsyRepository.getGenesContent(selfLink);
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

    public LiveData<GeneContent> getGeneResultData() {
        return geneResultData;
    }

    private void loadBioFromWeb(String webLink) {
        AppExecutors.getInstance().networkIO().execute(() -> {
            try {
                Document doc = Jsoup.connect(webLink).get();
                String title = doc.title();
                Elements links = doc.select("a[href]");
                Elements bioText = doc.select(ARTIST_BIO_CSS_QUERY);
                Log.d("ShowsDetailViewModel", "temp, bioText= " + bioText);
                // use postValue() instead of the setValue() for posting on a background thread
                artistBiographyData.postValue(bioText.text());
            } catch (IOException e) {
                Log.e("ShowsDetailViewModel", "error when connecting to web link");
            }
        });
    }

    /*public MutableLiveData<String> getArtworkLargeImage(@NonNull List<String> imageVersionList, @NonNull MainImage mainImageObject) {
        // Get the link for the current artwork,
        // e.g.: "https://d32dm0rphc51dk.cloudfront.net/rqoQ0ln0TqFAf7GcVwBtTw/{image_version}.jpg"
        if (mainImageObject.getHref() == null) {
            return null;
        }
        String artworkImgLinkString = mainImageObject.getHref();
        // Replace the {image_version} from the artworkImgLinkString with
        // the wanted version, e.g. "large"
        String value = StringUtils.extractImageLink(getVersionImage(imageVersionList, IMAGE_LARGE), artworkImgLinkString);
        artworkImageData.setValue(value);
        return artworkImageData;
    }*/


    private String getVersionImage(List<String> imageVersionList, String version) {
        if (imageVersionList.contains(version)) {
            return imageVersionList.get(imageVersionList.indexOf(version));
        } else {
            return imageVersionList.get(0);  // Get the first one no matter what is the value
        }
    }
}
