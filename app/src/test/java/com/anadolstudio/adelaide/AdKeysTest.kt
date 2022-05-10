package com.anadolstudio.adelaide

import com.anadolstudio.adelaide.data.AdKeys
import org.junit.Assert.assertEquals
import org.junit.Test

class AdKeysTest {

    @Test
    fun getKey() {

        var keyManager = AdKeys.TestKeyManager(AdKeys.Debug)

        assertEquals(AdKeys.KeyManager.appOpenId, keyManager.appOpenId)
        assertEquals(AdKeys.KeyManager.nativeId, keyManager.nativeId)
        assertEquals(AdKeys.KeyManager.bannerId, keyManager.bannerId)
        assertEquals(AdKeys.KeyManager.interstitialId, keyManager.interstitialId)

        assertEquals("ca-app-pub-3940256099942544/3419835294", keyManager.appOpenId)
        assertEquals("ca-app-pub-3940256099942544/2247696110", keyManager.nativeId)
        assertEquals("ca-app-pub-3940256099942544/6300978111", keyManager.bannerId)
        assertEquals("ca-app-pub-3940256099942544/1033173712", keyManager.interstitialId)

        keyManager = AdKeys.TestKeyManager(AdKeys.Release)
        assertEquals("", keyManager.appOpenId)
        assertEquals("", keyManager.nativeId)
        assertEquals("", keyManager.bannerId)
        assertEquals("", keyManager.interstitialId)
    }
}