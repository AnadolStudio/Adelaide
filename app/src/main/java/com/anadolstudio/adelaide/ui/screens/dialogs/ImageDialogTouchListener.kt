package com.anadolstudio.adelaide.ui.screens.dialogs

import android.view.MotionEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class ImageDialogTouchListener(val path: String, private val activity: AppCompatActivity) :
    View.OnTouchListener {
    private val PHOTO_DIALOG = "photo_dialog"
    var dialog: ImageDialog? = null

    override fun onTouch(view: View?, motionEvent: MotionEvent?): Boolean {

        when (motionEvent?.actionMasked) {

            MotionEvent.ACTION_DOWN -> {
                dialog = ImageDialog.newInstance(path)
                dialog?.show(activity.supportFragmentManager, PHOTO_DIALOG)
            }

            MotionEvent.ACTION_UP -> {
                dialog?.dismiss()
            }

            MotionEvent.ACTION_POINTER_DOWN -> {}
        }

        return false
    }
}
