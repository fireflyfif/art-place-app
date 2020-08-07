package dev.iotarho.artplace.app.utils;

import android.app.Activity;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.ablanco.zoomy.Zoomy;
import com.squareup.picasso.Picasso;

import java.util.List;

import dev.iotarho.artplace.app.R;
import dev.iotarho.artplace.app.model.ImageLinks;
import dev.iotarho.artplace.app.model.artworks.Artwork;
import dev.iotarho.artplace.app.model.artworks.MainImage;

public class ImageUtils {

    private static final String REGEX = "\\{.*?\\}";
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

    public static String extractImageLink(String stringFinal, String stringFull) {
        return stringFull.replaceAll(REGEX, stringFinal);
    }

    public static String getLargeImageUrl(@NonNull List<String> imageVersionList, @NonNull MainImage mainImageObject) {
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
        return extractImageLink(ImageUtils.getVersionImage(imageVersionList, IMAGE_SQUARE), artworkImgLinkString);
    }

    public static void displayImage(MainImage mainImageObject, List<String> imageVersionList, ImageView imageView) {
        String largeArtworkLink = ImageUtils.getLargeImageUrl(imageVersionList, mainImageObject);
        // Set the large image with Picasso
        Picasso.get()
                .load(largeArtworkLink)
                .placeholder(R.color.color_primary)
                .error(R.color.color_error)
                .into(imageView);
    }

    public static void setupZoomyImage(Activity activity, ImageView imageView) {
        Zoomy.Builder builder = new Zoomy.Builder(activity)
                .target(imageView)
                .enableImmersiveMode(false)
                .animateZooming(false);
        builder.register();
    }
}
