package com.tim.videofeed.layer

import android.view.View
import android.widget.FrameLayout
import com.tim.videofeed.layout.ITimItemLayout

interface ITimLayer {
    fun onCreateTimView(parent: FrameLayout, type: Int): View?
    fun getTimItemLayouts(onCreateView: View): MutableList<ITimItemLayout>?
}
