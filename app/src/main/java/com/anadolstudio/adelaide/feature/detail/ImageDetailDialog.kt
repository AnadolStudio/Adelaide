package com.anadolstudio.adelaide.feature.detail

import android.graphics.Bitmap
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import com.anadolstudio.adelaide.R
import com.anadolstudio.adelaide.databinding.DialogImageDetailBinding
import com.anadolstudio.ui.dialogs.CoreDialogFragment
import com.anadolstudio.ui.fragment.withArgs
import com.anadolstudio.ui.viewbinding.viewBinding

class ImageDetailDialog : CoreDialogFragment(R.layout.dialog_image_detail) {

    companion object {

        private const val PHOTO_FILE = "photo_file"

        fun newInstance(bitmap: Bitmap): ImageDetailDialog = ImageDetailDialog().withArgs {
            putParcelable(PHOTO_FILE, bitmap)
        }

    }

    private val binding by viewBinding { DialogImageDetailBinding.bind(it) }

    override fun getDialogTag(): String = ImageDetailDialog::class.simpleName.orEmpty()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireDialog().setContentView(binding.root)
        requireDialog().setCancelable(true)


        /*val size = Point()
        requireActivity().windowManager.defaultDisplay.getSize(size)
        val min = min(size.x, size.y)

        val bitmap = BitmapDecoder.Manager.decodeBitmapFromPath(
                requireContext(),
                path, min, min
        )*/

//        binding.imagePhoto.layoutParams.height = bitmap.height
        binding.imagePhoto.setImageBitmap(requireArguments().getParcelable(PHOTO_FILE))
        binding.imagePhoto.requestLayout()
        binding.imagePhoto.setOnClickListener { }
        binding.root.setOnTouchListener { v, event ->

            if (event.action == MotionEvent.ACTION_DOWN) {
                dismiss()
            }
            return@setOnTouchListener false
        }
    }

}
