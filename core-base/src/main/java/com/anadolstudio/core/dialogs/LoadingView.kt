package com.anadolstudio.core.dialogs

import android.app.Dialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.anadolstudio.core.R
import java.lang.ref.Reference
import java.lang.ref.WeakReference
import java.util.concurrent.atomic.AtomicBoolean

interface LoadingView {

    fun showLoadingIndicator()

    fun hideLoadingIndicator()


    class Base : DialogFragment() {

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setStyle(STYLE_NO_TITLE, theme)
            isCancelable = false
        }

        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog = AlertDialog.Builder(requireActivity())
            .setView(View.inflate(activity, R.layout.dialog_loading, null))
            .create()

        private class HideTask(fm: FragmentManager) : Runnable {
            private val mFmRef: Reference<FragmentManager>

            // TODO убери бесконечность, используй нижнюю переменную
            private val mAttempts = 5
            override fun run() {
                HANDLER.removeCallbacks(this)
                val fm = mFmRef.get()

                if (fm != null) {
                    (fm.findFragmentByTag(Base::class.java.name) as Base?)
                        ?.dismissAllowingStateLoss()
                        ?: HANDLER.postDelayed(this, 300)
                }
            }

            init {
                mFmRef = WeakReference(fm)
            }
        }

        companion object {
            val HANDLER = Handler(Looper.getMainLooper())

            fun view(fm: FragmentManager): LoadingView = object : LoadingView {

                private val mWaitForHide = AtomicBoolean()

                override fun showLoadingIndicator() {
                    if (mWaitForHide.compareAndSet(false, true)) {
                        if (fm.findFragmentByTag(Base::class.java.name) == null) {
                            val dialog = Base()
                            dialog.show(fm, Base::class.java.name)
                        }
                    }
                }

                override fun hideLoadingIndicator() {
                    HANDLER.post(HideTask(fm))
                }
            }
        }
    }
}