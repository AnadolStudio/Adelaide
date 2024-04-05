package com.anadolstudio.adelaide.feature.home.presentation

import android.Manifest
import android.content.Context
import com.anadolstudio.adelaide.base.viewmodel.BaseActionViewModel
import com.anadolstudio.adelaide.feature.common.domain.NightModeRepository
import com.anadolstudio.utils.util.extentions.hasAllPermissions
import javax.inject.Inject

class HomeViewModel @Inject constructor(
    private val nightModeRepository: NightModeRepository,
    private val context: Context,
) : BaseActionViewModel(), HomeController {

    companion object {

        val STORAGE_PERMISSION = arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
        )
    }

    init {
//        checkPermissionAndLoad()
    }

    private fun checkPermissionAndLoad() {
        if (!context.hasAllPermissions(STORAGE_PERMISSION)) {
//            showEvent(GalleryEvent.RequestPermissionEvent)
        }
    }



    override fun onPermissionGranted() = showTodo()

    override fun onBackClicked() {
        nightModeRepository.toggleNightMode()
    }


}
