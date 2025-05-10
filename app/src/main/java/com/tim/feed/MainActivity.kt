package com.tim.feed

import android.os.Bundle
import android.widget.Button
import androidx.activity.ComponentActivity
import androidx.lifecycle.ViewModelProvider
import com.tim.feed.layer.AdLayer
import com.tim.feed.layer.PanelLayer
import com.tim.feed.layer.VideoPlayLayer
import com.video.feed.manager.TimManager

class MainActivity : ComponentActivity() {

    private val feedManager: TimManager by lazy(LazyThreadSafetyMode.NONE) {
        TimManager().apply {
            // 视频播放层
            addTimLayer(VideoPlayLayer(this@MainActivity))
            // 操作面板层
            addTimLayer(PanelLayer(this@MainActivity))
            // 广告层
            addTimLayer(AdLayer(this@MainActivity))
            setVp2(findViewById(R.id.vp2))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 创建feed流
        feedManager.onCreateLayer()

        // data-viewmodel
        val viewModel = ViewModelProvider(owner = this)[MainViewModel::class.java].apply {
            feedItems.observe(this@MainActivity) {
                feedManager.addData(it)
            }
        }
        viewModel.timManager = feedManager
        viewModel.getData()

        // 事件点击
        findViewById<Button>(R.id.insert).setOnClickListener {
            viewModel.insert()
        }
        findViewById<Button>(R.id.remove).setOnClickListener {
            viewModel.remove()
        }
        findViewById<Button>(R.id.changeCur).setOnClickListener {
            viewModel.changeCur()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        feedManager.onDestroy()
    }
}