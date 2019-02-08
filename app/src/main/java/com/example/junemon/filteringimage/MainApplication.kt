package com.example.junemon.filteringimage

import android.app.Application
import android.content.Context
import android.media.MediaScannerConnection
import android.os.Environment
import android.util.Log
import java.io.File


/**
 * Created by ian on 07/02/19.
 */

class MainApplication : Application() {

    companion object {
        var IMAGE_NAME: String = "jun_testing.jpg"
        val RequestSelectGalleryImage: Int = 102
        val RequestOpenCamera: Int = 234
        val saveCaptureImagePath = "picture" + System.currentTimeMillis() + ".jpeg"
        val saveFilterImagePath = "filterImage" + System.currentTimeMillis() + ".jpeg"
        val maxWidth = 612
        val maxHeight = 816

        fun nonVoidCustomMediaScannerConnection(ctx: Context?, paths: String?): File {
            //save inside PICTURES directory in your phone
            val directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            val passingFile = File(directory, paths)
            MediaScannerConnection.scanFile(ctx, arrayOf(passingFile.toString()), null) { path, uri ->
                Log.i("ExternalStorage", "Scanned $path:")
                Log.i("ExternalStorage", "-> uri=$uri")
            }
            return passingFile
        }

        fun voidCustomMediaScannerConnection(ctx: Context?, paths: String?) {
            val directory = Environment.getExternalStorageDirectory()
            val passingFile = File(directory, paths)
            MediaScannerConnection.scanFile(ctx, arrayOf(passingFile.toString()), null) { path, uri ->
                Log.i("ExternalStorage", "Scanned $path:")
                Log.i("ExternalStorage", "-> uri=$uri")
            }
        }

    }
}