package dev.iotarho.artplace.app.ui.favorites;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import dev.iotarho.artplace.app.repository.FavArtRepository;

public class FavArtworksViewModelFactory implements ViewModelProvider.Factory {

    private FavArtRepository favRepository;

    public FavArtworksViewModelFactory(FavArtRepository favRepository) {
        this.favRepository = favRepository;
    }

    @SuppressWarnings("unchecked")
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(FavArtworksViewModel.class)) {
            return (T) new FavArtworksViewModel(favRepository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class.");
    }
}
