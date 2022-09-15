package com.anadolstudio.adelaide.view.screens

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.widget.Toast
import androidx.annotation.StringRes
import com.anadolstudio.adelaide.R
import com.anadolstudio.core.activity.BaseActivity
import com.anadolstudio.core.activity.MessageType
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar

open class BaseEditActivity : BaseActivity() {

    override fun showError(error: Throwable) {
        TODO("Not yet implemented")
    }

    override fun showMessage(message: MessageType) {
        val text = message.text
        when (message) {
            is MessageType.Snack -> showToast(text)
            is MessageType.Toast -> showSnack(text)
        }
    }

    protected fun showToast(@StringRes stringRes: Int) = showToast(getString(stringRes))

    protected fun showToast(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
    }

    protected fun showSnack(text: String) {
        Snackbar.make(findViewById(android.R.id.content), text, Snackbar.LENGTH_SHORT).show()
    }

    override fun showErrorDialog() {
        TODO("Not yet implemented")
    }

    override fun showInformationDialog() {
        TODO("Not yet implemented")
    }

    fun showSettingsSnackbar() = Snackbar
            .make(
                    findViewById(android.R.id.content),
                    getText(R.string.gallery_error_miss_permission),
                    BaseTransientBottomBar.LENGTH_INDEFINITE
            )
            .setAction(R.string.gallery_snack_bar_settings) { startAppSettingsActivity() }
            .show()

    private fun startAppSettingsActivity() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        intent.data = Uri.fromParts("package", packageName, null)

        startActivity(intent)
    }

}
