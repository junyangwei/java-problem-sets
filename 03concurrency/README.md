# 第3周作业
1. problem_set_1 拉了示例代码，整合了 httpclient
2. netty-practices 是Netty HTTP 的服务端、客户端以及两者结合的实践
    - 开这个项目起因是在 problem_set_1 中，写着写着代码太多，自己都乱了，也感觉没有掌握 Netty
    - 于是所以重新整理思路，将实践代码写在这个项目中 
    - 这个单独开了一个仓库，将代码复制过来的，源码也在GitHub中 [详见](https://github.com/junyangwei/netty-practices)

## 作业链接
1. （必做）整合你上次作业的 httpclient/okhttp；
    - [作业1链接](https://github.com/junyangwei/java-problem-sets/tree/main/03concurrency/problem_set_1/src/main/java/outbound/httpclient4)
    - Tips: 上一次的作业使用的是 httpclient

2. （选做）使用 netty 实现后端 http 访问（代替上一步骤）
    - [单独实现 Netty 客户端访问 后端 HTTP 参考 nettyhttpclient01 文件夹](https://github.com/junyangwei/netty-practices/tree/main/src/main/java/nettyhttpclient01) 
    - [单独实现 Netty 服务端 参考 nettyhttpserver01 文件夹](https://github.com/junyangwei/netty-practices/tree/main/src/main/java/nettyhttpserver01) 
    - [整合 Netty 服务端 与 Netty 客户端结合的实践，启动器在 nettyhttpserver02 文件夹](https://github.com/junyangwei/netty-practices/tree/main/src/main/java/nettyhttpserver02)
        - 关于nettyhttpserver02的[特别说明](https://github.com/junyangwei/netty-practices#%E7%89%B9%E5%88%AB%E8%AF%B4%E6%98%8E)

3. （必做）实现过滤器。
    - netty-practices 中过滤器在 [ProxyBizFilter](https://github.com/junyangwei/netty-practices/blob/main/src/main/java/filter01/ProxyBizFilter.java)
    - 简单校验了当前请求路径是否包含预期的"后端API标记"，不是则返回空内容

4. （选做）实现路由。
    - netty-practices 中的路由在 [router01](https://github.com/junyangwei/netty-practices/tree/main/src/main/java/router01)
    - 这个目前还没有运用到实际中，所以只是单纯写了个方法
    - 不过[后端服务的枚举](https://github.com/junyangwei/netty-practices/blob/main/src/main/java/router01/ApiTagEnum.java)（标记和地址）倒是有用到，不想完全写死路径在代码里

5. （选做）跑一跑课上的各个例子，加深对多线程的理解
    - 未完成 

6. （选做）完善网关的例子，试着调整其中的线程池参数
    - 未完成 

