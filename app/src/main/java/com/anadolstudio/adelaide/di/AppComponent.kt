package com.anadolstudio.adelaide.di

import android.content.Context
import com.anadolstudio.adelaide.App
import com.anadolstudio.adelaide.di.viewmodel.ViewModelsInjector
import com.anadolstudio.adelaide.feature.gallery.di.GalleryModule
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton


@Singleton
@Component(
    modules = [
        AppModule::class,
        GalleryModule::class,
    ]
)
internal interface AppComponent {

    val viewModelsInjector: ViewModelsInjector

    @Component.Factory
    interface Factory {

        fun create(
            @BindsInstance appContext: Context
        ): AppComponent
    }

    fun inject(entry: App)
}
