package com.anadolstudio.adelaide.view.screens.edit.enumeration

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.anadolstudio.adelaide.R

enum class InnerFunctionItem(
    @StringRes override val textId: Int,
    @DrawableRes override val drawableId: Int,
) :FuncItem {

    //Тут описанны все функции
    CROP(R.string.edit_func_crop, R.drawable.ic_crop),
    TURN(R.string.crop_func_turn, R.drawable.ic_rotate),
    FLIP_HORIZONTAL(R.string.crop_func_flip_h, R.drawable.ic_flip_horizontal),
    FLIP_VERTICAL(R.string.crop_func_flip_v, R.drawable.ic_flip_vertical),
}