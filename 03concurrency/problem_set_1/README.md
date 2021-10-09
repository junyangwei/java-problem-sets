## 作业内容
1. （必做）整合上次作业的 httpclient/okhttp
2. （选做）使用 netty 实现后端 http 访问（代替上一步骤）
3. （必做）实现过滤器
4. （选做）实现路由

## 操作步骤
参考范例
1. 下载老师的项目: https://github.com/JavaCourse00/JavaCourseCodes
2. 解压, 拷贝其中 02nio 路径下的 nio02 目录到第三周的作业目录下。
3. Idea或者Eclipse打开这个Maven项目。
4. 增加自己的包名, 以做标识。
5. 将第二周的作业代码整合进来: homework02 中的代码和pom.xml依赖。
6. 将 nio01 中的 HttpServer03 代码整合进来作为后端服务，改名为 BackendServer, 监听 8088 端口。
7. 找到Netty官网: https://netty.io/wiki/user-guide-for-4.x.html
8. 参照官方示例, 编写自己的过滤器 ProxyBizFilter;
9. 可以加入到 HttpInboundHandler.java; 【实际上应该加入到 HttpInboundInitializer 的初始化方法中】。
10. 修改 HttpOutboundHandler 类，集成自己写的第二周的作业代码；

## 其他说明：
- 过滤器目录：/src/main/java/filter
- 