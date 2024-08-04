package com.anadolstudio.adelaide.di

import android.app.Activity
import android.content.Context
import androidx.fragment.app.Fragment

fun Context.getSharedModule(): SharedComponent {
    return (applicationContext as SharedComponentProvider).getModule()
}

fun Activity.getSharedModule(): SharedComponent {
    return (applicationContext as SharedComponentProvider).getModule()
}

fun Fragment.getSharedModule(): SharedComponent {
    return requireContext().getSharedModule()
}
