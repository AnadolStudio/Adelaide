package com.anadolstudio.adelaide

import android.app.Application
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
import androidx.appcompat.app.AppCompatDelegate.getDefaultNightMode
import androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode
import com.anadolstudio.adelaide.data.SettingsPreference

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        setDefaultNightMode(SettingsPreference.getNightMode(this))
    }

    fun changeTheme() {
        // TODO это можно вынести за пределы App
        val mode = if (getDefaultNightMode() == MODE_NIGHT_FOLLOW_SYSTEM) {
            when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
                Configuration.UI_MODE_NIGHT_NO -> MODE_NIGHT_YES
                else -> MODE_NIGHT_NO
            }
        } else {
            if (getDefaultNightMode() == MODE_NIGHT_YES) MODE_NIGHT_NO else MODE_NIGHT_YES
        }

        SettingsPreference.setNightMode(this, mode)
        setDefaultNightMode(mode)
    }
}
