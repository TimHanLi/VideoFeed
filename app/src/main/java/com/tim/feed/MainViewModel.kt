package com.tim.feed

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.tim.feed.data.BusinessItem
import com.video.feed.data.TimData
import com.video.feed.manager.TimManager
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

const val TYPE_VIDEO = 1
const val TYPE_AD = 2

class MainViewModel: ViewModel() {

    /**
     * 只有id和type
     */
    val feedItems = MutableLiveData<MutableList<TimData>>()

    /**
     * key- id
     * value- BusinessItem
     */
    val businessItemMap = ConcurrentHashMap<String, BusinessItem>()

    /**
     * 当前播放位置
     */
    var curPlayId: String? = null

    /**
     * 当前选中数据
     * 如果改变当前数据，使用MutableLiveData
     * 挂载的条目是FeedItemData，内部仅封装了id和type，所以数据改变无法使用recyclerview notify
     */
    var curBusinessItemData = MutableLiveData<BusinessItem>()

    var timManager: TimManager? = null

    val videoProgress = MutableLiveData<Int>()
    val videoDuration = MutableLiveData<Int>()

    fun getData() {
        val list = mutableListOf<TimData>().apply {
            feedItems.value?.let { old ->
                addAll(old)
            }
            createBusinessItemWithUuid(TYPE_VIDEO, "aaa", "aaa.mp4").let { item ->
                businessItemMap[item.id] = item
                add(TimData(item.id, TYPE_VIDEO))
            }
            createBusinessItemWithUuid(TYPE_VIDEO, "bbb", "bbb.mp4").let { item ->
                businessItemMap[item.id] = item
                add(TimData(item.id, TYPE_VIDEO))
            }
            createBusinessItemWithUuid(TYPE_VIDEO, "ccc", "ccc.mp4").let { item ->
                businessItemMap[item.id] = item
                add(TimData(item.id, TYPE_VIDEO))
            }
            createBusinessItemWithUuid(TYPE_VIDEO, "ddd", "ddd.mp4").let { item ->
                businessItemMap[item.id] = item
                add(TimData(item.id, TYPE_VIDEO))
            }
            createBusinessItemWithUuid(TYPE_AD, "ad", "ad").let { item ->
                businessItemMap[item.id] = item
                add(TimData(item.id, TYPE_AD))
            }
            createBusinessItemWithUuid(TYPE_VIDEO, "eee", "eee.mp4").let { item ->
                businessItemMap[item.id] = item
                add(TimData(item.id, TYPE_VIDEO))
            }
        }
        feedItems.value = list
    }

    /**
     * 插入
     */
    fun insert() {
        createBusinessItemWithUuid(TYPE_VIDEO, "eee-intert", "eee.mp4").let { item ->
            businessItemMap[item.id] = item
            val curPlayPosition = feedItems.value?.indexOfFirst {
                it.id == curPlayId
            } ?: -1
            val list = feedItems.value ?: mutableListOf()
            list.add(curPlayPosition + 1, TimData(item.id, TYPE_VIDEO))
            feedItems.value = list
        }
    }


    fun remove() {
        feedItems.value?.firstOrNull {
            it.id == curPlayId
        }?.let {
            feedItems.value?.remove(it)
            timManager?.removeItem(it)
        }
    }

    fun changeCur() {
        val businessItem = curBusinessItemData.value
        businessItem?.title = "changeTitle"
        curBusinessItemData.value = businessItem
    }

    /**
     * 根据feeditem获取businessitem
     */
    fun getBusinessItemByFeedItem(itemData: TimData) =  businessItemMap[itemData.id]

    private fun createBusinessItemWithUuid(type: Int, title: String, path: String) =
        BusinessItem("${UUID.randomUUID()}:$title", type, title, path)

}