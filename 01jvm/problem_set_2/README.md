## 作业内容
自定义一个 Classloader，加载一个 Hello.xlass 文件，执行 hello 方法，此文件内容是一个 Hello.class 文件所有字节（x=255-x）处理后的文件

## 操作步骤
参考了范例，按照下面的流程初始化了一个Spring项目，并且将 Hello.xlass 放在工程的 /src/main/resources 下
1. 打开 Spring 官网: https://spring.io/
2. 找到 Projects --> Spring Initializr: https://start.spring.io/
3. 填写项目信息, 生成 maven 项目; 下载并解压。
4. Idea或者Eclipse从已有的Source导入Maven项目。
5. 从课件资料中找到资源 Hello.xlass 文件并复制到 src/main/resources 目录。
6. 编写代码，实现 findClass 方法，以及对应的解码方法
7. 编写main方法，调用 loadClass 方法；
8. 创建实例，以及调用方法
9. 执行.

这是我编写的执行类：[HelloXlassLoader](https://github.com/junyangwei/java-problem-sets/blob/main/01jvm/problem_set_2/src/main/java/work/HelloXlassLoader.java)
