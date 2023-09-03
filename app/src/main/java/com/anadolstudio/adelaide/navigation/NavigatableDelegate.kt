package com.anadolstudio.adelaide.navigation

import androidx.navigation.NavController
import com.anadolstudio.core.navigation.Add
import com.anadolstudio.core.navigation.Back
import com.anadolstudio.core.navigation.BackTo
import com.anadolstudio.core.navigation.NavigationEvent
import com.anadolstudio.core.navigation.Replace
import com.anadolstudio.core.presentation.Navigatable

class NavigatableDelegate(private val navController: NavController) : Navigatable<NavigateData> {

    override fun handleNavigationEvent(event: NavigationEvent<NavigateData>) {
        when (event) {
            is Add -> navController.navigate(event.data.id, event.data.args)
            is Back -> navController.navigateUp()
            is BackTo -> Unit
            is Replace -> Unit
        }
    }
}
