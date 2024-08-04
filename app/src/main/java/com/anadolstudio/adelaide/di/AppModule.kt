package com.anadolstudio.adelaide.di

import com.anadolstudio.adelaide.feature.gallery.di.GalleryModule
import dagger.Module

@Module(
    includes = [
        GalleryModule::class,
        ViewModelModule::class,
        RepositoryModule::class,
    ]
)
class AppModule
