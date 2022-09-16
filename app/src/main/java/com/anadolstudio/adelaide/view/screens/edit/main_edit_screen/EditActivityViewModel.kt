package com.anadolstudio.adelaide.view.screens.edit.main_edit_screen

import android.Manifest
import android.content.Context
import android.graphics.Bitmap
import android.widget.ImageView
import androidx.annotation.RequiresPermission
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import com.anadolstudio.adelaide.R
import com.anadolstudio.adelaide.domain.utils.ImageLoader
import com.anadolstudio.core.activity.SingleMessageToast
import com.anadolstudio.core.common_util.ProgressListener
import com.anadolstudio.core.livedata.onNext
import com.anadolstudio.core.livedata.toImmutable
import com.anadolstudio.core.rx_util.quickSingleFrom
import com.anadolstudio.core.rx_util.smartSubscribe
import com.anadolstudio.core.viewmodel.BaseViewModel
import com.anadolstudio.photoeditorcore.processor.EditProcessorContract
import com.anadolstudio.photoeditorcore.processor.implementation.EditProcessorStudy
import java.io.File

class EditActivityViewModel : BaseViewModel() {

    private val editProcessor = EditProcessorStudy()

    private val _currentBitmap = MutableLiveData<EditActivityViewState>()
    val currentBitmap = _currentBitmap.toImmutable()

    lateinit var viewController: EditViewController
        private set // TODO ?

    fun initEditProcessor(context: Context, path: String) {
        _singleEvent.onNext(EditActivityEvent.LoadingEvent(isLoading = true))

        editProcessor.init(context, path)
                .smartSubscribe(
                        onSuccess = { bitmap -> _currentBitmap.onNext(EditActivityViewState.Content(bitmap)) },
                        onError = { _singleEvent.onNext(EditActivityEvent.CantOpenPhotoEvent) }
                )
    }

    fun getEditProcessor(): EditProcessorContract = editProcessor

    fun processPreview(support: Bitmap? = null) {
        _singleEvent.onNext(EditActivityEvent.LoadingEvent(isLoading = true))

        editProcessor.processPreview(support)
                .smartSubscribe(
                        onSuccess = { bitmap -> _currentBitmap.onNext(EditActivityViewState.Content(bitmap)) },
                        onError = { SingleMessageToast() }
                )
    }

    @RequiresPermission(allOf = [Manifest.permission.WRITE_EXTERNAL_STORAGE])
    fun saveAsFile(
            context: Context,
            file: File,
            progressListener: ProgressListener<String>? = null
    ) {
        _singleEvent.onNext(EditActivityEvent.LoadingEvent(isLoading = true))

        quickSingleFrom { editProcessor.saveAsFile(context, file, progressListener) }
                .smartSubscribe(
                        onSuccess = { path ->
                            _singleEvent.onNext(EditActivityEvent.SuccessSaveEvent(path))
                        },
                        onError = {
                            _singleEvent.onNext(SingleMessageToast(context.getString(R.string.edit_error_failed_save_image)))
                        },
                        onFinally = {
                            _singleEvent.onNext(EditActivityEvent.LoadingEvent(isLoading = false))
                        }
                )
    }

    fun setEditViewController(viewController: EditViewController) {
        this.viewController = viewController
    }

    fun setCurrentImage(
            activity: AppCompatActivity, path: String, scaleType: ImageView.ScaleType?
    ) {
        val size = viewController.workspaceSize(activity)

        _singleEvent.onNext(EditActivityEvent.LoadingEvent(isLoading = true))

        ImageLoader.loadImageWithoutCache(activity, path, size.x, size.y) { bitmap: Bitmap ->
            _singleEvent.onNext(EditActivityEvent.LoadingEvent(isLoading = false))

            _currentBitmap.onNext(EditActivityViewState.Content(bitmap))
            editProcessor.setCurrentImage(bitmap)
            if (scaleType != null) viewController.mainImage.scaleType = scaleType
        }
    }

    override fun onCleared() {
        super.onCleared()
        editProcessor.clear()
    }

    fun rebootCurrentImage() {
        editProcessor.reboot()
        _currentBitmap.onNext(EditActivityViewState.Content(editProcessor.getCurrentImage()))
    }

    fun onChangedCurrentBitmap(bitmap: Bitmap) {
        _currentBitmap.onNext(EditActivityViewState.Content(bitmap))
    }
}
