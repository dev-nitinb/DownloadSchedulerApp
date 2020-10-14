package com.example.downloadscheduler.utils

import android.os.Environment
import android.util.Log

import java.io.File

object PublicDownloadStorageDir {

    fun getPublicDownloadStorageDir(): File? {
        var file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "Download Scheduler")
        val nomediaFile = File(file, ".nomedia")

        if (!file.mkdirs()) {
            Log.d("GetDownloadStorageDir", "Main directory exists")
        }

        if (!nomediaFile.exists())
            nomediaFile.mkdir()

        return file
    }
}