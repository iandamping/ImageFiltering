package com.example.junemon.filteringimage.ui.fragment.filter

import com.zomato.photofilters.imageprocessors.Filter
/**
 * Created by ian on 07/02/19.
 */

interface FragmentFilterListener {
    fun onFilterSelected(filter: Filter)
}