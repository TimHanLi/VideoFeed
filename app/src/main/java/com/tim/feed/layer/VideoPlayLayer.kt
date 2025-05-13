package com.tim.feed.layer

import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.activity.ComponentActivity
import com.tim.feed.R
import com.tim.feed.TYPE_VIDEO
import com.tim.feed.layout.VideoPlayLayout
import com.tim.videofeed.layer.ITimLayer
import com.tim.videofeed.layout.ITimItemLayout

class VideoPlayLayer(val act: ComponentActivity): ITimLayer {
    override fun onCreateTimView(parent: FrameLayout, type: Int): View? {
        if (TYPE_VIDEO == type) {
            val inflater = LayoutInflater.from(act).inflate(R.layout.layout_video, parent, false)
            parent.addView(inflater)
            return inflater
        }
        return null
    }

    override fun getTimItemLayouts(onCreateView: View): MutableList<ITimItemLayout> {
        return mutableListOf<ITimItemLayout>().apply {
            add(VideoPlayLayout(act))
        }
    }
}