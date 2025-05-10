package com.video.feed.layout

import android.view.View
import com.video.feed.data.TimData

/**
 * 条目级别Layout
 */
interface ITimItemLayout {
    fun onTimCreateView(type: Int, itemView: View)
    fun onTimBindView(position: Int, data: TimData)
    fun startTimSlidingIn()
    fun onTimSelected()
    fun startTimSlidingOut()
    fun onTimDeselected()
    fun onTimDestroyView()
}