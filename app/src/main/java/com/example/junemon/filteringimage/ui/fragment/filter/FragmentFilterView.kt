package com.example.junemon.filteringimage.ui.fragment.filter

import com.example.junemon.filteringimage.base.BaseFragmentView
import com.zomato.photofilters.utils.ThumbnailItem

/**
 * Created by ian on 07/02/19.
 */

interface FragmentFilterView : BaseFragmentView {
    fun getListFilter(allData: List<ThumbnailItem>?)
}