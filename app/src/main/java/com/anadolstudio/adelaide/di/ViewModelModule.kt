package com.anadolstudio.adelaide.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.anadolstudio.adelaide.di.viewmodel.ViewModelFactory
import com.anadolstudio.adelaide.di.viewmodel.ViewModelKey
import com.anadolstudio.adelaide.feature.gallery.presetnation.GalleryViewModel
import com.anadolstudio.adelaide.feature.start.single.SingleViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
interface ViewModelModule {

    @Binds
    fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(SingleViewModel::class)
    fun bindSingleViewModel(viewModel: SingleViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(GalleryViewModel::class)
    fun bindMainViewModel(viewModel: GalleryViewModel): ViewModel

}
