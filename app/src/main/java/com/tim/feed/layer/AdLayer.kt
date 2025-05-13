package com.tim.feed.layer

import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.activity.ComponentActivity
import com.tim.feed.R
import com.tim.feed.TYPE_AD
import com.tim.feed.layout.AdLayout
import com.tim.videofeed.layer.ITimLayer
import com.tim.videofeed.layout.ITimItemLayout

class AdLayer(val act: ComponentActivity): ITimLayer {
    override fun onCreateTimView(parent: FrameLayout, type: Int): View? {
        if (type == TYPE_AD) {
            val inflate = LayoutInflater.from(act).inflate(R.layout.layout_ad, parent, false)
            parent.addView(inflate)
            return inflate
        }
        return null
    }

    override fun getTimItemLayouts(onCreateView: View): MutableList<ITimItemLayout>? {
        return mutableListOf<ITimItemLayout>().apply {
            add(AdLayout(act))
        }
    }
}