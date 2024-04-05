package com.anadolstudio.adelaide.di.viewmodel

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.anadolstudio.adelaide.di.DI
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton

@Singleton
internal class ViewModelsInjector @Inject constructor(
    val viewModelFactory: ViewModelFactory,
    private val factories: Map<Class<out ViewModel>, @JvmSuppressWildcards Provider<ViewModel>>,
) {

    inline fun <reified T : ViewModel> assistedViewModelFactory(): T = assistedViewModelFactory(T::class.java)

    fun <T : ViewModel> assistedViewModelFactory(factoryClass: Class<T>): T {
        val provider = factories[factoryClass]
            ?: throw IllegalArgumentException("Binding for $factoryClass not found.")

        @Suppress("UNCHECKED_CAST")
        return provider.get() as T
    }
}

internal val LocalViewModelFactory = staticCompositionLocalOf { DI.viewModelsInjector.viewModelFactory }

@Composable
internal inline fun <reified VM : ViewModel> daggerViewModel(
    viewModelStoreOwner: ViewModelStoreOwner = checkNotNull(LocalViewModelStoreOwner.current) {
        "No ViewModelStoreOwner was provided via LocalViewModelStoreOwner"
    },
    factory: ViewModelProvider.Factory = LocalViewModelFactory.current,
): VM {
    return viewModel(factory = factory, viewModelStoreOwner = viewModelStoreOwner)
}

@Composable
internal inline fun <reified T : Any> rememberViewModelFactory(): T {
    return remember { DI.viewModelsInjector.assistedViewModelFactory() }
}

@Composable
internal inline fun <reified VM : ViewModel> assistedViewModel(
    crossinline createViewModel: () -> VM,
): VM {
    val factory = remember { createSingleViewModelFactory(createViewModel) }
    return viewModel(factory = factory)
}

internal inline fun createSingleViewModelFactory(
    crossinline createViewModel: () -> ViewModel,
): ViewModelProvider.Factory {
    return object : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T = createViewModel.invoke() as T
    }
}
