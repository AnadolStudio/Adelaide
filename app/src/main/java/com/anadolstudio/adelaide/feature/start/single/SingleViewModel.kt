package com.anadolstudio.adelaide.feature.start.single

import com.anadolstudio.adelaide.base.viewmodel.BaseActionViewModel
import com.anadolstudio.adelaide.feature.common.domain.NightModeRepository
import com.anadolstudio.utils.util.rx.smartSubscribe
import javax.inject.Inject

class SingleViewModel @Inject constructor(
    nightModeRepository: NightModeRepository,
) : BaseActionViewModel() {

    init {
        nightModeRepository.observeNightModeChanges()
                .smartSubscribe(
                        onSuccess = { showEvent(SingleEvents.ChangeNightModeEvent(it)) },
                        onError = this::showError
                )
                .disposeOnCleared()
    }

}
