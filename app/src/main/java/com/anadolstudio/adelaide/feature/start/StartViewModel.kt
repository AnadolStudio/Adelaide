package com.anadolstudio.adelaide.feature.start

import android.content.Context
import android.net.Uri
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.anadolstudio.adelaide.BuildConfig
import com.anadolstudio.adelaide.R
import com.anadolstudio.adelaide.base.viewmodel.BaseActionViewModel
import com.anadolstudio.adelaide.base.viewmodel.navigateTo
import com.anadolstudio.adelaide.base.viewmodel.navigateUp
import com.anadolstudio.adelaide.di.DI
import com.anadolstudio.adelaide.feature.gallery.presetnation.GalleryViewModel.Companion.EDIT_TYPE_KEY
import com.anadolstudio.core.util.files.CameraUtil
import javax.inject.Inject

class StartViewModel(private val context: Context) : BaseActionViewModel(), StartController {

    private companion object {
        const val PROVIDER = ".provider"
    }

    override fun onGalleryClicked() = navigateToGallery(EditType.PHOTO)
    override fun onDraftClicked() = showTodo()
    override fun onInfoClicked() = showTodo()

    private fun navigateToGallery(editType: EditType) {
        _navigationEvent.navigateTo(
                id = R.id.action_startFragment_to_galleryFragment,
                args = bundleOf(EDIT_TYPE_KEY to editType)
        )
    }

    override fun onBackClicked() = _navigationEvent.navigateUp()

    override fun onCameraPermissionsGranted() {
        val uri = CameraUtil.createImageFileUri(
                context = context,
                file = context.filesDir,
                authority = BuildConfig.APPLICATION_ID + PROVIDER
        )

        showEvent(StartFragmentEvents.TakePhotoEvent(uri))
    }

    override fun onTakePhotoResult(uri: Uri?) = showTodo()

    @Suppress("UNCHECKED_CAST")
    class Factory : ViewModelProvider.NewInstanceFactory() {

        @Inject
        lateinit var context: Context

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            DI.appComponent.inject(this)

            return StartViewModel(context) as T
        }
    }

}
