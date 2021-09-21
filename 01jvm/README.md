# 第1周作业
参见 01jvm 下的各个文件夹

## 作业链接
1.（选做）自己写一个简单的 Hello.java，里面需要涉及基本类型，四则运行，if 和 for，然后自己分析一下对应的字节码，有问题群里讨论。
  - [Hello.java 作业链接](https://github.com/junyangwei/java-problem-sets/blob/main/01jvm/problem_set_1/Hello.java)
  - [字节码分析](https://github.com/junyangwei/java-problem-sets/blob/main/01jvm/problem_set_1/Hello%E5%AD%97%E8%8A%82%E7%A0%81%E5%88%86%E6%9E%90.md)

2.（必做）自定义一个 Classloader，加载一个 Hello.xlass 文件，执行 hello 方法，此文件内容是一个 Hello.class 文件所有字节（x=255-x）处理后的文件。文件群里提供。
  - [HelloXlassLoader加载器](https://github.com/junyangwei/java-problem-sets/blob/main/01jvm/problem_set_2/src/main/java/work/HelloXlassLoader.java)
  - [详情描述](https://github.com/junyangwei/java-problem-sets/tree/main/01jvm/problem_set_2)

3.（必做）画一张图，展示 Xmx、Xms、Xmn、Meta、DirectMemory、Xss 这些内存参数的关系。
  - [个人画的内存参数关系图](https://github.com/junyangwei/java-problem-sets/blob/main/01jvm/problem_set_3/JVM%E5%86%85%E5%AD%98%E6%A8%A1%E5%9E%8B.png)
  - [详情(markdown)](https://github.com/junyangwei/java-problem-sets/tree/main/01jvm/problem_set_3)
