package dev.iotarho.artplace.app.ui.artworkdetail;

import androidx.lifecycle.ViewModel;

import dev.iotarho.artplace.app.callbacks.ResultFromDbCallback;
import dev.iotarho.artplace.app.database.entity.FavoriteArtworks;
import dev.iotarho.artplace.app.repository.FavArtRepository;

public class ArtworkDetailViewModel extends ViewModel {

    private FavArtRepository favArtRepository;

    public ArtworkDetailViewModel(FavArtRepository favArtRepository) {
        this.favArtRepository = favArtRepository;
    }

    public void insertItem(FavoriteArtworks favArtwork) {
        favArtRepository.insertItem(favArtwork);
    }

    public void getItemById(String artworkId, ResultFromDbCallback resultFromDbCallback) {
        favArtRepository.executeGetItemById(artworkId, resultFromDbCallback);
    }
}
