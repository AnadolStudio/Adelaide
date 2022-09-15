package com.anadolstudio.adelaide.data

import android.content.Context
import android.content.res.AssetManager
import java.io.IOException
import java.util.regex.Pattern

object AssetData {

    fun getPathList(context: Context, dir: AssetsDirections): MutableList<String> {
        val assetManager = context.assets
        val dataList = mutableListOf<String>()

        getFiles(assetManager, dir.nameDir, dataList)

        return dataList
    }

    private fun getFiles(
            assetManager: AssetManager,
            nameDir: String,
            dataList: MutableList<String>
    ) {
        try {
            val list = assetManager.list(nameDir)
                    ?: throw Exception("Asset list is null")

            list.forEach { path ->

                if (isFile(path)) {
                    dataList.add(getPath(nameDir, path))
                } else {
                    getFiles(assetManager, "$nameDir/$path", dataList)
                }
            }

        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun isFile(path: String): Boolean = Pattern.compile(".*[.].*").matcher(path).find()

    private fun getPath(dir: AssetsDirections, name: String): String = getPath(dir.nameDir, name)

    private fun getPath(nameDir: String, name: String): String = "file:///android_asset/$nameDir/$name"

}
