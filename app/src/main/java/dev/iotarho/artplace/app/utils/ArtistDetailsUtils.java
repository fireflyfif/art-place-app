package dev.iotarho.artplace.app.utils;

import java.util.List;

import dev.iotarho.artplace.app.model.ImageLinks;
import dev.iotarho.artplace.app.model.artworks.Artwork;
import dev.iotarho.artplace.app.model.artworks.MainImage;

public class ArtistDetailsUtils {

    private static final String IMAGE_LARGE = "large";
    private static final String IMAGE_SQUARE = "square";
    private static final String IMAGE_LARGER = "larger";

    public static String getVersionImage(List<String> imageVersionList, String version) {
        if (imageVersionList.contains(version)) {
            return imageVersionList.get(imageVersionList.indexOf(version));
        } else {
            return imageVersionList.get(0);  // Get the first one no matter what is the value
        }
    }

    private static String extractImageLink(String stringFinal, String stringFull) {
        return stringFull.replaceAll("\\{.*?\\}", stringFinal);
    }

    public static String getLargeImageUrl(Artwork currentArtwork, ImageLinks imageLinksObject) {
        MainImage mainImageObject = imageLinksObject.getImage();
        if (currentArtwork.getImageVersions() == null) {
            return null;
        }
        List<String> imageVersionList = currentArtwork.getImageVersions();
        // Get the link for the current artwork,
        // e.g.: "https://d32dm0rphc51dk.cloudfront.net/rqoQ0ln0TqFAf7GcVwBtTw/{image_version}.jpg"
        String artworkImgLinkString = mainImageObject.getHref();
        // Replace the {image_version} from the artworkImgLinkString with
        // the wanted version, e.g. "large"
        return extractImageLink(getVersionImage(imageVersionList, IMAGE_LARGE), artworkImgLinkString);
    }

    public static String getSquareImageUrl(Artwork currentArtwork, ImageLinks imageLinksObject) {
        MainImage mainImageObject = imageLinksObject.getImage();
        if (currentArtwork.getImageVersions() == null) {
            return null;
        }
        List<String> imageVersionList = currentArtwork.getImageVersions();
        // Get the link for the current artwork,
        // e.g.: "https://d32dm0rphc51dk.cloudfront.net/rqoQ0ln0TqFAf7GcVwBtTw/{image_version}.jpg"
        String artworkImgLinkString = mainImageObject.getHref();
        return extractImageLink(ArtistDetailsUtils.getVersionImage(imageVersionList, IMAGE_SQUARE), artworkImgLinkString);
    }
}
