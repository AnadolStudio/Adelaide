package com.anadolstudio.adelaide.feature.gallery.presetnation.model

import android.content.Context
import com.anadolstudio.adelaide.R

data class Folder(
        val name: String,
        val value: String? = null
) {
    companion object {
        fun getDefaultFolder(context: Context): Folder = Folder(
                name = context.getString(R.string.gallery_toolbar_title),
                value = null
        )
    }
}

internal fun String.toFolder(): Folder = Folder(name = this, value = this)
internal fun String.toFolder(value: String): Folder = Folder(name = this, value = value)
