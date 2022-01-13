package com.anadolstudio.adelaide.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.anadolstudio.adelaide.activities.EditActivity
import com.anadolstudio.adelaide.interfaces.LoadingView
import com.anadolstudio.adelaide.interfaces.StateListener
import com.google.android.material.slider.LabelFormatter
import com.google.android.material.slider.Slider
import kotlin.math.roundToInt

abstract class BaseEditFragment : Fragment(), StateListener {
/*
    val selectableController = object : SelectableController<LinearLayout>() {
        override fun updateView(linearLayout: LinearLayout, isSelected: Boolean) {
            if (!isSelected) {
                linearLayout.setBackgroundColor(
                    ContextCompat.getColor(
                        context!!,
                        R.color.colorWhite
                    )
                )
            } else {
                linearLayout.background =
                    ContextCompat.getDrawable(context!!, R.drawable.rectangle_background)
            }
        }

        override fun saveState(linearLayout: LinearLayout) = linearLayout.id
    }
*/

    private var mLoadingView: LoadingView? = null
    private var activity: EditActivity? = null

    override fun isLocalApply() = false

    override fun apply() :Boolean = false

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
        ensureActivity()?.let { mLoadingView = LoadingDialog.view(it.supportFragmentManager) }
        mLoadingView?.showLoadingIndicator()
    }

    fun hideLoadingDialog() {
        mLoadingView?.hideLoadingIndicator()
    }

    override fun onBackClick() = false

    protected class RealFormatter : LabelFormatter {
        override fun getFormattedValue(value: Float) = value.roundToInt().toString()
    }

    companion object {
        const val REQUEST_CHOOSE_PHOTO = 1001
        const val CHOOSE_PHOTO = "choose_photo"
        protected const val COLOR = "color"
        protected const val GRAVITY = "gravity"
        protected const val CURRENT_SIZE = "current_size"
        protected const val CURRENT_ALPHA = "current_alpha"
        protected const val STANDART_DURATION: Long = 300

        protected fun setValue(slider: Slider, from: Float, to: Float, value: Float) {
            slider.valueFrom = from
            slider.valueTo = to
            slider.value = value
        }

    }
}