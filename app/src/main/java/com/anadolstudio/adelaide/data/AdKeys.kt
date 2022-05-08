package com.anadolstudio.adelaide.data

import com.anadolstudio.adelaide.BuildConfig

interface AdKeys {
    val appOpenId: String
    val nativeId: String
    val bannerId: String
    val interstitialId: String

    object Debug : AdKeys {
        override val appOpenId: String = "ca-app-pub-3940256099942544/3419835294"
        override val nativeId: String = "ca-app-pub-3940256099942544/2247696110"
        override val bannerId: String = "ca-app-pub-3940256099942544/6300978111"
        override val interstitialId: String = "ca-app-pub-3940256099942544/1033173712"
    }

    object Release : AdKeys {
        override val appOpenId: String = ""
        override val nativeId: String = ""
        override val bannerId: String = ""
        override val interstitialId: String = ""
    }

    object KeyManager : AdKeys {
        private val KEYS: AdKeys = if (BuildConfig.DEBUG) Debug else Release

        override val appOpenId: String = KEYS.appOpenId
        override val nativeId: String = KEYS.nativeId
        override val bannerId: String = KEYS.bannerId
        override val interstitialId: String = KEYS.interstitialId
    }
}