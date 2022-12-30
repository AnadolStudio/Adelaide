package com.anadolstudio.adelaide.ui.screens.gallery

import android.content.Context
import android.widget.ArrayAdapter
import androidx.annotation.LayoutRes

class GalleryFolderAdapter(
        context: Context, @LayoutRes id: Int, data: List<String>
) : ArrayAdapter<String>(context, id, data) {

    fun setData(data: List<String>) {
        clear()
        addAll(data)
    }
}
