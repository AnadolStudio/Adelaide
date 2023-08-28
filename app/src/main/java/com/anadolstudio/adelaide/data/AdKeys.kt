package com.anadolstudio.adelaide.data

import com.anadolstudio.adelaide.BuildConfig

interface AdKeys {
    val appOpenId: String
    val nativeId: String
    val bannerId: String
    val interstitialId: String

    private object Debug : AdKeys {
        override val appOpenId: String = "ca-app-pub-3940256099942544/3419835294"
        override val nativeId: String = "ca-app-pub-3940256099942544/2247696110"
        override val bannerId: String = "ca-app-pub-3940256099942544/6300978111"
        override val interstitialId: String = "ca-app-pub-3940256099942544/1033173712"
    }

    private object Release : AdKeys {
        override val appOpenId: String = ""
        override val nativeId: String = ""
        override val bannerId: String = ""
        override val interstitialId: String = ""
    }

    abstract class AbstractKeyManager(keys: AdKeys) : AdKeys {

        override val appOpenId: String = keys.appOpenId
        override val nativeId: String = keys.nativeId
        override val bannerId: String = keys.bannerId
        override val interstitialId: String = keys.interstitialId
    }

    object KeyManager : AbstractKeyManager(if (BuildConfig.DEBUG) Debug else Release)

    class TestKeyManager(keys: AdKeys) : AbstractKeyManager(keys)
}
