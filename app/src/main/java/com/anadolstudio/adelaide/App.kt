package com.anadolstudio.adelaide

import android.app.Application
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate.*
import com.anadolstudio.adelaide.data.SettingsPreference
import com.anadolstudio.adelaide.di.DI

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        setDefaultNightMode(SettingsPreference.getNightMode(this))

        DI.init(applicationContext)
    }

    fun changeTheme() {
        val mode = when (getDefaultNightMode()) { // TODO это можно вынести за пределы App
            MODE_NIGHT_FOLLOW_SYSTEM -> getNightModeFromSystem()
            MODE_NIGHT_NO -> MODE_NIGHT_YES
            else /*MODE_NIGHT_YES*/ -> MODE_NIGHT_NO
        }

        SettingsPreference.setNightMode(this, mode)
        setDefaultNightMode(mode)
    }

    private fun getNightModeFromSystem(): Int = when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
        Configuration.UI_MODE_NIGHT_NO -> MODE_NIGHT_YES
        else -> MODE_NIGHT_NO
    }
}
