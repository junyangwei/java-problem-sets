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

## 拟真场景及预期
### 场景
1. 假设有两台机器(在同一机房下)，每台机器上都部署后端服务：netty_test01 和 netty_test02
2. 在本地模拟直接使用 localhost 和 127.0.0.1 模拟是两台机器的内网IP
3. netty_test01 服务上有接口："GET /test01api/get" 和 "POST /test01api/post"
4. netty_test02 服务上有接口："GET /test02api/get" 和 "POST /test02api/post"
5. netty_test01 固定启动的端口为 8801，netty_test02 固定启动端口为 8802
6. 实现的 netty 网关服务，启动端口为 8800
7. 通过过滤器在 请求头 和 响应头 加"机器IP"以及"后端服务名"

### 预期
1. 访问 netty 网关服务：
    - http://127.0.0.1:8800/test01api/ 时会自动将请求发到 netty_test01 服务
    - http://127.0.0.1:8800/test02api/ 时会自动将请求发到 netty_test02 服务
2. 打印访问 netty 网关服务代理请求的信息
    - 发送的实际后端服务地址
    - 其响应的 body
    - 查看 GET 方法和 POST 方法是否成功
    - 查看是否模拟在不同机器内网"IP"间调用
    - 查看请求头和响应头信息

## 校验注意
1. 先启动resources目录下 test01 和 test02 服务
    - java -jar test01-0.0.1-SNAPSHOT.jar
    - java -jar test02-0.0.1-SNAPSHOT.jar
2. 再启动主程序：ApplicationStart
3. 最后再分别访问测试地址
    - http://localhost:8800
    - http://localhost:8800/test01api/get
    - http://localhost:8800/test02api/get
