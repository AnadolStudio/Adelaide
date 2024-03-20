package com.anadolstudio.adelaide

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.anadolstudio.adelaide.di.DI
import com.anadolstudio.adelaide.di.SharedComponent
import com.anadolstudio.adelaide.di.SharedComponentProvider
import com.anadolstudio.adelaide.feature.common.data.PreferencesStorage
import javax.inject.Inject

class App : Application(), SharedComponentProvider {

    @Inject
    lateinit var preferences: PreferencesStorage

    override fun onCreate() {
        super.onCreate()

        DI.init(applicationContext)
        DI.getComponent().inject(this)
        AppCompatDelegate.setDefaultNightMode(preferences.nightMode)
    }

    override fun getModule(): SharedComponent = DI.getComponent()

}
