package com.anadolstudio.photoeditorprocessor

import android.net.Uri
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.anadolstudio.photoeditorprocessor.util.BitmapInfoUtil
import junit.framework.TestCase
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class BitmapInfoUtilAndroidTest : TestCase() {

    @Test
    fun mimeTypeTest() {
        val appContext = InstrumentationRegistry.getInstrumentation().context
        assertEquals(
                "image/jpeg",
                BitmapInfoUtil.getMimeType(appContext, Uri.parse("/testName.jpg"))
        )

        assertEquals(
                "image/png",
                BitmapInfoUtil.getMimeType(appContext, Uri.parse("/testName.png"))
        )

        assertEquals(
                "text/plain",
                BitmapInfoUtil.getMimeType(appContext, Uri.parse("/testName.text"))
        )
    }
}
