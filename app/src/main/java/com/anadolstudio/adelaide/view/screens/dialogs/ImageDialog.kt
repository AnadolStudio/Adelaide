package com.anadolstudio.adelaide.view.screens.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Point
import android.os.Bundle
import androidx.appcompat.app.AppCompatDialogFragment
import com.anadolstudio.adelaide.R
import com.anadolstudio.adelaide.databinding.DialogImageBinding
import com.anadolstudio.core.util.BitmapDecoder
import kotlin.math.min

class ImageDialog : AppCompatDialogFragment() {

    companion object {

        private const val PHOTO_FILE = "photo_file"

        fun newInstance(path: String?): ImageDialog = ImageDialog().apply {
            val args = Bundle()
            args.putString(PHOTO_FILE, path)
            arguments = args
        }

    }

    private lateinit var binding: DialogImageBinding
    private var path: String? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = DialogImageBinding.inflate(layoutInflater)

        path = arguments?.getString(PHOTO_FILE) ?: throw IllegalArgumentException()

        val size = Point()
        requireActivity().windowManager.defaultDisplay.getSize(size)
        val min = min(size.x, size.y)

        val bitmap = BitmapDecoder.Manager.decodeBitmapFromPath(
            requireContext(),
            path!!, min, min
        )

        binding.imagePhoto.layoutParams.height = bitmap.height
        binding.imagePhoto.requestLayout()
        binding.imagePhoto.setImageBitmap(bitmap)

        val dialog: AlertDialog = AlertDialog.Builder(activity)
            .setView(binding.root)
            .create()

        if (dialog.window != null)
            dialog.window!!.attributes.windowAnimations = R.style.SlidingDialogAnimation

        return dialog
    }

}