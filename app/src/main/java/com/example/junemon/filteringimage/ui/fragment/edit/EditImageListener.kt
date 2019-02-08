package com.example.junemon.filteringimage.ui.fragment.edit

import com.example.junemon.filteringimage.base.BaseFragmentView

/**
 * Created by ian on 07/02/19.
 */

interface EditImageListener : BaseFragmentView {
    fun onBrightnessChanged(brightness: Int)

    fun onSaturationChanged(saturation: Float)

    fun onContrastChanged(contrast: Float)

    fun onEditStarted()

    fun onEditCompleted()
}