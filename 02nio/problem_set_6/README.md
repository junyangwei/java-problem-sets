## 作业内容
写一段代码，使用 HttpClient 或 OkHttp 访问 http://localhost:8801 

## 操作步骤
参考了范例
1. 打开 Spring 官网: https://spring.io/
2. 找到 Projects --> Spring Initializr: https://start.spring.io/
3. 填写项目信息, 生成 maven 项目; 下载并解压。
4. Idea或者Eclipse从已有的Source导入Maven项目。
5. 搜索依赖， 推荐 mvnrepository: https://mvnrepository.com/
6. 搜索HttpClient，然后在 pom.xml 之中增加对应的依赖。
使用OkHttp
7. 使用HttpClient
    - 查找官网: http://hc.apache.org/
    - 参照官方示例编写代码: HttpClientHelper.java
    - 执行如果报错, 根据提示，增加 commons-logging 或者其他日志依赖。
8. 执行与测试.
    - 启动Http请求工具类main方法进行单元测试
    - 启动 problem_set_5中[HttpServer01](https://github.com/junyangwei/java-problem-sets/blob/main/02nio/problem_set_5/HttpServer01.java)
    - 启动 HttpPractice类main方法，模拟请求 http://localhost:8801

## 编写的类汇总
- 编写的Http请求工具类：[HttpClientHelper.java](https://github.com/junyangwei/java-problem-sets/blob/main/02nio/problem_set_6/src/main/java/work/HttpClientHelper.java)
- 编写的请求 http://localhost:8801 的类：[HttpPractice.java](https://github.com/junyangwei/java-problem-sets/blob/main/02nio/problem_set_6/src/main/java/work/HttpPractice.java)
