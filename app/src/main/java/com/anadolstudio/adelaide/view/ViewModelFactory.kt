package com.anadolstudio.adelaide.view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.anadolstudio.adelaide.data.GalleryService
import com.anadolstudio.adelaide.view.screens.gallery.GalleryListViewModel

class ViewModelFactory : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val viewModel = when (modelClass) {
            GalleryListViewModel::class.java -> {
                GalleryListViewModel(GalleryService())
            }
            else -> throw IllegalArgumentException()
        }
        return viewModel as T
    }
}