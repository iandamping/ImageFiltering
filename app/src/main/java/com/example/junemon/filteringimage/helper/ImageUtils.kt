package com.example.junemon.filteringimage.helper

import android.content.Context
import android.content.Intent
import android.content.res.AssetManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.support.design.widget.Snackbar
import android.support.v4.content.FileProvider
import android.util.Log
import android.view.View
import com.example.junemon.filteringimage.MainApplication
import com.example.junemon.filteringimage.MainApplication.Companion.nonVoidCustomMediaScannerConnection
import com.example.junemon.filteringimage.MainApplication.Companion.saveFilterImagePath
import com.example.junemon.filteringimage.MainApplication.Companion.voidCustomMediaScannerConnection
import com.example.junemon.filteringimage.R
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


    fun saveImage(views: View, bitmap: Bitmap?) {
        val pictureDirectory = Environment.getExternalStorageDirectory()
        val imageFile = File(pictureDirectory, saveFilterImagePath)
        val quality = 100
        try {
            val outputStream = FileOutputStream(imageFile)
            bitmap?.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
            outputStream.flush()
            outputStream.close()
            openPhoto(views, imageFile)
        } catch (e: Throwable) {
            e.printStackTrace()
        }
        voidCustomMediaScannerConnection(ctx, saveFilterImagePath)
    }

    fun createImageFileFromPhoto(): File {
        return nonVoidCustomMediaScannerConnection(ctx, MainApplication.saveCaptureImagePath)
    }

    //decode File into Bitmap and compress it
    fun decodeSampledBitmapFromFile(imageFile: File, reqWidth: Int, reqHeight: Int): Bitmap {
        // First decode with inJustDecodeBounds=true to check dimensions
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(imageFile.absolutePath, options)

        // Calculate inSampleSize
        options.inSampleSize = calculateSampleSize(options, reqWidth, reqHeight)

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false

        var scaledBitmap = BitmapFactory.decodeFile(imageFile.absolutePath, options)

        //check the rotation of the image and display it properly
        val exif = ExifInterface(imageFile.absolutePath)
        val orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0)
        val matrix = Matrix()
        if (orientation == 6) {
            matrix.postRotate(90F)
        } else if (orientation == 3) {
            matrix.postRotate(180F)
        } else if (orientation == 8) {
            matrix.postRotate(270F)
        }
        scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.width, scaledBitmap.height, matrix, true)
        return scaledBitmap
    }

    private fun openPhoto(views: View, imageFile: File) {
        val snackbar = Snackbar
            .make(views, "Image saved to gallery!", Snackbar.LENGTH_LONG)
            .setAction("OPEN") {
                val i = Intent(Intent.ACTION_VIEW)
                i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                val uri =
                    FileProvider.getUriForFile(ctx!!, ctx!!.resources.getString(R.string.package_name), imageFile)
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