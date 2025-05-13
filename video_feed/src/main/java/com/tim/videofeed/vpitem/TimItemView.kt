package com.tim.videofeed.vpitem

import android.content.Context
import android.util.AttributeSet
import android.util.SparseArray
import android.widget.FrameLayout
import com.tim.videofeed.data.TimData
import com.tim.videofeed.layer.ITimLayer
import com.tim.videofeed.layout.ITimItemLayout
import com.tim.videofeed.manager.timLog
import com.tim.videofeed.manager.costTime
import com.tim.videofeed.manager.startTime
import com.tim.videofeed.manager.getVideoFeedCommonTag

inline fun <E> SparseArray<E>.forEach(action: (Int, E) -> Unit) {
    for (i in 0 until size()) {
        action(keyAt(i), valueAt(i))
    }
}

inline fun <E> SparseArray<E>.putAll(other: SparseArray<E>): Unit = other.forEach(::put)

/**
 * itemview
 */
internal class TimItemView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val tag by lazy(LazyThreadSafetyMode.NONE) {
        getVideoFeedCommonTag()
    }
    private val isDebug  by lazy(LazyThreadSafetyMode.NONE) {
        true
    }
    private var viewType: Int = -1
    private var position: Int = -1
    private var data: TimData? = null
    private var timLayers = SparseArray<ITimLayer>()
    private var timLayouts = mutableListOf<ITimItemLayout>()
    private var isSelected = false

    fun setTimLayers(layers: SparseArray<ITimLayer>) {
        timLayers.clear()
        timLayers.putAll(layers)
    }

    fun onCreateTimView(viewType: Int) {
        this.viewType = viewType
        val startTime = startTime(isDebug)
        notifyCreateLayerView(viewType)
        val createLayerTime = costTime(startTime, isDebug)
        val startLayoutTime = startTime(isDebug)
        notifyCreateLayout(viewType)
        val createLayoutTime = costTime(startLayoutTime, isDebug)
        timLog(tag, "onCreateView: createLayerTime = $createLayerTime; createLayoutTime = $createLayoutTime")
    }

    private fun notifyCreateLayerView(viewType: Int) {
        timLayers.forEach { _, value ->
            value.apply {
                onCreateTimView(this@TimItemView, viewType)?.let { v ->
                    getTimItemLayouts(v)?.takeIf { it.size > 0 }?.let {
                        timLayouts.addAll(it)
                    }
                }
            }
        }
    }

    private fun notifyCreateLayout(viewType: Int) {
        timLayouts.forEach {
            it.onTimCreateView(viewType, this)
        }
    }

    fun onBindTimView(position: Int, data: TimData) {
        this.position = position
        this.data = data
        val startTime = startTime(isDebug)
        timLayouts.forEach {
            it.onTimBindView(position, data)
        }
        val costTime = costTime(startTime, isDebug)
        timLog(tag, "onBindView: id = ${data.id}; costTime = $costTime")
    }

    /**
     * 滑入
     */
    fun onTimSelect() {
        if (!isSelected) {
            isSelected = true
            val startTime = startTime(isDebug)
            timLayouts.forEach {
                val startTime1 = startTime(isDebug)
                it.onTimSelected()
                timLog(tag, "onSelected:id = ${data?.id}; position = $position; layout = ${it.javaClass.canonicalName}; costTime = ${costTime(startTime1, isDebug)}")
            }
            val costTime = costTime(startTime, isDebug)
            timLog(tag, "onSelected:id = ${data?.id}; position = $position; costTime = $costTime")
        }
    }

    /**
     * 滑出
     */
    fun onTimDeselected() {
        if (isSelected) {
            isSelected = false
            val startTime = startTime(isDebug)
            timLayouts.forEach {
                val startTime1 = startTime(isDebug)
                it.onTimDeselected()
                timLog(tag, "onDeselected:id = ${data?.id}; position = $position; layout = ${it.javaClass.canonicalName}; costTime = ${costTime(startTime1, isDebug)}")
            }
            val costTime = costTime(startTime, isDebug)
            timLog(tag, "onDeselected:id = ${data?.id}; position = $position; costTime = $costTime")
        }
    }

    /**
     * 开始滑入
     */
    fun onTimStartSlidingIn() {
        val startTime = startTime(isDebug)
        timLayouts.forEach {
            it.startTimSlidingIn()
        }
        val costTime = costTime(startTime, isDebug)
        timLog(tag, "onStartSlidingIn:id = ${data?.id}; position = $position; costTime = $costTime")
    }

    /**
     * 开始滑出
     */
    fun onTimStartSlidingOut() {
        val startTime = startTime(isDebug)
        timLayouts.forEach {
            it.startTimSlidingOut()
        }
        val costTime = costTime(startTime, isDebug)
        timLog(tag, "startSlidingOut:id = ${data?.id}; position = $position; costTime = $costTime")
    }

    /**
     * 回收view
     */
    fun onDestroyTimView() {
        val startTime = startTime(isDebug)
        timLayouts.forEach {
            it.onTimDestroyView()
        }
        val costTime = costTime(startTime, isDebug)
        timLog(tag, "onDestroyView: costTime = $costTime")
    }
}