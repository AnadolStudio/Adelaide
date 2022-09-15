package com.anadolstudio.photoeditorprocessor.util

import android.os.Environment
import android.util.Log
import com.anadolstudio.core.common_util.TimeUtil
import java.io.File
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object FileUtil {

    private const val TAG = "FileUtil"

    fun getFileName(): String {
        val timeFormat: DateFormat = SimpleDateFormat(TimeUtil.DEFAULT_FORMAT, Locale.getDefault())
        return "IMG_${timeFormat.format(Date())}.jpeg" // TODO JPEG?
    }

    fun createAppDir(nameDir: String, fileName: String = getFileName()): File {
        val directory = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                        .toString() + File.separator + nameDir
        )

        if (!directory.exists() && !directory.isDirectory) {
            // create empty directory
            if (!directory.mkdirs()) Log.d(TAG, "Unable to create app dir!")
        }

        return File(directory, fileName)
    }
}
