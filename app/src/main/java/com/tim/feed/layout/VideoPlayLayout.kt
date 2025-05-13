package com.tim.feed.layout

import android.content.res.AssetFileDescriptor
import android.graphics.SurfaceTexture
import android.media.MediaPlayer
import android.os.Looper
import android.view.Surface
import android.view.TextureView
import android.view.TextureView.SurfaceTextureListener
import android.view.View
import android.view.ViewGroup
import androidx.activity.ComponentActivity
import com.tim.feed.R
import com.tim.feed.util.LoopHandler
import com.tim.videofeed.data.TimData

private const val TAG = "VideoPlayLayout"

class VideoPlayLayout(val activity: ComponentActivity) : IBaseFeedItemLayout(activity), SurfaceTextureListener {

    private var textureView: TextureView? = null
    private var surfaceTexture: SurfaceTexture? = null
    private var mediaPlayer: MediaPlayer? = null

    private val loopHandler by lazy(LazyThreadSafetyMode.NONE) {
        object : LoopHandler(Looper.getMainLooper()) {
            override fun loop() {
                mediaPlayer?.currentPosition?.takeIf { isSelected }?.let {
                    mainViewModel.videoProgress.value = it
                }
            }
        }
    }

    override fun onTimCreateView(type: Int, itemView: View) {
        super.onTimCreateView(type, itemView)
        textureView = itemView.findViewById<TextureView?>(R.id.video_tv).apply {
            surfaceTextureListener = this@VideoPlayLayout
        }
    }

    override fun onTimBindView(position: Int, data: TimData) {
        super.onTimBindView(position, data)
        businessItem = mainViewModel.businessItemMap.get(data.id)
    }

    override fun startTimSlidingIn() {
        prepareAsyncMedia(businessItem?.path)
    }

    override fun onTimSelected() {
        super.onTimSelected()
        isSelected = true
        mainViewModel.videoDuration.value = mediaPlayer?.duration ?: 0
        prepareAsyncMedia(businessItem?.path)
    }

    private fun prepareAsyncMedia(path: String?) {
        if (path == null) {
            return
        }
        if (surfaceTexture == null) {
            return
        }
        if (mediaPlayer != null && isSelected) {
            startPlay()
            return
        }
        kotlin.runCatching {
            mediaPlayer = MediaPlayer().apply {
                setOnPreparedListener {
                    setTextsureViewParams(it)
                    if (isSelected) {
                        mainViewModel.videoDuration.value = it.duration
                    }
                    if (isSelected) {
                        startPlay()
                    }
                }
                setOnErrorListener { mp, what, extra ->
                    true
                }
                setOnCompletionListener {
                    releaseMedia()
                }
                val afd: AssetFileDescriptor = act.assets.openFd(path)
                setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
                setSurface(Surface(surfaceTexture))
                prepareAsync()
            }
        }
    }

    private fun setTextsureViewParams(it: MediaPlayer) {
        textureView?.apply {
            val videoWidth = it.videoWidth
            val videoHeight = it.videoHeight
            val viewWidth = width
            val viewHeight = height
            // 计算视频的宽高比
            val videoAspectRatio = videoWidth.toFloat() / videoHeight.toFloat()
            val viewAspectRatio = viewWidth.toFloat() / viewHeight.toFloat()

            val lp = layoutParams as ViewGroup.LayoutParams
            if (videoAspectRatio > viewAspectRatio) {
                // 视频更宽，调整高度
                lp.height = (viewWidth / videoAspectRatio).toInt()
            } else {
                // 视频更高，调整宽度
                lp.width = (viewHeight * videoAspectRatio).toInt()
            }
            val pageWidth = act.resources.displayMetrics.widthPixels
            val pageHeight = act.resources.displayMetrics.heightPixels
            if (lp.width == -1 || lp.width == -2) {
                lp.width = pageWidth
            }
            if (lp.height == -1 || lp.height == -2) {
                lp.height == pageHeight
            }
            val scaleW = lp.width * 1.0f / pageWidth
            val scaleH = lp.height * 1.0f / pageHeight
            if (scaleW > scaleH) {
                lp.width = pageWidth
                val newHeight = lp.height / scaleW
                lp.height = newHeight.toInt()
            } else {
                lp.height = pageHeight
                val newWidth = lp.width / scaleH
                lp.width = newWidth.toInt()
            }
            layoutParams = lp
        }
    }

    private fun startPlay() {
        loopHandler.start()
        mediaPlayer?.start()
    }

    override fun onTimDeselected() {
        super.onTimDeselected()
        isSelected = false
        releaseMedia()
    }

    override fun onTimDestroyView() {
        isSelected = false
        releaseMedia()
    }

    override fun onSurfaceTextureAvailable(surface: SurfaceTexture, width: Int, height: Int) {
        surfaceTexture = surface
        prepareAsyncMedia(businessItem?.path)
    }

    override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture, width: Int, height: Int) {
    }

    override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
        releaseMedia()
        return true
    }

    override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {
    }

    private fun releaseMedia() {
        loopHandler.stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}