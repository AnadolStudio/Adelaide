package com.anadolstudio.core.tasks

import android.os.Handler
import android.os.Looper
import androidx.annotation.MainThread

interface ProgressListener {

    @MainThread
    fun onProgress(progress: Int)

    abstract class Abstract : ProgressListener {

        override fun onProgress(progress: Int) {
            if (validate(progress)) {
                Handler(Looper.getMainLooper()).post {
                    doMain(progress)
                }
            }
        }

        protected fun validate(progress: Int): Boolean = progress in 0..100

        abstract fun doMain(progress: Int)
    }
}