package com.anadolstudio.adelaide.di

import android.content.Context

object DI {

    lateinit var appComponent: AppComponent

    fun init(context: Context) {
        appComponent = DaggerAppComponent.builder()
                .appContext(context)
                .build()
    }

    fun getComponent(): AppComponent = appComponent

}
