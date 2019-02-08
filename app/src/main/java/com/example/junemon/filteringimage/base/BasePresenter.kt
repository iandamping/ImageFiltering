package com.example.junemon.filteringimage.base

import android.content.Context

/**
 * Created by ian on 07/02/19.
 */

interface BasePresenter {
    fun getContext(): Context?

    fun onCreate(context: Context)

    fun onStop()
}