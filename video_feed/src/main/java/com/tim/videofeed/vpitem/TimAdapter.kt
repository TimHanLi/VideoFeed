package com.tim.videofeed.vpitem

import android.util.SparseArray
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.tim.videofeed.data.TimData
import com.tim.videofeed.layer.ITimLayer
import com.tim.videofeed.manager.timLog
import com.tim.videofeed.manager.getVideoFeedCommonTag

/**
 * feed流的adapter
 */
internal class TimAdapter: RecyclerView.Adapter<TimViewHolder>() {
    private val tag by lazy(LazyThreadSafetyMode.NONE) {
        getVideoFeedCommonTag()
    }
    private val timLayers = SparseArray<ITimLayer>()

    private val diffCallBack = object : DiffUtil.ItemCallback<TimData>() {
        override fun areItemsTheSame(
            oldItem: TimData,
            newItem: TimData
        ): Boolean {
            val result = oldItem.id == newItem.id
            timLog(tag, "result = $result, old = ${oldItem.id}, new = ${newItem.id}")
            return result
        }

        override fun areContentsTheSame(
            oldItem: TimData,
            newItem: TimData
        ): Boolean {
            val equals = oldItem.equals(newItem)
            timLog(tag, "equals = $equals; oldItem = ${oldItem.id}; newItem = ${newItem.id}")
            return equals
        }
    }

    private val asyncDiffer = AsyncListDiffer(this, diffCallBack)

    private val timDataList: MutableList<TimData>
        get() = asyncDiffer.currentList

    /**
     * 去重添加数据
     */
    fun addTimData(timList: MutableList<TimData>) {
        val result = mutableListOf<TimData>().apply {
            addAll(timList)
        }
        timLog(tag, "size = ${result.size}; result = $result")
        asyncDiffer.submitList(result)
    }

    /**
     * 移除数据
     */
    fun removeTimItem(item: TimData, removeCallBack: (index: Int) -> Unit) {
        val currentList = asyncDiffer.currentList.toMutableList()
        val index = currentList.indexOfFirst { it.id == item.id }
        if (index != -1) {
            removeCallBack(index)
            currentList.removeAt(index)
            asyncDiffer.submitList(currentList)
        }
    }

    /**
     * 设置layer
     */
    fun setTimLayers(array: SparseArray<ITimLayer>) {
        timLayers.clear()
        for (index in 0 until array.size()) {
            timLayers.put(index, array[index])
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimViewHolder {
        timLog(tag, "onCreateViewHolder:")
        return TimViewHolder(
            TimItemView(parent.context).apply {
                layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
                setTimLayers(timLayers)
                onCreateTimView(viewType)
            }
        )
    }

    override fun onBindViewHolder(holder: TimViewHolder, position: Int) {
        timLog(tag, "onBindViewHolder : position: $position")
        holder.view.onBindTimView(position, timDataList[position])
    }

    override fun onViewRecycled(holder: TimViewHolder) {
        super.onViewRecycled(holder)
        holder.view.onDestroyTimView()
    }

    override fun getItemCount(): Int {
        return timDataList.size
    }

    override fun getItemViewType(position: Int): Int {
        return timDataList[position].type
    }

}