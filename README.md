目前市面上视频feed流实现有以下四种方式:  
1.ViewPager2和Fragment  
优点：方案简单，开发成本低  
缺点：fragment创建和销毁有一定的开销，会有明显掉帧情况  
2.RecyclerView和PagerSnapHelper  
优点：api更灵活，扩展性更强，性能好  
缺点：item条目生命周期需要单独控制，需要额外单独处理多指操作和一直滑动情况  
3.ViewPager2和RecyclerView.Adapter  
优点：性能好，api成熟，多指操作和一直滑动情况兼容好  
缺点：item条目生命周期需要单独控制  
4.自定义viewpager 成本高，此方案暂不考虑  
笔者选择了第三个方案  

api使用说明  
![image](https://github.com/TimHanLi/VideoFeed/blob/main/image/%E6%9E%B6%E6%9E%84.jpg)

Feed流：使用ViewPager2实现  
Page：TimItemView，条目view，每个itemview上都可以承载n层layer  
Layer：layer中加载布局具体的文件  
Layout：业务ui控制层，例如播放视频、播放进度等  
VideoFeed用不到1000行代码实现了Feed流；有以下优势  

架构清晰，每层责任清楚  
单一原则，layout负责单一业务，代码简洁、逻辑清晰  
耦合度低，各个层级、业务不会产生依赖  
性能好，问题少，使用viewpager2性能几乎和RecyclerView差不多  
扩展性强，VideoFeed仅仅是帮我们实现Feeds，不关心具体业务  
播放器数量，在Video播放layout中，可复用一个播放器或者使用多个播放器，均没有问题  
具体使用：  
Feed流-viewpager2设置   
![image](https://github.com/TimHanLi/VideoFeed/blob/main/image/%E5%85%B7%E4%BD%93%E4%BD%BF%E7%94%A8.jpg)

图层layer  
demo中广告层和其他图层是互斥的，demo中根据type进行了判断，例如  
广告层   
![image](https://github.com/TimHanLi/VideoFeed/blob/main/image/layer.jpg)
操作面板层  
![image](https://github.com/TimHanLi/VideoFeed/blob/main/image/panel.jpg)
播放层   
![image](https://github.com/TimHanLi/VideoFeed/blob/main/image/%E6%92%AD%E6%94%BE.jpg)
