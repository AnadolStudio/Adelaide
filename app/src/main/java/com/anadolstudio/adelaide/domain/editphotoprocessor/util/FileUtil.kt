package com.anadolstudio.adelaide.domain.editphotoprocessor.util

import android.content.Context
import android.os.Environment
import android.util.Log
import com.anadolstudio.adelaide.R
import com.anadolstudio.adelaide.view.screens.edit.EditActivity
import com.anadolstudio.core.util.TimeUtil
import java.io.File
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object FileUtil {

    fun getFileName(): String {
        val timeFormat: DateFormat = SimpleDateFormat(TimeUtil.DEFAULT_FORMAT, Locale.getDefault())
        return "IMG_${timeFormat.format(Date())}.jpeg" // TODO JPEG?
    }

    fun createAppDir(context: Context, fileName:String = getFileName()): File {
        val directory = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                .toString() + File.separator + context.getString(R.string.app_name)
        )

        if (!directory.exists() && !directory.isDirectory) {
            // create empty directory
            if (!directory.mkdirs()) Log.d(EditActivity.TAG, "Unable to create app dir!")
        }

        return File(directory, fileName)
    }
}