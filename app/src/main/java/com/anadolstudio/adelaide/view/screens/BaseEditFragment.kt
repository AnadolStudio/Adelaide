package com.anadolstudio.adelaide.view.screens

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.anadolstudio.adelaide.view.screens.edit.EditActivity
import com.anadolstudio.core.dialogs.LoadingView
import com.anadolstudio.core.interfaces.StateListener
import com.google.android.material.slider.LabelFormatter
import kotlin.math.roundToInt

abstract class BaseEditFragment : Fragment(), StateListener {

    private var mLoadingView: LoadingView? = null
    private var activity: EditActivity? = null

    override fun isLocalApply() = false

    override fun apply(): Boolean = false

    override fun isLocalBackClick() = false

    fun parent(): EditActivity? = ensureActivity()

    private fun ensureActivity(): EditActivity? {
        activity ?: let { activity = (getActivity() as EditActivity?) }
        return activity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ensureActivity()
    }

    override fun onResume() {
        super.onResume()
        ensureActivity()
    }

    override fun onDetach() {
        activity = null
        super.onDetach()
    }

    fun showLoadingDialog() {
        ensureActivity()?.let { mLoadingView = LoadingView.Base.view(it.supportFragmentManager) }
        mLoadingView?.showLoadingIndicator()
    }

    fun hideLoadingDialog() {
        mLoadingView?.hideLoadingIndicator()
    }

    override fun onBackClick() = false

    protected class RealFormatter : LabelFormatter {
        override fun getFormattedValue(value: Float) = value.roundToInt().toString()
    }
}