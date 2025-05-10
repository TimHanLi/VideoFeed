package com.video.feed.layer

import android.view.View
import android.widget.FrameLayout
import com.video.feed.layout.ITimItemLayout

interface ITimLayer {
    fun onCreateTimView(parent: FrameLayout, type: Int): View?
    fun getTimItemLayouts(onCreateView: View): MutableList<ITimItemLayout>?
}
