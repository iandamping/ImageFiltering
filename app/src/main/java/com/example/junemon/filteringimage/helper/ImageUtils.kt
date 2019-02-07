package com.example.junemon.filteringimage.helper

import android.content.Context
import android.content.Intent
import android.content.res.AssetManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Environment
import android.os.StrictMode
import android.provider.MediaStore
import android.support.design.widget.Snackbar
import android.util.Log
import android.view.View
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
/**
 * Created by ian on 07/02/19.
 */

class ImageUtils(var ctx: Context?) {

    fun getBitmapFromAssets(fileName: String, widthImage: Int, heightImage: Int): Bitmap? {
        val assetManager: AssetManager = ctx?.assets!!
        val inputStreams: InputStream
        try {
            val options: BitmapFactory.Options = BitmapFactory.Options()
            options.inJustDecodeBounds = true

            inputStreams = assetManager.open(fileName)
            //calculate sample size
            options.inSampleSize = calculateSampleSize(options, widthImage, heightImage)
            //decode bitmpat with samplesize set
            options.inJustDecodeBounds = false
            return BitmapFactory.decodeStream(inputStreams, null, options)
        } catch (e: IOException) {
            Log.e(this::class.java.simpleName, e.message)
        }
        return null
    }

    fun getBitmapFromGallery(path: Uri, widthImage: Int, heightImage: Int): Bitmap? {
        val filePathColum = arrayOf(MediaStore.Images.Media.DATA)
        val cursor: Cursor? = ctx?.applicationContext?.contentResolver?.query(path, filePathColum, null, null, null)
        cursor?.moveToFirst()

        val columnIndex: Int? = cursor?.getColumnIndex(filePathColum.get(0))
        val picturePath = columnIndex?.let { cursor.getString(it) }
        cursor?.close()

        val options: BitmapFactory.Options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(picturePath, options)

        options.inSampleSize = calculateSampleSize(options, widthImage, heightImage)
        options.inJustDecodeBounds = false
        return BitmapFactory.decodeFile(picturePath, options)
    }


    fun saveImage(views: View, bitmap: Bitmap?, tittle: String) {
        val builder = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())
        val path =
            Environment.getExternalStorageDirectory().toString() + "/" + System.currentTimeMillis() + tittle + ".jpeg"
        val imageFile: File = File(path)
        val quality = 100
        try {
            val outputStream: FileOutputStream = FileOutputStream(imageFile)
            bitmap?.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
            outputStream.flush()
            outputStream.close()
            openPhoto(views, imageFile)
        } catch (e: Throwable) {
            e.printStackTrace()
        }
        MediaScannerConnection.scanFile(ctx, arrayOf(imageFile.toString()), null,
            MediaScannerConnection.OnScanCompletedListener { path, uri ->
                Log.i("ExternalStorage", "Scanned $path:")
                Log.i("ExternalStorage", "-> uri=$uri")
            })
    }

    private fun openPhoto(views: View, imageFile: File) {
        val snackbar = Snackbar
            .make(views, "Image saved to gallery!", Snackbar.LENGTH_LONG)
            .setAction("OPEN") {
                val i = Intent(Intent.ACTION_VIEW)
                val uri = Uri.fromFile(imageFile)
                i.setDataAndType(uri, "image/*")
                ctx?.startActivity(i)
            }
        snackbar.show()
    }

    private fun calculateSampleSize(options: BitmapFactory.Options, requiredWidth: Int, requiredHeight: Int): Int {
        val height = options.outHeight
        val widht = options.outWidth
        var inSampleSize = 1

        if (height > requiredHeight || widht > requiredHeight) {
            val halfHeight = height / 2
            val halfWidth = widht / 2

            while ((halfHeight / inSampleSize) >= requiredHeight && (halfWidth / inSampleSize) >= requiredWidth) {
                inSampleSize *= 2
            }
        }
        return inSampleSize
    }

}