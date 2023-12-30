package com.anadolstudio.adelaide.feature.detail

import android.view.MotionEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class ImageDialogTouchListener(val path: String, private val activity: AppCompatActivity) :
    View.OnTouchListener {
    private val PHOTO_DIALOG = "photo_dialog"
    var dialog: ImageDetailDialog? = null

    override fun onTouch(view: View?, motionEvent: MotionEvent?): Boolean {

        when (motionEvent?.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                dialog = ImageDetailDialog.newInstance(path)
                dialog?.show(activity.supportFragmentManager, PHOTO_DIALOG)
            }
            MotionEvent.ACTION_UP -> {
                dialog?.dismiss()
            }
        }

        return false
    }
}
