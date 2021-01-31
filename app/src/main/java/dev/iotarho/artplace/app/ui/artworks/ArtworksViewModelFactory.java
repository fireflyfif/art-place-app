package dev.iotarho.artplace.app.ui.artworks;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import dev.iotarho.artplace.app.repository.ArtsyRepository;

public class ArtworksViewModelFactory implements ViewModelProvider.Factory {

    private ArtsyRepository artsyRepository;

    public ArtworksViewModelFactory(ArtsyRepository artsyRepository) {
        this.artsyRepository = artsyRepository;
    }

    @SuppressWarnings("unchecked")
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(ArtworksViewModel.class)) {
            return (T) new ArtworksViewModel(artsyRepository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class.");
    }
}
