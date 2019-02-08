package com.example.junemon.filteringimage.ui.fragment.edit

import android.content.Context
import android.view.View
import com.example.junemon.filteringimage.base.BaseFragmentPresenter

/**
 * Created by ian on 07/02/19.
 */

class EditImagePresenter(var mView: EditImageView) : BaseFragmentPresenter {
    private var ctx: Context? = null
    override fun onAttach(context: Context?) {
        this.ctx = context
    }

    override fun onCreateView(view: View) {
        mView.initView(view)
    }

}