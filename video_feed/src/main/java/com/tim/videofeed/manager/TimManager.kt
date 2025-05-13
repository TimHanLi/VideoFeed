package com.tim.videofeed.manager

import android.os.SystemClock
import android.util.Log
import android.util.SparseArray
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import androidx.viewpager2.widget.ViewPager2.SCROLL_STATE_DRAGGING
import androidx.viewpager2.widget.ViewPager2.SCROLL_STATE_IDLE
import androidx.viewpager2.widget.ViewPager2.SCROLL_STATE_SETTLING
import com.tim.videofeed.data.TimData
import com.tim.videofeed.layer.ITimLayer
import com.tim.videofeed.vpitem.TimAdapter
import com.tim.videofeed.vpitem.TimItemView
import com.tim.videofeed.vpitem.TimViewHolder


const val VIDEO_TIM_TAG = "VideoTimTag:"

/**
 * 日志tag
 */
fun <T : Any> T.getVideoFeedCommonTag(): String = VIDEO_TIM_TAG + javaClass.simpleName

/**
 * 开始时间
 */
fun startTime(isDebug: Boolean = true): Long {
    return if (isDebug) {
        SystemClock.uptimeMillis()
    } else {
        0
    }
}

/**
 * 花费时间
 */
fun costTime(time: Long, isDebug: Boolean = true): Long {
    return if (isDebug) {
        SystemClock.uptimeMillis() - time
    } else {
        0
    }
}

fun timLog(tag: String, msg: String, isDebug: Boolean = true) {
    if (isDebug) {
        Log.d(tag, msg)
    }
}

/**
 * desc: feed功能入口
 */
class TimManager() {
    private var vp2: ViewPager2? = null
    private var timAdapter: TimAdapter? = null
    private val timLayerArray: SparseArray<ITimLayer> = SparseArray()
    private var timLayerIndex = -1
    private var curPosition: Int = -1

    /**
     * 添加
     */
    fun addTimLayer(timLayer: ITimLayer) {
        timLayerIndex++
        timLayerArray.put(timLayerIndex, timLayer)
    }

    /**
     * viewpager2
     */
    fun setVp2(vp2: ViewPager2) {
        this.vp2 = vp2
        initVp()
    }

    /**
     * 创建layer
     */
    fun onCreateLayer() {
        if (vpIsNull()) {
            return
        }
        timAdapter?.setTimLayers(timLayerArray)
    }

    /**
     * 设置数据
     */
    fun addData(list: MutableList<TimData>) {
        timAdapter?.addTimData(list)
    }

    /**
     * 移除数据
     */
    fun removeItem(item: TimData) {
        timAdapter?.removeTimItem(item) { index ->
            if (index == curPosition) {
                getTimItemView(curPosition)?.let {
                    it.onTimStartSlidingOut()
                    it.onTimDeselected()
                }
                getTimItemView(curPosition + 1)?.let {
                    it.onTimStartSlidingIn()
                    it.onTimSelect()
                }
            }
        }
    }

    /**
     * 是否允许用户滑动
     */
    fun setUserInputEnable(enable: Boolean) {
        vp2?.isUserInputEnabled = enable
    }

    /**
     * 设置位置
     * noScroll: true，立刻选中
     */
    fun setCurrentItem(position: Int, noScroll: Boolean = false) {
        if (position < 0 || position >= (timAdapter?.itemCount ?: 0)) {
            return
        }
        if (position == curPosition) {
            return
        }
        if (noScroll) {
            vp2?.setCurrentItem(position, false)
            return
        }
        if (position == curPosition + 1) {
            vp2?.setCurrentItem(position, true)
        } else {
            vp2?.setCurrentItem(position, false)
        }
    }

    /**
     * 销毁
     */
    fun onDestroy() {
        if (vpIsNull()) {
            return
        }
        getTimItemView(curPosition)?.apply {
            onTimDeselected()
            onDestroyTimView()
        }
    }

    private fun initVp() {
        vp2?.let { vp2 ->
            vp2.registerOnPageChangeCallback(object : OnPageChangeCallback() {
                private var scrollPos = -1
                private var scrollState = SCROLL_STATE_IDLE
                private var slidingIn = false
                private var slidingOut = false
                override fun onPageScrollStateChanged(state: Int) {
                    super.onPageScrollStateChanged(state)
                    scrollState = state
                    when(scrollState) {
                        SCROLL_STATE_IDLE -> {
                            val currentItem = vp2.currentItem
                            if (currentItem != curPosition) {
                                getTimItemView(curPosition)?.onTimDeselected()
                            }
                            if (currentItem != scrollPos) {
                                getTimItemView(scrollPos)?.onTimDeselected()
                            }
                            getTimItemView(currentItem)?.onTimSelect()
                            curPosition = currentItem
                            scrollPos = -1
                            slidingOut = false
                            slidingIn = false
                        }
                    }
                }

                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    if (scrollPos == -1 && curPosition != position) {
                        slidingOut = false
                        slidingIn = false
                        getTimItemView(curPosition)?.onTimDeselected()
                    }
                    if (curPosition == -1) {
                        getTimItemView(position)?.onTimSelect()
                    }
                    if (scrollState == SCROLL_STATE_IDLE) {
                        curPosition = position
                    }
                }

                override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int
                ) {
                    super.onPageScrolled(position, positionOffset, positionOffsetPixels)
                    if (scrollState == SCROLL_STATE_DRAGGING || (positionOffset > 0 && scrollState == SCROLL_STATE_SETTLING)) {
                        if (positionOffset >= 0f || downTwoItem(position) || upTwoItem(position)) {
                            if (scrollPos == -1) {
                                scrollPos = curPosition
                            }
                            if (position >= scrollPos) {
                                if (position > scrollPos) {
                                    getTimItemView(scrollPos)?.onTimDeselected()
                                    scrollPos = position
                                    slidingOut = false
                                    slidingIn = false
                                }
                                if (!slidingOut) {
                                    slidingOut = true
                                    getTimItemView(position)?.onTimStartSlidingOut()
                                }
                                if (!slidingIn) {
                                    slidingIn = true
                                    getTimItemView(position + 1)?.onTimStartSlidingIn()
                                }
                            } else if (position <= scrollPos - 1) {
                                if (position < scrollPos - 1) {
                                    getTimItemView(scrollPos)?.onTimDeselected()
                                    scrollPos = position + 1
                                    slidingOut = false
                                    slidingIn = false
                                }
                                if (!slidingOut) {
                                    slidingOut = true
                                    getTimItemView(position + 1)?.onTimStartSlidingOut()
                                }
                                if (!slidingIn) {
                                    slidingIn = true
                                    getTimItemView(position)?.onTimStartSlidingIn()
                                }
                            }
                        }
                    } else {
                        if (positionOffset == 0f) {
                            slidingOut = false
                            slidingIn = false
                            if (scrollPos != position) {
                                getTimItemView(scrollPos)?.onTimDeselected()
                            }
                            getTimItemView(position)?.onTimSelect()
                            curPosition = position
                            scrollPos = -1
                        }
                    }
                }

                private fun upTwoItem(position: Int) =
                    (position + 2 < scrollPos && scrollPos != -1)

                private fun downTwoItem(position: Int) =
                    (position > scrollPos && scrollPos != -1)
            })
            timAdapter = TimAdapter()
            vp2.offscreenPageLimit = 1
            vp2.adapter = timAdapter
        }
    }

    private fun vpIsNull(): Boolean {
        return vp2 == null
    }

    private fun getTimItemView(position: Int): TimItemView? {
        return vp2?.getChildAt(0)?.let { ry ->
            var result: TimItemView? = null
            if (ry is RecyclerView) {
                val viewHolder = ry.findViewHolderForAdapterPosition(position)
                if (viewHolder is TimViewHolder) {
                    result = viewHolder.view
                }
            }
            result
        }
    }
}

