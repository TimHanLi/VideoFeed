TimManager负责封装ViewPager2；
addTimLayer添加每个itemview展示的图层；
onCreateLayer()创建图层；
addData(list: MutableList<TimData>) 添加数据；
    

    private val timManager: TimManager by lazy(LazyThreadSafetyMode.NONE) {
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
    timManager.onCreateLayer()
