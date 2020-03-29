package dev.iotarho.artplace.app.ui.searchresults.datasource;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import dev.iotarho.artplace.app.model.Thumbnail;
import dev.iotarho.artplace.app.model.search.ImagesObject;
import dev.iotarho.artplace.app.model.search.LinksResult;
import dev.iotarho.artplace.app.model.search.Result;
import dev.iotarho.artplace.app.utils.StringUtils;
import dev.iotarho.artplace.app.utils.Utils;

public class SearchResultsLogic {

    public static List<Result> getFilteredResults(List<Result> resultList) {
        List<Result> filteredList = new ArrayList<>();
        for (Result result : resultList) {
            LinksResult linksResult = result.getLinks();
            Thumbnail thumbnail = linksResult.getThumbnail();
            String thumbnailLink = "";
            if (thumbnail != null) {
                thumbnailLink = StringUtils.getImageUrl(thumbnail.getHref());
            } else {
                Log.d("SearchResultsLogic", "temp, Thumbnail is null, title: " + result.getTitle());
            }

            String imageLink = "";
            ImagesObject images = linksResult.getImages();
            if (images != null) {
                imageLink = StringUtils.getImageUrl(images.getHref());
                Log.d("SearchResultsLogic", "temp, ImageLink is: " + imageLink);
            }

            boolean isImageMissing = Utils.isNullOrEmpty(thumbnailLink);
            boolean isOtherImagesMissing = Utils.isNullOrEmpty(imageLink);

            if (!isImageMissing || !isOtherImagesMissing) {
                filteredList.add(result);
                Log.d("SearchResultsLogic", "Adding result: " + result.getTitle());
            } else {
                Log.d("SearchResultsLogic", "Removing result without image: " + result.getTitle());
            }
        }
        resultList.addAll(filteredList);
        return filteredList;
    }
}