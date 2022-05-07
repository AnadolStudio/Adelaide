package com.anadolstudio.adelaide.view.screens

import android.view.View
import com.anadolstudio.adelaide.R
import com.anadolstudio.core.view.BaseActivity

open class BaseEditActivity : BaseActivity() {

    fun showSettingsSnackbar(rootView: View?) {
        super.showSettingsSnackbar(
            rootView,
            R.string.gallery_error_miss_permission,
            R.string.gallery_snack_bar_settings
        )
    }

}