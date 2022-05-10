package com.anadolstudio.photoeditorprocessor

import androidx.exifinterface.media.ExifInterface
import com.anadolstudio.photoeditorprocessor.util.BitmapInfoUtil
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class BitmapInfoUtilTest{

    @Test
    fun getDegreesTest() {
        assertEquals(90, BitmapInfoUtil.getDegree(ExifInterface.ORIENTATION_ROTATE_90))
        assertEquals(180, BitmapInfoUtil.getDegree(ExifInterface.ORIENTATION_ROTATE_180))
        assertEquals(270, BitmapInfoUtil.getDegree(ExifInterface.ORIENTATION_ROTATE_270))
    }

}