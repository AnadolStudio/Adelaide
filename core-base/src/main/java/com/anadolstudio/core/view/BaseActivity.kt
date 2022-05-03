package com.anadolstudio.core.view

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
}