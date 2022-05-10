package com.anadolstudio.photoeditorprocessor

import com.anadolstudio.photoeditorprocessor.util.BitmapCommonUtil
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ScaleTest {

    @Test
    fun getScaleRatioTest() {

        var mainW = 500F
        var mainH = 1000F
        var supportW = 1000F
        var supportH = 2000F
        var scale = BitmapCommonUtil.getScaleRatio(mainW, mainH, supportW, supportH)

        assertTrue(mainW == supportW * scale && mainH == supportH * scale)
        println(scale)

        mainW = 700F
        mainH = 1200F
        supportW = 350F
        supportH = 600F
        scale = BitmapCommonUtil.getScaleRatio(mainW, mainH, supportW, supportH)

        println(scale)
        assertTrue(mainW == supportW * scale && mainH == supportH * scale)

        mainW = 500F
        mainH = 1000F
        supportW = 700F
        supportH = 1200F
        scale = BitmapCommonUtil.getScaleRatio(mainW, mainH, supportW, supportH)

        assertTrue(mainW == supportW * scale || mainH == supportH * scale)
        println(scale)

        mainW = 700F
        mainH = 1200F
        supportW = 500F
        supportH = 1000F
        scale = BitmapCommonUtil.getScaleRatio(mainW, mainH, supportW, supportH)

        println(scale)
        assertTrue(mainW == supportW * scale || mainH == supportH * scale)

        mainW = 500F
        mainH = 1000F
        supportW = 700F
        supportH = 900F

        scale = BitmapCommonUtil.getScaleRatio(mainW, mainH, supportW, supportH)
        println(scale)
        assertTrue(mainW == supportW * scale || mainH == supportH * scale)
    }

}