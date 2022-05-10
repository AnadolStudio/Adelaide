package com.anadolstudio.photoeditorprocessor.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapShader
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Point
import android.graphics.Rect
import android.graphics.Shader
import com.anadolstudio.photoeditorprocessor.util.BitmapRenderEffects.blur
import kotlin.math.abs
import kotlin.math.min
import kotlin.math.sqrt

object BitmapCutUtil {

    class Cell(val row: Int = 0, var column: Int = 0) {

        companion object {

            fun toCell(index: Int, width: Int): Cell = Cell(index / width, index % width)

            fun toIndex(cell: Cell, width: Int): Int = toIndex(cell.row, cell.column, width)

            fun toIndex(row: Int, column: Int, width: Int): Int = row * width + column
        }

        fun toIndex(width: Int): Int = toIndex(row, column, width)
    }

    const val SIDE_OF_SQUARE = 60
    private const val DEFAULT_RADIUS = 1
    private const val RADIUS_BLUR_DEFAULT = 12
    private const val DEFAULT_DEEP = 5

    fun erosion(bitmap: Bitmap): IntArray {
        val w = bitmap.width
        val h = bitmap.height

        if (w == 0 || h == 0) throw IllegalArgumentException("Weight or height must be more than 0")

        val pixels = IntArray(w * h)
        bitmap.getPixels(pixels, 0, w, 0, 0, w, h)
        return erosion(pixels, w, h)
    }

    fun erosion(pixels: IntArray, width: Int, height: Int): IntArray {
        val delete: MutableSet<Int> = HashSet()
        for (i in 0 until height * width) {
            val pixel = pixels[i]
            if (pixel != Color.TRANSPARENT
                && hasTransparentNeighbors(i, DEFAULT_RADIUS, pixels, width, height)
            ) {
                delete.add(i)
            }
        }

        for (i in delete) {
            pixels[i] = Color.TRANSPARENT
        }

        return pixels
    }

    fun opening(pixels: IntArray, width: Int, height: Int, color: Int): IntArray =
        dilation(
            erosion(pixels, width, height),
            width,
            height, color
        )

    fun opening(bitmap: Bitmap, color: Int): IntArray = dilation(
        erosion(bitmap),
        bitmap.width,
        bitmap.height, color
    )

    fun closing(pixels: IntArray, width: Int, height: Int, color: Int): IntArray = erosion(
        dilation(pixels, width, height, color),
        width,
        height
    )

    fun closing(bitmap: Bitmap, color: Int): IntArray = erosion(
        dilation(bitmap, color),
        bitmap.width,
        bitmap.height
    )

    fun dilation(bitmap: Bitmap, color: Int): IntArray {
        val w = bitmap.width
        val h = bitmap.height
        if (w == 0 || h == 0) throw IllegalArgumentException("Weight or height must be more than 0")

        val pixels = IntArray(w * h)
        bitmap.getPixels(pixels, 0, w, 0, 0, w, h)
        return dilation(pixels, w, h, color)
    }

    fun dilation(pixels: IntArray, width: Int, height: Int, color: Int): IntArray {
        val add: MutableSet<Int> = HashSet()

        for (i in 0 until height * width) {
            val pixel = pixels[i]

            if (pixel != Color.TRANSPARENT) {
                for (neighbour in getNeighbors(i, DEFAULT_RADIUS, width, height)) {
                    if (neighbour == Color.TRANSPARENT) {
                        add.add(neighbour)
                    }
                }
            }
        }

        for (i in add) pixels[i] = color

        return pixels
    }

    fun getEdgePixels(bitmap: Bitmap, deep: Int = DEFAULT_DEEP): HashMap<Int, Int?> {
        val w = bitmap.width
        val h = bitmap.height

        if (w == 0 || h == 0) throw IllegalArgumentException("Weight or height must be more than 0")

        val pixels = IntArray(w * h)
        bitmap.getPixels(pixels, 0, w, 0, 0, w, h)
        return getEdgePixels(pixels, w, h, deep)
    }

    fun getEdgePixels(pixels: IntArray, w: Int, h: Int, deep: Int): HashMap<Int, Int?> {
        val map = HashMap<Int, Int?>()
        for (i in 0 until h * w) {

            if (pixels[i] != Color.TRANSPARENT && hasTransparentNeighbors(i, 1, pixels, w, h)) {

                for (j in getCircleNeighbors(i, deep, w, h)) {
                    if (pixels[j] == Color.TRANSPARENT) {
                        continue
                    }

                    val r = getRadius(i, j, w)

                    map[j] = if (map.containsKey(j)) min(r, map[j]!!) else r
                }
            }
        }

        return map
    }

    fun getEdgePixelsList(pixels: IntArray, w: Int, h: Int, deep: Int): ArrayList<Int> {
        val result = ArrayList<Int>()

        for (i in 0 until h * w) {

            if (pixels[i] != Color.TRANSPARENT && hasTransparentNeighbors(i, 1, pixels, w, h)) {

                for (j in getCircleNeighbors(i, deep, w, h)) {

                    if (pixels[j] == Color.TRANSPARENT) continue

                    result.add(j)
                }
            }
        }

        return result
    }

    private fun hasTransparentNeighbors(
        index: Int,
        radius: Int,
        pixels: IntArray,
        width: Int,
        height: Int
    ): Boolean {
        val neighbors = getNeighbors(index, radius, width, height)

        for (i in neighbors) {

            if (pixels[i] == Color.TRANSPARENT) {
                return true
            }
        }

        return false
    }

    fun getNeighbors(center: Int, radius: Int, width: Int, height: Int): ArrayList<Int> {
        val result = ArrayList<Int>()
        if (center < 0) {
            return result
        }
        var top = Cell.toCell(center, width)
        var left = Cell.toCell(center, width)
        var bottom = Cell.toCell(center, width)
        var right = Cell.toCell(center, width)
        val centerCell = Cell.toCell(center, width)

        for (i in 1..radius) {
            //Поиск верхней границы
            var current = Cell(centerCell.row - i, centerCell.column)
            if (current.row > 0) {
                top = current
            }

            //Поиск левой границы
            current = Cell(centerCell.row, centerCell.column - i)
            if (current.column > 0) {
                left = current
            }

            //Поиск нижней границы
            current = Cell(centerCell.row + i, centerCell.column)
            if (current.row < height - 1) {
                bottom = current
            }

            //Поиск правой границы
            current = Cell(centerCell.row, centerCell.column + i)
            if (current.column < width - 1) {
                right = current
            }
        }

        for (row in top.row..bottom.row) {
            for (column in left.column..right.column) {
                result.add(Cell.toIndex(row, column, width))
            }
        }

        return result
    }

    fun getCircleNeighbors(center: Int, radius: Int, width: Int, height: Int): ArrayList<Int> {
        val neighbors = getNeighbors(center, radius, width, height)
        val result = ArrayList<Int>()
        val side = sqrt((neighbors.size + 1).toDouble()).toInt()
        val LT = Cell(0, 0)
        val RT = Cell(0, side - 1)
        val LB = Cell(side - 1, 0)
        val RB = Cell(side - 1, side - 1)
        val doNotUse = ArrayList<Int>()

        if (radius >= 3) { // 3
            doNotUse.add(LT.toIndex(side))
            doNotUse.add(RT.toIndex(side))
            doNotUse.add(LB.toIndex(side))
            doNotUse.add(RB.toIndex(side))
        }

        if (radius >= 7) {
            doNotUse.add(LT.toIndex(side) + 1)
            doNotUse.add(LT.toIndex(side) + side)
            doNotUse.add(RT.toIndex(side) - 1)
            doNotUse.add(RT.toIndex(side) + side)
            doNotUse.add(LB.toIndex(side) + 1)
            doNotUse.add(LB.toIndex(side) - side)
            doNotUse.add(RB.toIndex(side) - 1)
            doNotUse.add(RB.toIndex(side) - side)
        }

        for (i in neighbors.indices) {

            if (doNotUse.contains(i)) {
                continue
            }

            result.add(neighbors[i])
        }

        return result
    }

    fun getRadius(indexCenter: Int, indexPoint: Int, width: Int): Int {
        if (indexCenter == indexPoint) {
            return 0
        }
        val cellCenter = Cell.toCell(indexCenter, width)
        val cellPoint = Cell.toCell(indexPoint, width)
        val cathetOne = abs(cellCenter.column - cellPoint.column)
        val cathetTwo = abs(cellCenter.row - cellPoint.row)
        return cathetOne + cathetTwo
    }

    fun getBitmapFromColor(color: Int, width: Int, height: Int): Bitmap? {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawColor(color)
        return bitmap
    }

    fun createNullBackground(point: Point): Bitmap {
        val background = createNullSquare(SIDE_OF_SQUARE)
        val bitmap = Bitmap.createBitmap(point.x, point.y, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)

        paint.shader = BitmapShader(background, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT)
        canvas.drawRect(Rect(0, 0, point.x, point.y), paint)

        return bitmap
    }

    fun createNullSquare(side: Int): Bitmap {
        val background = Bitmap.createBitmap(side, side, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(background)
        val paint = Paint()
        paint.color = Color.WHITE
        val x = side / 2
        canvas.drawRect(Rect(0, 0, x, x), paint)
        canvas.drawRect(Rect(x, x, x * 2, x * 2), paint)
        paint.color = Color.LTGRAY
        canvas.drawRect(Rect(0, x, x, x * 2), paint)
        canvas.drawRect(Rect(x, 0, x * 2, x), paint)
        return background
    }

    fun getColorWithAlpha(color: Int, alpha: Int): Int {
        return Color.argb(alpha, Color.red(color), Color.green(color), Color.blue(color))
    }

    fun blur(
        context: Context,
        main: IntArray,
        w: Int,
        h: Int,
        radius: Int = RADIUS_BLUR_DEFAULT
    ): Bitmap = blur(
        context,
        Bitmap.createBitmap(main, w, h, Bitmap.Config.ARGB_8888),
        radius.toFloat()
    )

    fun blur(
        context: Context,
        main: IntArray,
        edge: IntArray,
        w: Int,
        h: Int,
        radius: Int = RADIUS_BLUR_DEFAULT
    ): Bitmap {
        val edgeBitmap = Bitmap.createBitmap(edge, w, h, Bitmap.Config.ARGB_8888)
        val mainBitmap = Bitmap.createBitmap(main, w, h, Bitmap.Config.ARGB_8888)
        val result = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(result)

        val blur = blur(context, edgeBitmap, radius.toFloat())
        canvas.drawBitmap(blur, 0f, 0f, null)
        canvas.drawBitmap(mainBitmap, 0f, 0f, null)

        mainBitmap.recycle()
        blur.recycle()
        edgeBitmap.recycle()

        return result
    }
}