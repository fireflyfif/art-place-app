package dev.iotarho.artplace.app.utils;

public class Constants {

    public static class General {
        public static final String BASE_ARTSY_URL = "https://api.artsy.net/";
        public static final String HEADER_TOKEN_KEY = "X-XAPP-Token";
    }

    public static class SearchFragment {
        public static final String SEARCH_QUERY_SAVE_STATE = "search_state";
        public static final String SEARCH_TYPE_SAVE_STATE = "search_type";
        public static final String ITEM_CHECKED_SAVE_STATE = "search_type";
        public static final String RESULT_PARCEL_KEY = "results_key";
        public static final String ARTIST_TYPE = "artist";
        public static final String ARTWORK_TYPE = "artwork";
        public static final String GENE_TYPE = "gene";
        public static final String SHOW_TYPE = "show";
        public static final int PAGE_SIZE = 10;
        public static final int INITIAL_SIZE_HINT = 10;
        public static final int PREFETCH_DISTANCE_HINT = 10;
    }

    public static class ArtworksFragment {
        public static final int PAGE_SIZE = 80;
        public static final int INITIAL_SIZE_HINT = 50;
        public static final int PREFETCH_DISTANCE_HINT = 20;
    }
}
