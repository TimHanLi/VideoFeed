package com.tim.feed.util

import android.os.Handler
import android.os.Looper
import android.os.Message

private const val MSG_WHAT = 1
private const val MSG_INTER = 200L

abstract class LoopHandler(looper: Looper): Handler(looper) {
    private var isStart = false
    fun start() {
        isStart = true
        sendEmptyMessageDelayed(MSG_WHAT, MSG_INTER)
    }

    override fun handleMessage(msg: Message) {
        super.handleMessage(msg)
        if (isStart && msg.what == MSG_WHAT) {
            loop()
            sendEmptyMessageDelayed(MSG_WHAT, MSG_INTER)
        }
    }

    abstract fun loop()

    fun stop() {
        isStart = false
        removeCallbacksAndMessages(null)
    }
}