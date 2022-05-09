package com.anadolstudio.adelaide.data

import android.content.Context
import android.util.Log
import java.io.IOException

object AssetData {

    fun getPathList(context: Context, dir: AssetsDirections): MutableList<String> {
        val assetManager = context.assets
        val dataList = mutableListOf<String>()

        try {
            val list = assetManager.list(dir.nameDir)
            list?.also { for (s in it) dataList.add(getPath(dir, s)) }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return dataList
    }

    private fun getPath(dir: AssetsDirections, name: String): String =
        "file:///android_asset/${dir.nameDir}/$name"
}