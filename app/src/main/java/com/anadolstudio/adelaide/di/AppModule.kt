package com.anadolstudio.adelaide.di

import com.anadolstudio.adelaide.feature.gallery.di.GalleryModule
import dagger.Module

@Module(
        includes = [
            UseCaseModule::class,
            GalleryModule::class
        ]
)
class AppModule
