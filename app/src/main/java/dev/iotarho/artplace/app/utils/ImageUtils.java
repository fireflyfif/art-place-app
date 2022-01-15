package dev.iotarho.artplace.app.utils;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.ablanco.zoomy.Zoomy;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.facebook.imagepipeline.request.Postprocessor;
import com.squareup.picasso.Picasso;

import java.util.List;

import dev.iotarho.artplace.app.R;
import dev.iotarho.artplace.app.model.ImageLinks;
import dev.iotarho.artplace.app.model.artworks.Artwork;
import dev.iotarho.artplace.app.model.artworks.MainImage;
import jp.wasabeef.fresco.processors.BlurPostprocessor;

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
        String artworkImgLinkString = StringUtils.getImageUrl(mainImageObject.getHref());
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

    /**
     * Make the image blurry with the help of SimpleDraweeView View
     * Tutorial:https://android.jlelse.eu/android-image-blur-using-fresco-vs-picasso-ea095264abbf
     */
    public static void makeImageBlurry(Postprocessor postProcessor,
                                       SimpleDraweeView blurryImage,
                                       String linkString) {
        // Instantiate Image Request using Post Processor as parameter
        ImageRequest imageRequest = ImageRequestBuilder.newBuilderWithSource(Uri.parse(linkString))
                .setPostprocessor(postProcessor)
                .build();

        // Instantiate Controller
        PipelineDraweeController controller = (PipelineDraweeController) Fresco.newDraweeControllerBuilder()
                .setImageRequest(imageRequest)
                .setOldController(blurryImage.getController())
                .build();

        // Load the blurred image
        blurryImage.setController(controller);
    }
}
