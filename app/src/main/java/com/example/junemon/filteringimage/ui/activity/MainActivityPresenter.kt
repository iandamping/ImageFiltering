package com.example.junemon.filteringimage.ui.activity

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.support.v4.app.FragmentActivity
import android.view.View
import android.widget.Toast
import com.example.junemon.filteringimage.MainApplication.Companion.RequestSelectGalleryImage
import com.example.junemon.filteringimage.base.BasePresenter
import com.example.junemon.filteringimage.helper.ImageUtils
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
/**
 * Created by ian on 07/02/19.
 */

class MainActivityPresenter(var target: FragmentActivity, var mView: MainActivityView) : BasePresenter {
    lateinit var ctx: Context
    private lateinit var utils: ImageUtils
    override fun getContext(): Context? {
        return ctx
    }

    override fun onCreate(context: Context) {
        ctx = context
        utils = ImageUtils(ctx)
    }

    override fun onStop() {
    }

    fun getAllPermisions() {
        Dexter.withActivity(target).withPermissions(
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        ).withListener(object : MultiplePermissionsListener {
            override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                if (report?.areAllPermissionsGranted()!!) {
                    mView.allPermisionGranted(report.areAllPermissionsGranted())
                }
            }

            override fun onPermissionRationaleShouldBeShown(
                permissions: MutableList<PermissionRequest>?,
                token: PermissionToken?
            ) {
            }

        }).check()
    }

    fun openImageFromGallery(status: Boolean?) {
        if (status != null) {
            if (status) {
                val i = Intent(Intent.ACTION_PICK)
                i.type = "image/*"
                target.startActivityForResult(i, RequestSelectGalleryImage)
            } else {
                Toast.makeText(ctx, "Permissions are not granted!", Toast.LENGTH_SHORT).show();
            }
        }

    }

    fun saveImageToGallery(views: View, status: Boolean?, bitmap: Bitmap?) {
        if (status != null) {
            if (status) {
                utils.saveImage(views, bitmap, "stoya")
            } else {
                Toast.makeText(ctx, "Permissions are not granted!", Toast.LENGTH_SHORT).show();
            }
        }

    }

}


//fun pickPhoto() {
//    val i = Intent(Intent.ACTION_GET_CONTENT)
//    i.type = "image/jpeg"
//    i.putExtra(Intent.EXTRA_LOCAL_ONLY, true)
//    startActivityForResult(Intent.createChooser(i, "Complete action using"), RequestPhotoPicker)
//}
//
//private fun openImage(path: String?) {
//    val intent = Intent(Intent.ACTION_VIEW)
//    intent.setDataAndType(Uri.parse(path), "image/*")
//    startActivity(intent)
//}
