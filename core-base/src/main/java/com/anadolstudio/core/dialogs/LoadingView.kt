package com.anadolstudio.core.dialogs

import android.app.Dialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.anadolstudio.core.R
import com.anadolstudio.core.databinding.DialogLoadingBinding
import com.anadolstudio.core.tasks.ProgressListener
import java.lang.ref.Reference
import java.lang.ref.WeakReference
import java.util.concurrent.atomic.AtomicBoolean

interface LoadingView : ProgressListener {

    fun showLoadingIndicator()

    fun hideLoadingIndicator()

    class Base : DialogFragment() {

        companion object {
            val HANDLER = Handler(Looper.getMainLooper())

            fun view(fm: FragmentManager): LoadingView = object : LoadingView {

                var dialog: Base? = null

                private val mWaitForHide = AtomicBoolean()

                override fun showLoadingIndicator() {
                    if (mWaitForHide.compareAndSet(false, true)) {

                        if (fm.findFragmentByTag(Base::class.java.name) == null) {
                            dialog = Base()
                            dialog?.show(fm, Base::class.java.name)
                        }
                    }
                }

                override fun hideLoadingIndicator() {
                    HANDLER.post(HideTask(fm))
                }

                override fun onProgress(progress: Int) {
                    dialog?.dialogProgressListener?.onProgress(progress)
                }
            }
        }

        inner class DialogProgressListener : ProgressListener.Abstract() {

            override fun doMain(progress: Int) {
                // TODO не обновляет
                binding.textView.text = if (progress == 100) {
                    activity?.getString(R.string.loading)
                } else {
                    progress.toString()
                }
            }

        }

        val dialogProgressListener = DialogProgressListener()
        lateinit var binding: DialogLoadingBinding

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setStyle(STYLE_NO_TITLE, theme)
            isCancelable = false
        }

        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            binding = DialogLoadingBinding.inflate(requireActivity().layoutInflater)

            return AlertDialog.Builder(requireActivity())
                .setView(binding.root)
                .create()
        }

        private class HideTask(fm: FragmentManager) : Runnable {
            private val mFmRef: Reference<FragmentManager>

            // TODO убери бесконечность, используй нижнюю переменную
            private var attempts = 5
            override fun run() {
                HANDLER.removeCallbacks(this)

                (mFmRef.get()?.findFragmentByTag(Base::class.java.name) as Base?)
                    ?.dismissAllowingStateLoss()
                    ?: let {
                        attempts--
                        if (attempts > 0) HANDLER.postDelayed(this, 300)
                    }
            }

            init {
                mFmRef = WeakReference(fm)
            }
        }
    }
}