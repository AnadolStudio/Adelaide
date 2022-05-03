package com.anadolstudio.core.view

import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import com.anadolstudio.core.dialogs.LoadingView

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
        Toast.makeText(this, getString(id), Toast.LENGTH_SHORT).show()
    }

    protected fun showToast(text: String, length: Int = Toast.LENGTH_SHORT) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
    }
}