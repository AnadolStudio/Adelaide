package com.anadolstudio.adelaide.feature.gallery.di

import androidx.lifecycle.ViewModel
import com.anadolstudio.adelaide.di.viewmodel.ViewModelKey
import com.anadolstudio.adelaide.feature.gallery.presetnation.GalleryViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
internal interface GalleryViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(GalleryViewModel::class)
    fun bindGalleryViewModel(impl: GalleryViewModel): ViewModel

}
