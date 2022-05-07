package com.anadolstudio.core.view

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import com.anadolstudio.core.R
import com.anadolstudio.core.dialogs.LoadingView
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar

open class BaseActivity : AppCompatActivity() {
    private var mLoadingView: LoadingView? = null

    protected fun showLoadingDialog() {
        mLoadingView = LoadingView.Base.view(supportFragmentManager)
        mLoadingView?.showLoadingIndicator()
    }

    protected fun hideLoadingDialog() {
        mLoadingView?.hideLoadingIndicator()
    }

    override fun onStop() {
        super.onStop()
        hideLoadingDialog()
    }

    protected fun showToast(@StringRes id: Int, length: Int = Toast.LENGTH_SHORT) {
        Toast.makeText(this, getString(id), length).show()
    }

    protected fun showToast(text: String, length: Int = Toast.LENGTH_SHORT) {
        Toast.makeText(this, text, length).show()
    }

    open fun showSettingsSnackbar(
        rootView: View?,
        @StringRes textStringId: Int,
        @StringRes actionStringId: Int,
    ) {
        val snackbar = Snackbar.make(
            rootView!!,
            getText(textStringId),
            BaseTransientBottomBar.LENGTH_INDEFINITE
        )

        snackbar.setAction(actionStringId) { startAppSettingsActivity() }
        snackbar.show()
    }

    open fun startAppSettingsActivity() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", packageName, null)
        intent.data = uri
        startActivity(intent)
    }
}