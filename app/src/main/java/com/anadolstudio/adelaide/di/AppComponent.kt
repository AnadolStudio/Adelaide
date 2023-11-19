package com.anadolstudio.adelaide.di

import android.content.Context
import com.anadolstudio.adelaide.feature.gallery.presetnation.GalleryViewModel
import com.anadolstudio.adelaide.feature.start.StartViewModel
import dagger.BindsInstance
import dagger.Component

@Component(modules = [AppModule::class])
interface AppComponent {

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun appContext(appContext: Context): Builder

        fun build(): AppComponent
    }

//    fun inject(entry: DaysFragment)
    fun inject(entry: StartViewModel.Factory)
    fun inject(entry: GalleryViewModel.Factory)
}
