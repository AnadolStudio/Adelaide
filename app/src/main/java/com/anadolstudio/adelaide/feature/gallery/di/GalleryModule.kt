package com.anadolstudio.adelaide.feature.gallery.di

import android.content.Context
import com.anadolstudio.core.data_source.MediaDataStorage
import com.anadolstudio.data.repository.GalleryRepository
import com.anadolstudio.domain.repository.gallery.GalleryRepositoryImpl
import dagger.Module
import dagger.Provides

@Module
class GalleryModule {

    @Provides
    fun provideMediaDataStorage(context: Context): MediaDataStorage = MediaDataStorage(context)

    @Provides
    fun provideGalleryRepository(mediaDataStorage: MediaDataStorage): GalleryRepository =
            GalleryRepositoryImpl(mediaDataStorage)
}
