package com.anadolstudio.core.view

import android.view.View

fun View.show(visibility: Boolean) {
    this.visibility = if (visibility) View.VISIBLE else View.GONE
}