package com.anadolstudio.adelaide.feature.start

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.anadolstudio.adelaide.R
import com.anadolstudio.adelaide.base.viewmodel.BaseActionViewModel
import com.anadolstudio.adelaide.base.viewmodel.navigateTo
import com.anadolstudio.adelaide.base.viewmodel.navigateUp
import com.anadolstudio.adelaide.navigation.NavigateData
import com.anadolstudio.core.livedata.onNext
import com.anadolstudio.core.navigation.Back

class StartViewModel : BaseActionViewModel(), StartController {

    override fun onPhotoClicked() = _navigationEvent.navigateTo(R.id.action_startFragment_to_galleryFragment)

    override fun onBackClicked() = _navigationEvent.navigateUp()

    @Suppress("UNCHECKED_CAST")
    class Factory : ViewModelProvider.NewInstanceFactory() {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return StartViewModel() as T
        }
    }

}
