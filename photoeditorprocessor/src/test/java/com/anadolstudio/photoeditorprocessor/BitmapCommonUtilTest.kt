package com.anadolstudio.photoeditorprocessor

import com.anadolstudio.photoeditorprocessor.util.BitmapCommonUtil
import org.junit.Assert.assertTrue
import org.junit.Test

class BitmapCommonUtilTest {

    companion object {
        private val BIGGER_MAIN_SIZES = listOf(
            TestItem(700F, 1200F, 350F, 600F),
            TestItem(700F, 1200F, 300F, 600F),
            TestItem(700F, 1200F, 500F, 600F)
        )

        private val BIGGER_SUPPORT_SIZES = listOf(
            TestItem(500F, 1000F, 700F, 1200F),
            TestItem(300F, 900F, 700F, 1200F),
        )

        private val ONE_BIGGER_MAIN_SIZES = listOf(
            TestItem(1080F, 1717F, 1080F, 1350F),
            TestItem(700F, 1000F, 700F, 900F)
        )

        private val ONE_BIGGER_SUPPORT_SIZES = listOf(
            TestItem(500F, 1000F, 500F, 2000F),
            TestItem(1080F, 1350F, 1080F, 1717F),
        )

        private val BASE_SIZES = listOf(
            TestItem(500F, 1000F, 700F, 900F),
            TestItem(700F, 1200F, 1900F, 900F),
            TestItem(900F, 1000F, 700F, 900F),
            TestItem(300F, 1000F, 700F, 900F),
            TestItem(900F, 500F, 700F, 300F),
            TestItem(500F, 1000F, 700F, 900F),
            TestItem(0F, 0F, 0F, 0F),
            TestItem(1000F, 1000F, 1000F, 1000F),
        )
    }

    @Test
    fun circumscribedTest_AllMainSizesBiggerThanSupportSizes() {
        BIGGER_MAIN_SIZES.forEach {
            assertCircumscribed(it)
        }
    }

    @Test
    fun circumscribedTest_AllSupportSizesBiggerThanMainSizes() {
        BIGGER_SUPPORT_SIZES.forEach {
            assertCircumscribed(it)
        }
    }

    @Test
    fun circumscribedTest_OneMainSizesBiggerThanSupportOneSize() {
        ONE_BIGGER_MAIN_SIZES.forEach {
            assertCircumscribed(it)
        }
    }

    @Test
    fun circumscribedTest_OneSupportSizesBiggerThanMainOneSize() {
        ONE_BIGGER_SUPPORT_SIZES.forEach {
            assertCircumscribed(it)
        }
    }

    @Test
    fun circumscribedTest_Base() {
        BASE_SIZES.forEach {
            assertCircumscribed(it)
        }
    }

    private fun assertCircumscribed(testItem: TestItem) {
        assertCircumscribed(testItem.main.width, testItem.main.height, testItem.support.width, testItem.support.height)
    }

    private fun assertCircumscribed(mainW: Float, supportW: Float, mainH: Float, supportH: Float) {
        val scale = BitmapCommonUtil.scaleRatioCircumscribed(mainW, mainH, supportW, supportH)

        assertTrue(
            mainW.toInt() == (supportW * scale).toInt() && mainH.toInt() <= (supportH * scale).toInt()
                    || (mainW.toInt() <= (supportW * scale).toInt() && mainH.toInt() == (supportH * scale).toInt())
        )
    }

    @Test
    fun inscribedTest_AllMainSizesBiggerThanSupportSizes() {
        BIGGER_MAIN_SIZES.forEach {
            assertInscribed(it)
        }
    }

    @Test
    fun inscribedTest_AllSupportSizesBiggerThanMainSizes() {
        BIGGER_SUPPORT_SIZES.forEach {
            assertInscribed(it)
        }
    }

    @Test
    fun inscribedTest_OneMainSizesBiggerThanSupportOneSize() {
        ONE_BIGGER_MAIN_SIZES.forEach {
            assertInscribed(it)
        }
    }

    @Test
    fun inscribedTest_OneSupportSizesBiggerThanMainOneSize() {
        ONE_BIGGER_SUPPORT_SIZES.forEach {
            assertInscribed(it)
        }
    }

    @Test
    fun inscribedTest_Base() {
        BASE_SIZES.forEach {
            assertInscribed(it)
        }
    }

    private fun assertInscribed(testItem: TestItem) {
        assertInscribed(testItem.main.width, testItem.main.height, testItem.support.width, testItem.support.height)
    }

    private fun assertInscribed(
        mainW: Float,
        supportW: Float,
        mainH: Float,
        supportH: Float
    ) {
        val scale = BitmapCommonUtil.scaleRatioInscribed(mainW, mainH, supportW, supportH)

        assertTrue(
            mainW.toInt() == (supportW * scale).toInt() && mainH.toInt() >= (supportH * scale).toInt()
                    || (mainW.toInt() >= (supportW * scale).toInt() && mainH.toInt() == (supportH * scale).toInt())
        )
    }


    data class TestItem(val main: BitmapItem, val support: BitmapItem) {

        constructor(
            mainWidth: Float,
            mainHeight: Float,
            supportWidth: Float,
            supportHeight: Float
        ) : this(BitmapItem(mainWidth, mainHeight), BitmapItem(supportWidth, supportHeight))

        data class BitmapItem(val width: Float, val height: Float)

    }
}
