package com.tim.feed.layout

import android.view.View
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.lifecycle.Observer
import com.tim.feed.R
import com.tim.feed.data.BusinessItem
import com.video.feed.data.TimData

class TitleLayout(val activity: ComponentActivity): IBaseFeedItemLayout(activity) {

    private var title: TextView? = null
    private var curObserver = Observer<BusinessItem> {
        title?.text = it.title
    }

    override fun onTimCreateView(type: Int, itemView: View) {
        super.onTimCreateView(type, itemView)
        title = itemView.findViewById(R.id.title)
    }

    override fun onTimBindView(position: Int, data: TimData) {
        super.onTimBindView(position, data)
        businessItem = mainViewModel.businessItemMap[data.id]?.apply {
            this@TitleLayout.title?.text = title
        }
    }

    override fun onTimSelected() {
        super.onTimSelected()
        mainViewModel.feedItems.value?.let {
            var selectIndex = -1
            for (index in 0 until it.size) {
                if (businessItem?.id == it[index].id) {
                    selectIndex = index
                    break
                }
            }
            if (selectIndex >= it.size - 3) {
                mainViewModel.getData()
            }
        }
        mainViewModel.curBusinessItemData.observe(activity, curObserver)
    }

    override fun onTimDeselected() {
        super.onTimDeselected()
        mainViewModel.curBusinessItemData.removeObserver(curObserver)
    }

}