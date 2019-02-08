package com.example.junemon.filteringimage.base

import android.content.Context
import android.view.View

/**
 * Created by ian on 07/02/19.
 */

interface BaseFragmentPresenter {
    fun onAttach(context: Context?)
    fun onCreateView(view: View)
}