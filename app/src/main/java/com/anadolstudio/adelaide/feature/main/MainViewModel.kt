package com.anadolstudio.adelaide.feature.main

import android.content.res.Resources
import androidx.annotation.StringRes
import com.anadolstudio.adelaide.R
import com.anadolstudio.adelaide.base.viewmodel.BaseContentViewModel
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val resources: Resources
) : BaseContentViewModel<MainScreenState>(
    MainScreenState(
        tabList = MainScreenState.Tabs.values().map { resources.getString(it.nameRes) }
    )
), MainController {


    override fun onBackClicked() = showTodo()

    override fun onTabClicked(tabIndex: Int) {
        val newTab = MainScreenState.Tabs.values().getOrNull(tabIndex) ?: return
        updateState { copy(currentTab = newTab) }
    }
}

data class MainScreenState(
    val tabList: List<String>,
    val currentTab: Tabs = Tabs.MEDIA
) {
    enum class Tabs(@StringRes val nameRes: Int) {
        MEDIA(R.string.common_media), ALBUMS(R.string.common_albums), NEWS(R.string.common_news)
    }
}
