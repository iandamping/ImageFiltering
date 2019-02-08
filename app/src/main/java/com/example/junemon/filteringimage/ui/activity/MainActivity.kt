package com.example.junemon.filteringimage.ui.activity

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.example.junemon.filteringimage.MainApplication
import com.example.junemon.filteringimage.MainApplication.Companion.IMAGE_NAME
import com.example.junemon.filteringimage.MainApplication.Companion.RequestOpenCamera
import com.example.junemon.filteringimage.MainApplication.Companion.RequestSelectGalleryImage
import com.example.junemon.filteringimage.MainApplication.Companion.maxHeight
import com.example.junemon.filteringimage.MainApplication.Companion.maxWidth
import com.example.junemon.filteringimage.MainApplication.Companion.nonVoidCustomMediaScannerConnection
import com.example.junemon.filteringimage.helper.ImageUtils
import com.example.junemon.filteringimage.ui.adapter.ViewPagerAdapter
import com.example.junemon.filteringimage.ui.fragment.edit.EditImageFragment
import com.example.junemon.filteringimage.ui.fragment.edit.EditImageListener
import com.example.junemon.filteringimage.ui.fragment.filter.FragmentFilterList
import com.example.junemon.filteringimage.ui.fragment.filter.FragmentFilterListener
import com.zomato.photofilters.imageprocessors.Filter
import com.zomato.photofilters.imageprocessors.subfilters.BrightnessSubFilter
import com.zomato.photofilters.imageprocessors.subfilters.ContrastSubFilter
import com.zomato.photofilters.imageprocessors.subfilters.SaturationSubfilter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*


/**
 * Created by ian on 07/02/19.
 */

class MainActivity : AppCompatActivity(), MainActivityView, FragmentFilterListener, EditImageListener {
    companion object {
        init {
            System.loadLibrary("NativeImageProcessor")
        }
    }


    override fun allPermisionGranted(status: Boolean) {
        stat = status
    }

    private lateinit var presenter: MainActivityPresenter
    private var originalImage: Bitmap? = null
    private var filteredImage: Bitmap? = null
    private var finalImage: Bitmap? = null

    private var filtersListFragment: FragmentFilterList? = null
    private var editImageFragment: EditImageFragment? = null

    private var brightnessFinal = 0
    private var saturationFinal = 1.0f
    private var contrastFinal = 1.0f
    private var stat: Boolean? = null
    private lateinit var BitmapUtils: ImageUtils

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.example.junemon.filteringimage.R.layout.activity_main)
        presenter = MainActivityPresenter(this, this)
        presenter.onCreate(this)
        presenter.getAllPermisions()
        BitmapUtils = ImageUtils(this)

        loadImage()
        setupViewPager(viewpager)
        tabs.setupWithViewPager(viewpager)

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(com.example.junemon.filteringimage.R.menu.images_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            com.example.junemon.filteringimage.R.id.action_open -> {
                presenter.openImageFromGallery(stat)
                true
            }

            com.example.junemon.filteringimage.R.id.action_save -> {
                presenter.saveImageToGallery(coordinator_layout, stat, finalImage)
                true
            }
            com.example.junemon.filteringimage.R.id.action_camera -> {
                invokeCamera()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setupViewPager(vp: ViewPager) {
        val vpAdapter = ViewPagerAdapter(supportFragmentManager)
        filtersListFragment = FragmentFilterList()
        editImageFragment = EditImageFragment()

        filtersListFragment?.setListener(this)
        editImageFragment?.setListener(this)

        vpAdapter.addFragment(filtersListFragment, getString(com.example.junemon.filteringimage.R.string.tab_filters))
        vpAdapter.addFragment(editImageFragment, getString(com.example.junemon.filteringimage.R.string.tab_edit))
        vp.adapter = vpAdapter
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == RequestSelectGalleryImage) {
                if (data != null) {
                    val bitmap = BitmapUtils.getBitmapFromGallery(data.data!!, 800, 800)
                    clearBitmapMemory()

                    originalImage = bitmap?.copy(Bitmap.Config.ARGB_8888, true)
                    filteredImage = originalImage?.copy(Bitmap.Config.ARGB_8888, true)
                    finalImage = originalImage?.copy(Bitmap.Config.ARGB_8888, true)
                    ivImagePreview.setImageBitmap(originalImage)
                    bitmap?.recycle()

                    filtersListFragment?.prepareThumbnail(originalImage)
                }
            } else if (requestCode == RequestOpenCamera) {
                val bitmap = BitmapUtils.decodeSampledBitmapFromFile(
                    nonVoidCustomMediaScannerConnection(
                        this,
                        MainApplication.saveCaptureImagePath
                    ), reqWidth = maxWidth, reqHeight = maxHeight
                )
                clearBitmapMemory()

                originalImage = bitmap.copy(Bitmap.Config.ARGB_8888, true)
                filteredImage = originalImage?.copy(Bitmap.Config.ARGB_8888, true)
                finalImage = originalImage?.copy(Bitmap.Config.ARGB_8888, true)
                ivImagePreview.setImageBitmap(originalImage)
                bitmap.recycle()

                filtersListFragment?.prepareThumbnail(originalImage)
            }
        }

    }


    override fun onFilterSelected(filter: Filter) {
        resetControls()
        filteredImage = originalImage?.copy(Bitmap.Config.ARGB_8888, true)
        finalImage = filteredImage?.copy(Bitmap.Config.ARGB_8888, true)
        ivImagePreview.setImageBitmap(filter.processFilter(finalImage))

    }

    override fun initView(view: View) {
    }

    override fun onBrightnessChanged(brightness: Int) {
        brightnessFinal = brightness
        val myFilter: Filter = Filter()
        myFilter.addSubFilter(BrightnessSubFilter(brightness));
        ivImagePreview.setImageBitmap(myFilter.processFilter(finalImage?.copy(Bitmap.Config.ARGB_8888, true)))
    }

    override fun onSaturationChanged(saturation: Float) {
        saturationFinal = saturation
        val myFilter = Filter()
        myFilter.addSubFilter(SaturationSubfilter(saturation))
        ivImagePreview.setImageBitmap(myFilter.processFilter(finalImage?.copy(Bitmap.Config.ARGB_8888, true)))
    }

    override fun onContrastChanged(contrast: Float) {
        contrastFinal = contrast
        val myFilter = Filter()
        myFilter.addSubFilter(ContrastSubFilter(contrast))
        ivImagePreview.setImageBitmap(myFilter.processFilter(finalImage?.copy(Bitmap.Config.ARGB_8888, true)))
    }

    override fun onEditStarted() {
    }

    override fun onEditCompleted() {
        val bitmap = filteredImage?.copy(Bitmap.Config.ARGB_8888, true)
        val myFilter = Filter()
        myFilter.addSubFilter(BrightnessSubFilter(brightnessFinal))
        myFilter.addSubFilter(ContrastSubFilter(contrastFinal))
        myFilter.addSubFilter(SaturationSubfilter(saturationFinal))
        finalImage = myFilter.processFilter(bitmap)
    }

    // load the default image from assets on app launch
    private fun loadImage() {
        originalImage = BitmapUtils.getBitmapFromAssets(IMAGE_NAME, 300, 300)
        filteredImage = originalImage?.copy(Bitmap.Config.ARGB_8888, true)
        finalImage = originalImage?.copy(Bitmap.Config.ARGB_8888, true)
        ivImagePreview.setImageBitmap(originalImage)
    }

    //reset all contrast,brightness and saturation
    private fun resetControls() {
        if (editImageFragment != null) {
            editImageFragment?.resetControls()
        }
        brightnessFinal = 0
        saturationFinal = 1.0f
        contrastFinal = 1.0f
    }

    //clear bitmap memory
    private fun clearBitmapMemory() {
        originalImage?.recycle()
        filteredImage?.recycle()
        finalImage?.recycle()
    }

    private fun invokeCamera() {
        presenter.openCamera(stat, BitmapUtils.createImageFileFromPhoto())
    }

}
