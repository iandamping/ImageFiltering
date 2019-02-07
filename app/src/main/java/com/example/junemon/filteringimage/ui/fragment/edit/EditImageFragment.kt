package com.example.junemon.filteringimage.ui.fragment.edit

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import com.example.junemon.filteringimage.R
import kotlinx.android.synthetic.main.fragment_edit_image.view.*

/**
 * Created by ian on 07/02/19.
 */

class EditImageFragment : Fragment(), EditImageView, SeekBar.OnSeekBarChangeListener {
    private lateinit var presenter: EditImagePresenter
    private lateinit var actualView: View
    private var ctx: Context? = null
    private lateinit var listener: EditImageListener

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        this.ctx = context
        presenter = EditImagePresenter(this)
        presenter.onAttach(ctx)
    }

    fun setListener(editListener: EditImageListener) {
        this.listener = editListener
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val views: View =
            inflater.inflate(R.layout.fragment_edit_image, container, false)
        actualView = views
        presenter.onCreateView(views)
        return views
    }

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        if (listener != null) {
            if (seekBar?.id == R.id.sbBrightness) {
                listener.onBrightnessChanged(progress - 100)
            }
            if (seekBar?.id == R.id.sbCntrast) {
                val tmpData = progress.plus(10)
                val floatVal = .10f * tmpData
                listener.onContrastChanged(floatVal)

            }
            if (seekBar?.id == R.id.sbSaturation) {
                val floatVal = .10f * progress
                listener.onContrastChanged(floatVal)
            }
        }
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {
        if (listener != null) {
            listener.onEditStarted()
        }
    }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {
        if (listener != null) {
            listener.onEditCompleted()
        }
    }

    override fun initView(view: View) {
        view.sbBrightness.max = 200
        view.sbBrightness.progress = 100

        view.sbCntrast.max = 200
        view.sbCntrast.progress = 100

        view.sbSaturation.max = 200
        view.sbSaturation.progress = 100

        view.sbSaturation.setOnSeekBarChangeListener(this)
        view.sbBrightness.setOnSeekBarChangeListener(this)
        view.sbCntrast.setOnSeekBarChangeListener(this)
    }

    fun resetControls() {
        actualView.sbBrightness.setProgress(100)
        actualView.sbCntrast.setProgress(0)
        actualView.sbSaturation.setProgress(10)
    }
}