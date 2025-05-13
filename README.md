1、setting中设置
mavenCentral()
maven("https://jitpack.io")

2、对应模块添加依赖，最新版本是1.0.1
implementation("com.github.TimHanLi:VideoFeed:最新版本")

3、使用
private val feedManager: TimManager by lazy(LazyThreadSafetyMode.NONE) {
    TimManager().apply {
        // 视频播放层
        addTimLayer(VideoPlayLayer(this@MainActivity))
        // 操作面板层
        addTimLayer(PanelLayer(this@MainActivity))
        // 广告层
        addTimLayer(AdLayer(this@MainActivity))
        // 设置viewpager2
        setVp2(findViewById(R.id.vp2))
    }
}

VideoPlayLayer、PanelLayer、AdLayer均由业务自己控制实现，例如
<img width="882" alt="image" src="https://github.com/user-attachments/assets/14980077-253a-4d59-be4c-44952d3f768f" />


4、创建feed流
feedManager.onCreateLayer()

5、更新数据
feedManager.addData(it)




