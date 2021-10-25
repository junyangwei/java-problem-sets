# 第五周作业
由于之前业务繁忙期，第四周第五周作业都要一周之内完成，因此第五周只做了必做作业，后续再补充

## 必做作业1
写代码实现 Spring Bean 的装配，方式越多越好（XML、Annotation 都可以）
- [使用 XML 显示地装配 Bean](https://github.com/junyangwei/java-problem-sets/blob/main/05spring/springtest/src/main/java/bean/BeanAssemble01.java)，[配置文件](https://github.com/junyangwei/java-problem-sets/blob/main/05spring/springtest/src/main/resources/applicationContext.xml)
- [使用 Java 代码显示地装配 Bean](https://github.com/junyangwei/java-problem-sets/blob/main/05spring/springtest/src/main/java/bean/BeanAssemble02.java)
- [隐式地 Bean 扫描发现机制 和 自动装配](https://github.com/junyangwei/java-problem-sets/blob/main/05spring/springtest/src/main/java/bean/BeanAssemble03.java)

## 必做作业2
给前面课程提供的 Student/Klass/School 实现自动配置和 Starter
（不知道是不是我的理解有误，感觉和必做作业1有交集）
- [作业地址](https://github.com/junyangwei/java-problem-sets/tree/main/05spring/springtest/src/main/java/autoassemble)
- [相关配置文件地址](https://github.com/junyangwei/java-problem-sets/blob/main/05spring/springtest/src/main/resources/applicationContext.xml)

## 必做作业3
研究一下 JDBC 接口和数据库连接池，掌握它们的设计和用法：
- 使用 JDBC 原生接口，实现数据库的增删改查操作。
- 使用事务，PrepareStatement 方式，批处理方式，改进上述操作。
- 配置 Hikari 连接池，改进上述操作。提交代码到 GitHub。

作业完成的内容
- [使用 JDBC 原生接口实现增删查改](https://github.com/junyangwei/java-problem-sets/blob/main/05spring/springtest/src/main/java/jdbc/JDBCTest.java)
- 使用 Prepare Statements 改进（这个和上面在一个文件中）
- [使用 Hikari连接池改进](https://github.com/junyangwei/java-problem-sets/blob/main/05spring/springtest/src/main/java/jdbc/HikariJDBCTest.java)
