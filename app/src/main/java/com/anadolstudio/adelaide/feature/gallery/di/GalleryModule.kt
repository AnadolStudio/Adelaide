package com.anadolstudio.adelaide.feature.gallery.di

import android.content.Context
import com.anadolstudio.core.data_source.media.MediaDataStorage
import com.anadolstudio.data.repository.gallery.GalleryRepositoryImpl
import com.anadolstudio.domain.repository.GalleryRepository
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
