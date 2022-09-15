package com.anadolstudio.photoeditorprocessor

import androidx.exifinterface.media.ExifInterface
import com.anadolstudio.photoeditorprocessor.util.BitmapInfoUtil
import org.junit.Assert.assertEquals
import org.junit.Test

class BitmapInfoUtilTest {

    @Test
    fun getDegreesTest() {
        assertEquals(90, BitmapInfoUtil.getDegree(ExifInterface.ORIENTATION_ROTATE_90))
        assertEquals(180, BitmapInfoUtil.getDegree(ExifInterface.ORIENTATION_ROTATE_180))
        assertEquals(270, BitmapInfoUtil.getDegree(ExifInterface.ORIENTATION_ROTATE_270))
    }

}
