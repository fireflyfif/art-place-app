package dev.iotarho.artplace.app.ui.artworks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dev.iotarho.artplace.app.repository.ArtsyRepository

class ArtworksViewModelFactory(private val artsyRepository: ArtsyRepository) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ArtworksViewModel::class.java)) {
            return ArtworksViewModel(artsyRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class.")
    }
}