package com.anadolstudio.photoeditorprocessor.functions.transform

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.anadolstudio.photoeditorprocessor.R

enum class RatioItem(
    @StringRes val textId: Int,
    @DrawableRes var drawableId: Int?,
    val ratio: Ratio,
    val density: Density
) {

    FREE(R.string.crop_func_free, R.drawable.ic_crop_free, Ratio(), Density()),
    IG_1X1(R.string.crop_func_instagram_1x1, R.drawable.ic_instagram_vector, Ratio(1, 1), Density(40, 40)),
    IG_4X5(R.string.crop_func_instagram_4x5, R.drawable.ic_instagram_vector, Ratio(4, 5), Density(40, 50)),
    INST_STORY(R.string.crop_func_instagram_story, R.drawable.ic_instagram_vector, Ratio(9, 16), Density(40, 71)),
    MOVIE(R.string.crop_func_movie, R.drawable.ic_movie_vector, Ratio(47, 20), Density(71, 30)),
    FB_POST(R.string.crop_func_post, R.drawable.ic_facebook_vector, Ratio(4, 3), Density(53, 40)),
    FB_COVER(R.string.crop_func_facebook_cover, R.drawable.ic_facebook_vector, Ratio(37, 14), Density(70, 30)),
    YB_COVER(R.string.crop_func_facebook_cover, R.drawable.ic_youtube_vector, Ratio(16, 9), Density(71, 40)),
    TW_POST(R.string.crop_func_post, R.drawable.ic_twitter_vector, Ratio(2, 1), Density(80, 40)),
    TW_HEADER(R.string.crop_func_twitter_header, R.drawable.ic_twitter_vector, Ratio(3, 1), Density(90, 30)),
    A4(R.string.crop_func_A4, null, Ratio(70, 99), Density(40, 57)),
    RATIO_AUTO(R.string.crop_func_display, R.drawable.ic_phone_android, Ratio(-1, -1), Density(40, 71)),
    RATIO_2X3(R.string.crop_func_ratio2x3, null, Ratio(2, 3), Density(40, 60)),
    RATIO_3X2(R.string.crop_func_ratio3x2, null, Ratio(3, 2), Density(60, 40)),
    RATIO_3X4(R.string.crop_func_ratio3x4, null, Ratio(3, 4), Density(40, 53)),
    RATIO_4X3(R.string.crop_func_ratio4x3, null, Ratio(4, 3), Density(53, 40)),
    RATIO_5X4(R.string.crop_func_ratio5x4, null, Ratio(5, 4), Density(50, 40)),
    RATIO_9X16(R.string.crop_func_ratio9x16, null, Ratio(9, 16), Density(40, 71)),
    RATIO_16X9(R.string.crop_func_ratio16x9, null, Ratio(16, 9), Density(71, 40));

    data class Ratio(var x: Int = 0, var y: Int = 0)

    data class Density(val w: Int = 40, val h: Int = 40)
}