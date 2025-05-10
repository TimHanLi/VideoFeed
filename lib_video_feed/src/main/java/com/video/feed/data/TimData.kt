package com.video.feed.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * id: 条目的唯一
 * type: 条目类型，由使用方传进来
 */
@Parcelize
data class TimData(
    val id: String,
    val type: Int
) : Parcelable {

    override fun hashCode(): Int {
        return id.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (other is TimData) {
            return id == other.id && type == other.type
        }
        return false
    }
}

