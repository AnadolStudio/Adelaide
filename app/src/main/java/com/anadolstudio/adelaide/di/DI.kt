package com.anadolstudio.adelaide.di

import android.app.Application
import com.anadolstudio.adelaide.di.viewmodel.ViewModelsInjector

internal object DI {

    private var application: Application? = null

    private val appComponent: AppComponent by lazy {
        val application = checkNotNull(application) { "App is null" }
        DaggerAppComponent.factory().create(application)
    }

    fun init(application: Application) {
        this.application = application
    }

    val viewModelsInjector: ViewModelsInjector get() = appComponent.viewModelsInjector
}
