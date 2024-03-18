package com.anadolstudio.adelaide.di

import androidx.lifecycle.ViewModel
import com.anadolstudio.adelaide.di.viewmodel.ViewModelKey
import com.anadolstudio.adelaide.feature.main.ActivityMainViewModel
import com.anadolstudio.adelaide.feature.main.MainViewModel
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import javax.inject.Singleton

@Module
interface AppModule {

    @Binds
    @IntoMap
    @ViewModelKey(ActivityMainViewModel::class)
    fun bindActivityMainViewModel(impl: ActivityMainViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(MainViewModel::class)
    fun bindMainViewModel(impl: MainViewModel): ViewModel

    companion object {

        @Provides
        @Singleton
        fun dispatcherProvider(): DispatcherProvider = DispatcherProvider()
    }

}
