package dev.iotarho.artplace.app.ui.searchresults.datasource;

import android.util.Log;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import dev.iotarho.artplace.app.model.Thumbnail;
import dev.iotarho.artplace.app.model.search.LinksResult;
import dev.iotarho.artplace.app.model.search.Result;
import dev.iotarho.artplace.app.utils.StringUtils;
import dev.iotarho.artplace.app.utils.Utils;

import static dev.iotarho.artplace.app.utils.Constants.SearchFragment.ARTIST_TYPE;
import static dev.iotarho.artplace.app.utils.Constants.SearchFragment.ARTWORK_TYPE;
import static dev.iotarho.artplace.app.utils.Constants.SearchFragment.GENE_TYPE;
import static dev.iotarho.artplace.app.utils.Constants.SearchFragment.SHOW_TYPE;

public class SearchResultsLogic {

    public static List<Result> getFilteredResults(List<Result> resultList) {
        List<Result> filteredList = new ArrayList<>();
        for (Result result : resultList) {
            if (result.getLinks() == null) {
                return null;
            }

            LinksResult linksResult = result.getLinks();
            Thumbnail thumbnail = linksResult.getThumbnail();
            String thumbnailLink = "";
            if (thumbnail != null) {
                thumbnailLink = StringUtils.getImageUrl(thumbnail.getHref());
            } else {
                Log.d("SearchResultsLogic", "temp, Thumbnail is null, title: " + result.getTitle());
            }

            boolean isImageMissing = Utils.isNullOrEmpty(thumbnailLink);

            if (!isImageMissing && isKnownType(result)) {
                filteredList.add(result);
                Log.d("SearchResultsLogic", "Adding result: " + result.getTitle());
            } else {
                // just don't add it to the list
                Log.d("SearchResultsLogic", "Removing result without image: " + result.getTitle());
            }
        }
        resultList.addAll(filteredList);
        return filteredList;
    }

    private static boolean isKnownType(Result result) {
        return ((result.getType().equals(SHOW_TYPE)) ||
                (result.getType().equals(ARTIST_TYPE)) ||
                (result.getType().equals(ARTWORK_TYPE)) ||
                (result.getType().equals(GENE_TYPE)));
    }

    private static Comparator<Result> resultComparator = (o1, o2) -> {
        if (o1.getTitle().equals(o2.getTitle())) {
            return 0;
        }
        if (o1.getDescription().contains(o2.getDescription())) {
            return 0;
        }
        return 1;
    };
}
