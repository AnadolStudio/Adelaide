package com.anadolstudio.adelaide.feature.detail

import android.graphics.Point
import android.os.Bundle
import android.view.View
import com.anadolstudio.adelaide.R
import com.anadolstudio.adelaide.databinding.DialogImageDetailBinding
import com.anadolstudio.ui.dialogs.CoreDialogFragment
import com.anadolstudio.ui.viewbinding.viewBinding
import com.anadolstudio.utils.bitmap_util.BitmapDecoder
import kotlin.math.min

class ImageDetailDialog : CoreDialogFragment(R.layout.dialog_image_detail) {

    companion object {

        private const val PHOTO_FILE = "photo_file"

        fun newInstance(path: String?): ImageDetailDialog = ImageDetailDialog().apply {
            val args = Bundle()
            args.putString(PHOTO_FILE, path)
            arguments = args
        }

    }

    private val binding by viewBinding { DialogImageDetailBinding.bind(it) }

    override fun getDialogTag(): String = ImageDetailDialog::class.simpleName.orEmpty()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireDialog().setContentView(binding.root)

        val path = requireArguments().getString(PHOTO_FILE)

        if (path == null) {
            dismiss()
            return
        }

        val size = Point()
        requireActivity().windowManager.defaultDisplay.getSize(size)
        val min = min(size.x, size.y)

        val bitmap = BitmapDecoder.Manager.decodeBitmapFromPath(
                requireContext(),
                path, min, min
        )
//        binding.imagePhoto.layoutParams.height = bitmap.height
        binding.imagePhoto.setImageBitmap(bitmap)
        binding.imagePhoto.requestLayout()

    }

}
