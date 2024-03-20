package com.anadolstudio.adelaide.di

import com.anadolstudio.adelaide.di.viewmodel.ViewModelFactory
import dagger.Module

interface SharedComponent {
    fun viewModelsFactory(): ViewModelFactory
}

@Module
class SharedModule {}

interface SharedComponentProvider {
    fun getModule(): SharedComponent
}
