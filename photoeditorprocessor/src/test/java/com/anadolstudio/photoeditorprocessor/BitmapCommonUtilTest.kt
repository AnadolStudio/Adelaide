package com.anadolstudio.photoeditorprocessor

import com.anadolstudio.photoeditorprocessor.util.BitmapCommonUtil
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class BitmapCommonUtilTest {

    @Test
    fun scaleRatioToCircumscribedTest() {

        var mainW = 500F
        var mainH = 1000F
        var supportW = 1000F
        var supportH = 2000F
        assertCircumscribed(mainW, supportW, mainH, supportH)

        mainW = 700F
        mainH = 1200F
        supportW = 350F
        supportH = 600F
        assertCircumscribed(mainW, supportW, mainH, supportH)

        // Не пропорциональные
        mainW = 1080F
        mainH = 1350F
        supportW = 1080F
        supportH = 1717F
        assertCircumscribed(mainW, supportW, mainH, supportH)

        mainW = 700F
        mainH = 1200F
        supportW = 300F
        supportH = 600F
        assertCircumscribed(mainW, supportW, mainH, supportH)

        mainW = 500F
        mainH = 1000F
        supportW = 700F
        supportH = 1200F
        assertCircumscribed(mainW, supportW, mainH, supportH)

        mainW = 700F
        mainH = 1200F
        supportW = 500F
        supportH = 1000F
        assertCircumscribed(mainW, supportW, mainH, supportH)

        mainW = 500F
        mainH = 1000F
        supportW = 700F
        supportH = 900F
        assertCircumscribed(mainW, supportW, mainH, supportH)
    }

    private fun assertCircumscribed(mainW: Float, supportW: Float, mainH: Float, supportH: Float) {
        val scale = BitmapCommonUtil.scaleRatioCircumscribed(mainW, mainH, supportW, supportH)
        println("$scale $mainW $mainH ${supportW * scale} ${supportH * scale}")
        assertTrue(
            mainW.toInt() == (supportW * scale).toInt() && mainH.toInt() <= (supportH * scale).toInt()
                    || (mainW.toInt() <= (supportW * scale).toInt() && mainH.toInt() == (supportH * scale).toInt())
        )

    }

    @Test
    fun scaleRatioToInscribedTest() {

        //Пропорциональные
        var mainW = 500F
        var mainH = 1000F
        var supportW = 1000F
        var supportH = 2000F
        assertInscribed(mainW, supportW, mainH, supportH)

        mainW = 700F
        mainH = 1200F
        supportW = 350F
        supportH = 600F
        assertInscribed(mainW, supportW, mainH, supportH)

        // Не пропорциональные
        mainW = 700F
        mainH = 1200F
        supportW = 300F
        supportH = 600F
        assertInscribed(mainW, supportW, mainH, supportH)

        mainW = 500F
        mainH = 1000F
        supportW = 700F
        supportH = 1200F
        assertInscribed(mainW, supportW, mainH, supportH)

        mainW = 700F
        mainH = 1200F
        supportW = 500F
        supportH = 1000F
        assertInscribed(mainW, supportW, mainH, supportH)

        mainW = 500F
        mainH = 1000F
        supportW = 700F
        supportH = 900F
        assertInscribed(mainW, supportW, mainH, supportH)
    }

    private fun assertInscribed(
        mainW: Float,
        supportW: Float,
        mainH: Float,
        supportH: Float
    ) {
        val scale = BitmapCommonUtil.scaleRatioInscribed(mainW, mainH, supportW, supportH)
        println("$scale $mainW $mainH ${supportW * scale} ${supportH * scale}")

        assertTrue(
            (mainW == supportW * scale && mainH >= supportH * scale)
                    || (mainW >= supportW * scale && mainH == supportH * scale)
        )
    }

}