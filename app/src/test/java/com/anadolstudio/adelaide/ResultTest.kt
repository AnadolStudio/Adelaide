package com.anadolstudio.adelaide

import com.anadolstudio.adelaide.data.AssetData
import junit.framework.Assert.assertFalse
import junit.framework.Assert.assertTrue
import org.junit.Test
import java.util.regex.Pattern

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ResultTest {

    @Test
    fun patternTest() {
        val pattern = Pattern.compile(".*[.].*")
        assertTrue(pattern.matcher("dvl.txt").find())
        assertTrue(pattern.matcher("sfa.dvl.txt").find())
        assertFalse(pattern.matcher("dvltxt").find())
        assertFalse(pattern.matcher("dvl/txt").find())
    }

}