package com.anadolstudio.adelaide.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.anadolstudio.adelaide.ui.screens.gallery.GalleryListViewModel
import com.anadolstudio.domain.repository.gallery.GalleryRepositoryImpl

class ViewModelFactory : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T = when (modelClass) {
        GalleryListViewModel::class.java -> GalleryListViewModel(GalleryRepositoryImpl())
        else -> throw IllegalArgumentException()
    } as T

}
