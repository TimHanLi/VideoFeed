package com.tim.feed.layout

import android.util.Log
import android.view.View
import android.widget.SeekBar
import androidx.activity.ComponentActivity
import androidx.lifecycle.Observer
import com.tim.feed.R
import com.tim.videofeed.data.TimData

private const val TAG = "PlayProgressLayout"

class PlayProgressLayout(val activity: ComponentActivity): IBaseFeedItemLayout(activity) {

    private var progressSeekBar: SeekBar? = null
    private var durationObserver: Observer<Int>? = null
    private var progressObserver: Observer<Int>? = null

    override fun onTimCreateView(type: Int, itemView: View) {
        super.onTimCreateView(type, itemView)
        progressSeekBar = itemView.findViewById<SeekBar?>(R.id.progress_seek)?.apply {
            progress = 0
            max = mainViewModel.videoDuration.value ?: 100
        }
    }

    override fun onTimBindView(position: Int, data: TimData) {
        super.onTimBindView(position, data)
        this.position = position
        this.id = data.id
    }

    override fun onTimSelected() {
        super.onTimSelected()
        if (progressObserver == null) {
            progressObserver = Observer<Int> {
                progressSeekBar?.progress = it
            }
        }
        progressObserver?.let {
            mainViewModel.videoProgress.observe(act, it)
        }
        if (durationObserver == null) {
            durationObserver = Observer<Int> {
                progressSeekBar?.max = it
                Log.d(TAG, "duration = $it")
            }
        }
        durationObserver?.let {
            mainViewModel.videoDuration.observe(act, it)
        }
    }

    override fun onTimDeselected() {
        super.onTimDeselected()
        progressSeekBar?.progress = 0
        removeObserver()
    }

    private fun removeObserver() {
        durationObserver?.let {
            mainViewModel.videoDuration.removeObserver(it)
        }
        progressObserver?.let {
            mainViewModel.videoProgress.removeObserver(it)
        }
    }

    override fun onTimDestroyView() {
        removeObserver()
    }
}