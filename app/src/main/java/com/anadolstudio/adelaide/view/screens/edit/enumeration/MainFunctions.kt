package com.anadolstudio.adelaide.view.screens.edit.enumeration

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.anadolstudio.adelaide.R

enum class MainFunctions(
    @StringRes override val textId: Int,
    @DrawableRes override val drawableId: Int,
    val innerFunctions: List<InnerFunctionItem> = listOf()
):FuncItem {

    TRANSFORM(
        R.string.crop_func_transform,
        R.drawable.ic_transform,
        listOf(
            InnerFunctionItem.CROP,
            InnerFunctionItem.TURN,
            InnerFunctionItem.FLIP_HORIZONTAL,
            InnerFunctionItem.FLIP_VERTICAL
        )
    ),

    FILTER(R.string.edit_func_filter, R.drawable.ic_filter),
    EFFECT(R.string.edit_func_effect, R.drawable.ic_effect),
    BLUR(R.string.edit_func_blur, R.drawable.ic_blur),
    EXPOSITION(R.string.edit_func_exposition, R.drawable.ic_overlap),
    FIT(R.string.edit_func_fit, R.drawable.ic_fit),
    CUT(R.string.edit_func_cut, R.drawable.ic_cut),
    MIRROR(R.string.edit_func_mirror, R.drawable.ic_mirror),
    TEXT(R.string.edit_func_text, R.drawable.ic_text),
    STICKER(R.string.edit_func_sticker, R.drawable.ic_sticker),
    ADD_IMAGE(R.string.edit_func_add, R.drawable.ic_add_image),
    BRUSH(R.string.edit_func_brush, R.drawable.ic_brush);

    companion object {
        val mainFunction = listOf(
            TRANSFORM,
            FILTER,
            EFFECT,
            BLUR,
            EXPOSITION,
            FIT,
            CUT,
            MIRROR,
            TEXT,
            STICKER,
            ADD_IMAGE,
            BRUSH,
        )
    }

}