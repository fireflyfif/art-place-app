package dev.iotarho.artplace.app.ui.searchdetail;

import dev.iotarho.artplace.app.utils.Utils;

public class SearchDetailLogic {

    public static boolean isArtistInfoInsufficient(String hometown, String location, String nationality, String birthday, String artistDeathString) {
        return (Utils.isNullOrEmpty(hometown) && Utils.isNullOrEmpty(location) && Utils.isNullOrEmpty(nationality) &&
                (Utils.isNullOrEmpty(birthday) || Utils.isNullOrEmpty(artistDeathString)));
    }
}
