package com.anadolstudio.adelaide.feature.start

import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.anadolstudio.adelaide.R
import com.anadolstudio.adelaide.base.viewmodel.BaseActionViewModel
import com.anadolstudio.adelaide.base.viewmodel.navigateTo
import com.anadolstudio.adelaide.base.viewmodel.navigateUp
import com.anadolstudio.adelaide.feature.gallery.presetnation.GalleryViewModel.Companion.EDIT_TYPE_KEY

class StartViewModel : BaseActionViewModel(), StartController {

    override fun onPhotoClicked() = navigateToGallery(EditType.PHOTO)
    override fun onCollageClicked() = navigateToGallery(EditType.COLLAGE)

    private fun navigateToGallery(editType: EditType) {
        _navigationEvent.navigateTo(
                id = R.id.action_startFragment_to_galleryFragment,
                args = bundleOf(EDIT_TYPE_KEY to editType)
        )
    }

    override fun onBackClicked() = _navigationEvent.navigateUp()

    @Suppress("UNCHECKED_CAST")
    class Factory : ViewModelProvider.NewInstanceFactory() {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return StartViewModel() as T
        }
    }

}
