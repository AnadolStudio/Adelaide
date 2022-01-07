package com.anadolstudio.adelaide

import android.app.Application
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate.*
import com.anadolstudio.adelaide.model.SettingsPreference
import com.google.android.gms.ads.MobileAds

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        setDefaultNightMode(SettingsPreference.getNightMode(this))
    }


    fun changeTheme() {
        val mode = if (getDefaultNightMode() == MODE_NIGHT_FOLLOW_SYSTEM) {
            when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
                Configuration.UI_MODE_NIGHT_NO -> MODE_NIGHT_YES
                else -> MODE_NIGHT_NO
            }
        } else {
            if (getDefaultNightMode() == MODE_NIGHT_YES) {
                MODE_NIGHT_NO
            } else {
                MODE_NIGHT_YES
            }
        }
        SettingsPreference.setNightMode(this, mode)
        setDefaultNightMode(mode)
    }
}