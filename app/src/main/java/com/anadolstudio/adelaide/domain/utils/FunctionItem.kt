package com.anadolstudio.adelaide.domain.utils

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.anadolstudio.adelaide.R
import com.anadolstudio.adelaide.domain.utils.FunctionItem.*

enum class FunctionItem(
    @StringRes val textId: Int,
    @DrawableRes var drawableId: Int,
    val innerFunctions: List<FunctionItem> = listOf()
) {
    //Тут описанны все функции
    CROP(R.string.crop, R.drawable.ic_crop),
    TURN(R.string.turn, R.drawable.ic_rotate),
    FLIP_HORIZONTAL(R.string.flip_h, R.drawable.ic_flip_horizontal),
    FLIP_VERTICAL(R.string.flip_v, R.drawable.ic_flip_vertical),

    TRANSFORM(
        R.string.transform,
        R.drawable.ic_transform,
        listOf(CROP, TURN, FLIP_HORIZONTAL, FLIP_VERTICAL)
    ),

    FILTER(R.string.filter, R.drawable.ic_filter),

    EFFECT(R.string.effect, R.drawable.ic_effect),

    BLUR(R.string.blur, R.drawable.ic_blur),

    EXPOSITION(R.string.exposition, R.drawable.ic_overlap),

    FIT(R.string.fit, R.drawable.ic_fit),

    CUT(R.string.cut, R.drawable.ic_cut),

    MIRROR(R.string.mirror, R.drawable.ic_mirror),

    TEXT(R.string.text, R.drawable.ic_text),

    STICKER(R.string.sticker, R.drawable.ic_sticker),

    ADD_IMAGE(R.string.add, R.drawable.ic_add_image),

    BRUSH(R.string.brush, R.drawable.ic_brush)
}

class MainFunctions {
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