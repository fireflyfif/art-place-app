package dev.iotarho.artplace.app.ui.searchdetail;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import dev.iotarho.artplace.app.repository.ArtsyRepository;

public class ShowDetailViewModelFactory implements ViewModelProvider.Factory {

    private ArtsyRepository artsyRepository;

    public ShowDetailViewModelFactory(ArtsyRepository artsyRepository) {
        this.artsyRepository = artsyRepository;
    }

    @SuppressWarnings("unchecked")
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(ShowsDetailViewModel.class)) {
            return (T) new ShowsDetailViewModel(artsyRepository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class.");
    }
}
