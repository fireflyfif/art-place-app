package dev.iotarho.artplace.app.ui.artworks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dev.iotarho.artplace.app.repository.ArtsyRepository

class TrendyArtistViewModelFactory(private val artsyRepository: ArtsyRepository) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TrendyArtistsViewModel::class.java)) {
            return TrendyArtistsViewModel(artsyRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class.")
    }
}