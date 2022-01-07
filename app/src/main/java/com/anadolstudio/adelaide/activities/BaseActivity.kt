package com.anadolstudio.adelaide.activities

import androidx.appcompat.app.AppCompatActivity
import com.anadolstudio.adelaide.fragments.LoadingDialog
import com.anadolstudio.adelaide.interfaces.LoadingView

open class BaseActivity : AppCompatActivity() {
    private var mLoadingView: LoadingView? = null

    protected fun showLoadingDialog() {
        mLoadingView = LoadingDialog.view(supportFragmentManager)
        mLoadingView?.showLoadingIndicator()
    }

    protected fun hideLoadingDialog() {
        mLoadingView?.hideLoadingIndicator()
    }
}