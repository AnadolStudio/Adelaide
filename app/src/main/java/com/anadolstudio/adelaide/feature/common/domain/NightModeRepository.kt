package com.anadolstudio.adelaide.feature.common.domain

import io.reactivex.Observable

interface NightModeRepository {

    var nightMode: Int

    fun toggleNightMode()

    fun observeNightModeChanges(): Observable<Int>

}
