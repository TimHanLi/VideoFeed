package com.tim.feed.layer

import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.activity.ComponentActivity
import com.tim.feed.R
import com.tim.feed.TYPE_VIDEO
import com.tim.feed.layout.PlayProgressLayout
import com.tim.feed.layout.TitleLayout
import com.tim.videofeed.layer.ITimLayer
import com.tim.videofeed.layout.ITimItemLayout

class PanelLayer(val act: ComponentActivity): ITimLayer {
    override fun onCreateTimView(parent: FrameLayout, type: Int): View? {
        if (TYPE_VIDEO == type) {
            val inflater =LayoutInflater.from(act).inflate(R.layout.layout_panel, parent, false)
            parent.addView(inflater)
            return inflater
        }
        return null
    }

    override fun getTimItemLayouts(onCreateView: View): MutableList<ITimItemLayout>? {
        return mutableListOf<ITimItemLayout>().apply {
            add(TitleLayout(act))
            add(PlayProgressLayout(act))
        }
    }
}