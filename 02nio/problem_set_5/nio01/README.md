## 作业内容
运行课上 Netty 的例子

## 操作步骤
1. 打开 Spring 官网: https://spring.io/
2. 找到 Projects --> Spring Initializr: https://start.spring.io/
3. 填写项目信息, 生成 maven 项目; 下载并解压。
4. Idea或者Eclipse从已有的Source导入Maven项目。
5. 搜索依赖， 推荐 mvnrepository: https://mvnrepository.com/
6. 搜索 netty，然后在 pom.xml 之中增加对应的依赖。
7. 执行与测试.

## 编写的类
- [HttpHandler类](https://github.com/junyangwei/java-problem-sets/blob/main/02nio/problem_set_5/nio01/src/main/java/nio01/netty/HttpHandler.java)
- [HttpInitializer类](https://github.com/junyangwei/java-problem-sets/blob/main/02nio/problem_set_5/nio01/src/main/java/nio01/netty/HttpInitializer.java)
- [NettyHttpServer类](https://github.com/junyangwei/java-problem-sets/blob/main/02nio/problem_set_5/nio01/src/main/java/nio01/netty/NettyHttpServer.java)
