package com.anadolstudio.photoeditorprocessor.util

import android.content.Context
import android.graphics.Point
import android.util.DisplayMetrics
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlin.math.max
import kotlin.math.roundToInt

object DisplayUtil {
    fun dpToPx(context: Context, dp: Int): Int {
        val displayMetrics = context.resources.displayMetrics
        return (dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT)).roundToInt()
    }

    fun pxToDp(context: Context, px: Int): Int {
        val displayMetrics = context.resources.displayMetrics
        return (px / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT)).roundToInt()
    }

    fun getDefaultSize(activity: AppCompatActivity): DisplayMetrics {
        //TODO Возвращаемое значение заменить на x и y
        val displayMetrics = DisplayMetrics()

        /*if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
        activity.display?.getRealMetrics(displayMetrics)
        //TODO значения различаются
    } else {
        @Suppress("DEPRECATION")
        activity.windowManager.defaultDisplay.getMetrics(displayMetrics)
    }*/
        activity.windowManager.defaultDisplay.getMetrics(displayMetrics)
        return displayMetrics
    }

    // TODO need Test
    fun workspaceSize(
        activity: AppCompatActivity,
        vararg views: View,
        checkWidth: Boolean = false,
        checkHeight: Boolean = true
    ): Point {
        val dm = getDefaultSize(activity)
        return with(views) {
            val result = Point(dm.widthPixels, dm.heightPixels)

            if (isEmpty()) return result

            var width = 0
            var height = 0

            for (view in views) {
                if (view.visibility != View.VISIBLE) continue

                if (checkWidth) width += view.width
                if (checkHeight) height += view.height
            }

            result.run {
                x = max(x - width, 0)
                y = max(y - height, 0)
            }

            result
        }
    }
}
