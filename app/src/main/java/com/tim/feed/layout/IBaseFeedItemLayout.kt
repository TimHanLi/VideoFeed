package com.tim.feed.layout

import android.view.View
import androidx.activity.ComponentActivity
import androidx.lifecycle.ViewModelProvider
import com.tim.feed.MainViewModel
import com.tim.feed.data.BusinessItem
import com.video.feed.data.TimData
import com.video.feed.layout.ITimItemLayout

open class IBaseFeedItemLayout(val act: ComponentActivity): ITimItemLayout {
    protected var type: Int = 0
    protected var position: Int = -1
    protected var id: String? = null
    protected var isSelected = false
    protected var itemData: TimData? = null
    protected var businessItem: BusinessItem? = null

    protected val mainViewModel by lazy(LazyThreadSafetyMode.NONE) {
        ViewModelProvider(act)[MainViewModel::class.java]
    }

    override fun onTimCreateView(type: Int, itemView: View) {
        this.type = type
    }

    override fun onTimBindView(position: Int, data: TimData) {
        this.position = position
        this.itemData = data
        this.id = data.id
        this.businessItem = mainViewModel.getBusinessItemByFeedItem(data)
    }

    override fun startTimSlidingIn() {
    }

    override fun onTimSelected() {
        isSelected = true
        mainViewModel.curPlayId = id
        mainViewModel.curBusinessItemData.value = businessItem
    }

    override fun startTimSlidingOut() {
    }

    override fun onTimDeselected() {
        isSelected = false
    }

    override fun onTimDestroyView() {
    }
}