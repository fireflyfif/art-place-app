package dev.iotarho.artplace.app.utils;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.card.MaterialCardView;
import com.ms.square.android.expandabletextview.ExpandableTextView;

import java.util.List;

import dev.iotarho.artplace.app.model.ImageLinks;
import dev.iotarho.artplace.app.model.artists.Artist;
import dev.iotarho.artplace.app.model.artworks.MainImage;
import dev.iotarho.artplace.app.ui.searchdetail.SearchDetailLogic;

public class ArtistInfoUtils {

    private static final String TAG = ArtistInfoUtils.class.getSimpleName();

    public static void setupArtistUi(Artist currentArtist, MaterialCardView artistCardView, TextView artistName,
                                     TextView artistHomeTown, TextView hometownLabel, TextView artistLifespan,
                                     View artistDivider, TextView artistLocation, TextView locationLabel,
                                     TextView artistNationality, TextView artistNationalityLabel,
                                     ExpandableTextView artistBio, TextView artistBioLabel) {
        if (currentArtist == null) {
            Log.d(TAG, "Artist is null");
            return;
        }
        String artistNameString = currentArtist.getName();
        String hometown = currentArtist.getHometown();
        String birthday = currentArtist.getBirthday();
        String artistDeathString = currentArtist.getDeathday();
        String location = currentArtist.getLocation();
        String nationality = currentArtist.getNationality();
        String artistBiography = currentArtist.getBiography();

        // Meet necessary criteria for showing up artist card
        if (SearchDetailLogic.isArtistInfoInsufficient(hometown, location, nationality, birthday, artistDeathString)) {
            Log.w(TAG, "Not enough artist info to show details.");
            return;
        }

        // Name
        artistName.setText(Utils.isNullOrEmpty(artistNameString) ? null : artistNameString);
        // Home town
        if (!Utils.isNullOrEmpty(hometown)) {
            artistHomeTown.setVisibility(View.VISIBLE);
            hometownLabel.setVisibility(View.VISIBLE);
            artistHomeTown.setText(hometown);
        }
        // Birth and dead date
        String lifespanConcatString = birthday + " - " + artistDeathString;
        if (!Utils.isNullOrEmpty(lifespanConcatString)) {
            artistLifespan.setText(lifespanConcatString);
            artistDivider.setVisibility(View.VISIBLE);
        }
        // Location
        if (!Utils.isNullOrEmpty(location)) {
            artistLocation.setText(location);
            artistLocation.setVisibility(View.VISIBLE);
            locationLabel.setVisibility(View.VISIBLE);
        }
        // Nationality
        if (!Utils.isNullOrEmpty(nationality)) {
            artistNationality.setText(nationality);
            artistNationality.setVisibility(View.VISIBLE);
            artistNationalityLabel.setVisibility(View.VISIBLE);
        }
        // Biography
        if (!Utils.isNullOrEmpty(artistBiography)) {
            artistBio.setText(artistBiography);
            artistBio.setVisibility(View.VISIBLE);
            artistBioLabel.setVisibility(View.VISIBLE);
        }
        artistCardView.setVisibility(View.VISIBLE);
    }

    public static void displayArtistImage(Artist currentArtist, ImageView imageView) {
        ImageLinks imageLinksObject = currentArtist.getLinks();
        // Get the list of image versions first
        List<String> imageVersionList = currentArtist.getImageVersions();
        MainImage mainImageObject = imageLinksObject.getImage();
        ImageUtils.displayImage(mainImageObject, imageVersionList, imageView);
    }
}
