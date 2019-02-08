package com.example.junemon.filteringimage.ui.fragment.filter

import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.junemon.filteringimage.R
import com.example.junemon.filteringimage.helper.SpacesItemDecoration
import com.example.junemon.filteringimage.ui.adapter.ThumbnailsAdapter
import com.zomato.photofilters.utils.ThumbnailItem
import kotlinx.android.synthetic.main.fragment_filter_list.view.*

/**
 * Created by ian on 07/02/19.
 */


class FragmentFilterList : Fragment(), FragmentFilterView {
    private lateinit var actualView: View
    private var ctx: Context? = null
    private lateinit var presenter: FragmentFilterPresenter
    private lateinit var listener: FragmentFilterListener

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        this.ctx = context
        presenter = FragmentFilterPresenter(this)
        presenter.onAttach(ctx)
        prepareThumbnail(null)
    }

    fun setListener(listenerList: FragmentFilterListener) {
        this.listener = listenerList
    }

    override fun getListFilter(allData: List<ThumbnailItem>?) {
        val space = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, 8F,
            getResources().getDisplayMetrics()
        )
        if (actualView != null) {
            actualView.rvFragmentFilterList.layoutManager =
                LinearLayoutManager(ctx, LinearLayoutManager.HORIZONTAL, false)
            actualView.rvFragmentFilterList.itemAnimator = DefaultItemAnimator()
            actualView.rvFragmentFilterList.addItemDecoration(SpacesItemDecoration(space.toInt()))
            actualView.rvFragmentFilterList.adapter = ThumbnailsAdapter(ctx, allData!!) {
                listener.onFilterSelected(it.filter)
            }
        }
    }

    fun prepareThumbnail(bitmap: Bitmap?) {
        presenter.prepareThumbnail(bitmap)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val views: View = inflater.inflate(R.layout.fragment_filter_list, container, false)
        actualView = views
        return views
    }

    override fun initView(view: View) {
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.onDestroy()
    }


}
