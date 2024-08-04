package com.anadolstudio.adelaide.feature.common.data

import androidx.appcompat.app.AppCompatDelegate
import com.anadolstudio.utils.util.preferences.modify
import com.ironz.binaryprefs.Preferences

@Suppress("TooManyFunctions")
class PreferencesStorage(private val preferences: Preferences) {

    private companion object {
        const val NIGHT_MODE = "NIGHT_MODE"
    }

    var nightMode: Int
        set(value) = preferences.modify { putInt(NIGHT_MODE, value) }
        get() = preferences.getInt(NIGHT_MODE, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
}
