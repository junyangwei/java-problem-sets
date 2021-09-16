[toc]

## Hello.java文件源码

```java
/**
 * JVM进阶 —— 1.(选做) 简单的Hello.java
 */
public class Hello {
    public static void main(String[] args) {
        // 定义基本类型的变量
        int i1 = 1;
        byte b1 = 2;
        short s1 = 3;
        long l1 = 4L;
        float f1 = 5.0F;
        double d1 = 5.11D;
        char c1 = 'A';
        boolean b2 = true;

        // 进行简单的判断（i1小于f1），是则打印信息
        if (i1 < f1) {
            System.out.println("i1 is smaller than f1");
        }
        
        // for循环
        for (int i = 0; i < i1; i++) {
            // 进行四则运算（加减乘除）
            System.out.println("i add i1 : " + (i + i1));
            System.out.println("i sub i1 : " + (i - i1));
            System.out.println("i mult i1 : " + (i * i1));
            System.out.println("i divide i1 : " + (i / i1));
        }
    }
}
```

Tips：

- 下文的字节码信息通过在本地执行下面两条命令获得，并将其字节码信息保存在HelloByteCode.txt文件内（文件路径被我通过...省略了）
  - `javac -g Hello.java`
  - `javap -c -verbose Hello > HelloByteCode.txt`
- 因为字节码信息比较长，因此拆成多个模块，逐个模块进行分析

## 字节码分析

### 关于class文件的信息

```java
Classfile /Users/.../problem-sets/01jvm/problem_set_1/Hello.class
  Last modified 2021-9-15; size 1249 bytes
  MD5 checksum 77a578bab9ff47a1bf9d5110c8a2f095
  Compiled from "Hello.java"
public class Hello
  minor version: 0
  major version: 52
  flags: ACC_PUBLIC, ACC_SUPER
```

- 包含了一些文件的基本的信息：
  - 文件路径(Classfile)
  - 最近修改时间(Last modified)
  - 文件大小
  - MD5校验值
  - 从哪个文件编译而来(这里是Hello.java)
- 类的声明相关信息：
  - 次版本号(minor version)
  - 主版本号(major version)，这里合并起来就是52.0版本，即java8
  - 该类的一些标识(flags)，包括访问权限(public / private / protected)，父类、实现的接口等等

### 关于常量池 (Constant pool) 的信息

首先以前两条为例来分析：

```java
Constant pool:
   #1 = Methodref          #20.#53        // java/lang/Object."<init>":()V
   #2 = Long               4l
```

| 每行\每列的描述 | 常量编号（#加数字） | 分隔符（=） | 引用的具体类别                                            | 具体引用内容                                        | 注释                                                         |
| --------------- | ------------------- | ----------- | --------------------------------------------------------- | --------------------------------------------------- | ------------------------------------------------------------ |
| 1行             | #1                  | =           | Methodref<br />它是方法引用，表示引用的是哪个类的哪个方法 | #20.#53<br />这种形式表示同时引用了常量编号#20和#53 | // java/lang/Object."<init>":()V<br />是对引用内容的描述，在这里表示引用的是 (父类) Object类的初始化方法<init> (该方法没有返回值) |
| 2行             | #2                  | =           | Long<br />它是常量类型，表示这个常量是long类型            | 4L<br />它表示引用的就是long类型值4                 |                                                              |

#### 引用的具体类别拓展

| 类型                                              | 描述                                                         |
| ------------------------------------------------- | ------------------------------------------------------------ |
| Integer                                           | 一个 4 字节的 int 常量                                       |
| Long                                              | 一个 8 字节的 long 常量                                      |
| Float                                             | 一个 4 字节的 float 常量                                     |
| Double                                            | 一个 8 字节的 double 常量                                    |
| String                                            | 一个字符串常量（指向一个Utf8实体）                           |
| Utf8                                              | 一个UTF-8编码的字符串                                        |
| Class                                             | 一个类或接口的符号引用（指向一个Utf8实体)                    |
| NameAndType                                       | 字段和方法的名称以及类型的符号引用                           |
| Fieldref,<br />Methodref,<br />InterfaceMethodref | 字段的符号引用<br />类中方法的符号引用<br />接口中方法的符号引用 |

#### 完整的常量池字节码信息

根据前两行的分析和具体类别明拓展信息，后面的信息以此类推，不再重复说明

```java
Constant pool:
   #1 = Methodref          #20.#53        // java/lang/Object."<init>":()V
   #2 = Long               4l
   #4 = Float              5.0f
   #5 = Double             5.11d
   #7 = Fieldref           #54.#55        // java/lang/System.out:Ljava/io/PrintStream;
   #8 = String             #56            // i1 is smaller than f1
   #9 = Methodref          #57.#58        // java/io/PrintStream.println:(Ljava/lang/String;)V
  #10 = Class              #59            // java/lang/StringBuilder
  #11 = Methodref          #10.#53        // java/lang/StringBuilder."<init>":()V
  #12 = String             #60            // i add i1 :
  #13 = Methodref          #10.#61        // java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
  #14 = Methodref          #10.#62        // java/lang/StringBuilder.append:(I)Ljava/lang/StringBuilder;
  #15 = Methodref          #10.#63        // java/lang/StringBuilder.toString:()Ljava/lang/String;
  #16 = String             #64            // i sub i1 :
  #17 = String             #65            // i mult i1 :
  #18 = String             #66            // i divide i1 :
  #19 = Class              #67            // Hello
  #20 = Class              #68            // java/lang/Object
  #21 = Utf8               <init>
  #22 = Utf8               ()V
  #23 = Utf8               Code
  #24 = Utf8               LineNumberTable
  #25 = Utf8               LocalVariableTable
  #26 = Utf8               this
  #27 = Utf8               LHello;
  #28 = Utf8               main
  #29 = Utf8               ([Ljava/lang/String;)V
  #30 = Utf8               i
  #31 = Utf8               I
  #32 = Utf8               args
  #33 = Utf8               [Ljava/lang/String;
  #34 = Utf8               i1
  #35 = Utf8               b1
  #36 = Utf8               B
  #37 = Utf8               s1
  #38 = Utf8               S
  #39 = Utf8               l1
  #40 = Utf8               J
  #41 = Utf8               f1
  #42 = Utf8               F
  #43 = Utf8               d1
  #44 = Utf8               D
  #45 = Utf8               c1
  #46 = Utf8               C
  #47 = Utf8               b2
  #48 = Utf8               Z
  #49 = Utf8               StackMapTable
  #50 = Class              #33            // "[Ljava/lang/String;"
  #51 = Utf8               SourceFile
  #52 = Utf8               Hello.java
  #53 = NameAndType        #21:#22        // "<init>":()V
  #54 = Class              #69            // java/lang/System
  #55 = NameAndType        #70:#71        // out:Ljava/io/PrintStream;
  #56 = Utf8               i1 is smaller than f1
  #57 = Class              #72            // java/io/PrintStream
  #58 = NameAndType        #73:#74        // println:(Ljava/lang/String;)V
  #59 = Utf8               java/lang/StringBuilder
  #60 = Utf8               i add i1 :
  #61 = NameAndType        #75:#76        // append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
  #62 = NameAndType        #75:#77        // append:(I)Ljava/lang/StringBuilder;
  #63 = NameAndType        #78:#79        // toString:()Ljava/lang/String;
  #64 = Utf8               i sub i1 :
  #65 = Utf8               i mult i1 :
  #66 = Utf8               i divide i1 :
  #67 = Utf8               Hello
  #68 = Utf8               java/lang/Object
  #69 = Utf8               java/lang/System
  #70 = Utf8               out
  #71 = Utf8               Ljava/io/PrintStream;
  #72 = Utf8               java/io/PrintStream
  #73 = Utf8               println
  #74 = Utf8               (Ljava/lang/String;)V
  #75 = Utf8               append
  #76 = Utf8               (Ljava/lang/String;)Ljava/lang/StringBuilder;
  #77 = Utf8               (I)Ljava/lang/StringBuilder;
  #78 = Utf8               toString
  #79 = Utf8               ()Ljava/lang/String;
```

#### 具体引用内容——描述符拓展

在常量池具体引用内容中，有一些描述符，它的作用是用来描述字段的数据类型、方法的参数列表（包含数量、类型以及顺序）和返回值，如下表

| 标志符 | 含义                                                |
| ------ | --------------------------------------------------- |
| B      | 基本数据类型byte                                    |
| C      | 基本数据类型char                                    |
| D      | 基本数据类型double                                  |
| F      | 基本数据类型float                                   |
| I      | 基本数据类型int                                     |
| J      | 基本数据类型long                                    |
| S      | 基本数据类型short                                   |
| Z      | 基本数据类型boolean                                 |
| V      | 代表void类型                                        |
| L      | 对象类型，比如：`Ljava/lang/Object;`                |
| [      | 数组类型，代表一维数组。比如：`double[][][] = [[[D` |

参考自：[JVM字节码文件概述](https://segmentfault.com/a/1190000037594434)

### 默认构造函数分析

```java
public Hello();
    descriptor: ()V
    flags: ACC_PUBLIC
    Code:
      stack=1, locals=1, args_size=1
         0: aload_0
         1: invokespecial #1                  // Method java/lang/Object."<init>":()V
         4: return
      LineNumberTable:
        line 4: 0
      LocalVariableTable:
        Start  Length  Slot  Name   Signature
            0       5     0  this   LHello;
```

- public Hello() 是这个构造方法的声明
- descriptor: ()V 描述方法的入参类型和返回，这里括号中没有内容表示没有参数，V表示返回类型为void（即没有返回值）
- flags: 修饰方法则表示方法的标识，包含它的访问权限等等，这里ACC_PUBLIC表示它是一个公有的方法，修饰符为public
- Code: 表示的是方法的具体内容，在它的缩进中
  - stack=1, locals=1, args_size=1 表示栈的深度是1，局部变量表的槽位数为1，方法参数个数为1
    - 编号0字节码：将 this 压入(操作数栈)栈顶
    - 编号1字节码：调用 (父类) Object类的初始化方法<init>(没有返回值)；invokespecial是方法调用指令，用来调用构造函数，这里调用了常量池中编号 #1 引用的方法
    - 编号4字节码：执行完毕后返回（没有返回值）
  - Tips：
    - JVM会依赖stack, locals, args_size这些数据建立 **操作数栈** 和 **局部变量表**，并根据参数个数向局部变量表中依次加入参数
    - 方法参数个数为1的原因： 在Java中，对于非静态方法，**this**将被配分配到局部变量表的第0号槽位中
- LineNumberTable: 是Java字节码指令对应java文件中的第几行，以方便快速定位
- LocalVariableTable: 是本地变量表中的具体内容

### main方法

因为main方法的字节码信息太长，因此同样拆成几个部分来说明，结合实际的代码块

#### main方法描述

```java
public static void main(java.lang.String[]);
    descriptor: ([Ljava/lang/String;)V
    flags: ACC_PUBLIC, ACC_STATIC
```

- public static void main(java.lang.String[]) 是main方法的定义
- descriptor: ([Ljava/lang/String;)V 表明main方法有一个参数，类型为String数组，并且没有返回值
- flags: ACC_PUBLIC, ACC_STATIC 表明该方法是公有的 (public)，并且是静态的 (static)

#### 定义基本类型变量

```java
// 定义基本类型的变量
int i1 = 1;
byte b1 = 2;
short s1 = 3;
long l1 = 4L;
float f1 = 5.0F;
double d1 = 5.11D;
char c1 = 'A';
boolean b2 = true;
```

```java
    Code:
      stack=4, locals=12, args_size=1
         0: iconst_1
         1: istore_1
         2: iconst_2
         3: istore_2
         4: iconst_3
         5: istore_3
         6: ldc2_w        #2                  // long 4l
         9: lstore        4
        11: ldc           #4                  // float 5.0f
        13: fstore        6
        15: ldc2_w        #5                  // double 5.11d
        18: dstore        7
        20: bipush        65
        22: istore        9
        24: iconst_1
        25: istore        10
```

- 编号0字节码：将 int 类型的常量值 1 压入栈顶
- 编号1字节码：将栈顶为 int 类型的值存储到槽位为 1 的局部变量表中
- 编号2字节码：将 int 类型的常量值 2 压入栈顶
- 编号3字节码：将栈顶为 int 类型的值存储到槽位为 2 的局部变量表中
- 编号4字节码：将 int 类型的常量值 3 压入栈顶
- 编号5字节码：将栈顶为 int 类型的值存储到槽位为 1 的局部变量表中
- 编号6字节码：将常量池中编号 #2 的 long 类型的常量值压入操作数栈的栈顶中
- 编号9字节码：将栈顶为 long 类型的值保存到槽位为 4 的局部变量中（即将常量4赋值给l1）
- 编号11字节码：将常量池中编号 #4 的 float 类型的常量值压入操作数栈的栈顶中
- 编号13字节码：将栈顶为 float 类型的值保存到槽位为 6 的局部变量中（即将常量5.0赋值给f1）
- 编号15字节码：将常量池中编号 #5 的 double 类型的常量值压入操作数栈的栈顶中
- 编号18字节码：将栈顶为 double 类型的值保存到槽位为 7 的局部变量中（即将常量5.11赋值给d1）
- 编号20字节码：将单字节的常量值 65 压入到操作数的栈顶中（字符'A'的单字节常量值为65）
- 编号22字节码：将栈顶为 int 类型的值保存到槽位为 9 的局部变量中（即将'A'赋值给c1）
- 编号24字节码：将 int 类型的常量值 1 压入栈顶
- 编号25字节码：将栈顶为 int 类型的值存储到槽位为10的局部变量表中（即将true赋值给b2）

Tips：实际类型为boolean, byte, char, short, int在JVM计算类型中都是int

助记符的具体含义参考了：[JVM虚拟机字节码指令集](https://segmentfault.com/a/1190000008722128)，[JVM字节码指令集概述](https://segmentfault.com/a/1190000037628881)

#### if条件判断语句

```java
// 进行简单的判断（i1小于f1），是则打印信息
if (i1 < f1) {
    System.out.println("i1 is smaller than f1");
}
        
```

```java
        27: iload_1
        28: i2f
        29: fload         6
        31: fcmpg
        32: ifge          43
        35: getstatic     #7                  // Field java/lang/System.out:Ljava/io/PrintStream;
        38: ldc           #8                  // String i1 is smaller than f1
        40: invokevirtual #9                  // Method java/io/PrintStream.println:(Ljava/lang/String;)V
```

- 编号27字节码：将局部变量表中槽位为 1 的 int 类型值压入操作数栈的栈顶中
- 编号28字节码：将栈顶为 int 类型的值转为 float
- 编号29字节码：将局部变量表中槽位为 6 的 float 类型变量值压入操作数栈的栈顶中
- 编号31字节码：比较栈顶中第一个 float 类型值是否**大于**第二个 float 类型值，大于则将 1 压入栈顶 / 相等则将0压入栈顶 / 小于则将-1压入栈顶
  - Tips：fcmpg 可解读为(float, compare, great)，表示比较栈顶的两个 float 类型值的大小，并将结果(1, 0, -1)压入栈顶，当其中一个数值为NaN时，将1压入栈顶
- 编号32字节码：若栈顶 int 类型值大于等于 0 时，则直接跳到编号43字节码处（跳转指令）
  - Tips：ifge 是一个跳转指令，可解读为(if, great , equal)，表示当满足大于且等于的条件时，跳到指定编号的字节码处
- 编号35字节码：将常量池中编号 #7 的静态域 (System.out) 压入操作数栈的栈顶中（out 是 System 的静态常量，PrintStream 类对象）
- 编号38字节码：将常量池中编号 #8 的 String 类型值压入操作数栈的栈顶中（"i1 is smaller than f1"）
- 编号40字节码：调用 PrintStream 类对象的 println 方法 (没有返回值)；invokevirtual是方法调用指令，用来调用具体类型对象的方法，这里调用了常量池中编号 #9 引用的方法

条件语句的字节码参考：[条件判断语句案例分析](https://www.cnblogs.com/jzhlin/p/6008568.html)

#### 循环语句分析

```java
// for循环
for (int i = 0; i < i1; i++) {
    // 进行四则运算（加减乘除）
    System.out.println("i add i1 : " + (i + i1));
    System.out.println("i sub i1 : " + (i - i1));
    System.out.println("i mult i1 : " + (i * i1));
    System.out.println("i divide i1 : " + (i / i1));
}
```

##### 以其中加法来分析

考虑到这部分字节码信息过长，就单独拿加法来分析，重复内容就不分析了，差别在于算数运算指令`isub`， `imul`，` idiv`分别表示两个 int 类型值相减、相乘、相除

```java
        43: iconst_0
        44: istore        11
        46: iload         11
        48: iload_1
        49: if_icmpge     170
        52: getstatic     #7                  // Field java/lang/System.out:Ljava/io/PrintStream;
        55: new           #10                 // class java/lang/StringBuilder
        58: dup
        59: invokespecial #11                 // Method java/lang/StringBuilder."<init>":()V
        62: ldc           #12                 // String i add i1 :
        64: invokevirtual #13                 // Method java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        67: iload         11
        69: iload_1
        70: iadd
        71: invokevirtual #14                 // Method java/lang/StringBuilder.append:(I)Ljava/lang/StringBuilder;
        74: invokevirtual #15                 // Method java/lang/StringBuilder.toString:()Ljava/lang/String;
        77: invokevirtual #9                  // Method java/io/PrintStream.println:
       ......
       164: iinc          11, 1
       167: goto          46
       170: return
```

- 编号43字节码：将 int 类型值 0 压入栈顶
- 编号44字节码：将栈顶为 int 类型值存储到槽位为 11 的局部变量表中（即将0赋值给 i）
- 编号46字节码：将局部变量表中槽位为 11 的 int 类型值压入栈顶（取 i 的值）
- 编号48字节码：将局部变量表中槽位为 1 的 int 类型之压入栈顶（取 i1 的值）
- 编号49字节码：比较栈顶第一个 int 类型值是否**大于且等于**第二个 int 类型值，是则跳到编号170字节码处
  - Tips：if_icmpge 是跳转指令，可理解为(if, int, compare, great, equal)，比较栈顶 int 类型的两个值，若条件成功则调到指定编号的字节码处（这里等价于判断 i >= i1）
- 编号52字节码：将常量池中编号 #7 的静态域 (System.out) 压入操作数栈的栈顶中（out 是 System 的静态常量，PrintStream 类对象）
- 编号55字节码：为常量池中编号 #10 引用的类StringBuilder，构造一个新的对象，并将这个新对象的引用压入栈顶
- 编号58字节码：复制栈顶的值（即编号55字节码中压入栈的对象的引用）
  - Tips：后面的 invokespecial 指令会消耗掉一个当前类的引用，因此需要复制一份
- 编号59字节码：调用 StringBuilder 类的初始化方法 <init> (没有返回值)；invokespecial是方法调用指令，用来调用构造函数，这里调用了常量池中编号 #11 引用的方法
- 编号62字节码：将常量池中编号 #12 的 String 类型的值压入操作数栈的栈顶中（"i add i1 : "）
- 编号64字节码：调用 StringBuilder 类对象的append方法 (返回 StringBuilder 类对象的引用)；invokevirtual是方法调用指令，用来调用具体类型对象的方法，这里调用了常量池中编号 #13 引用的方法
- 编号67字节码：将局部变量表中槽位为 11 的 int 类型值压入栈顶
- 编号69字节码：将局部变量表中槽位为 1 的 int 类型值压入栈顶
- 编号70字节码：将栈顶的两个 int 类型值相加，并将结果值压入栈顶；iadd 是算数运算指令，表示将两个 int 类型值相加，求得的结果压入栈顶
- 编号71字节码：调用 StringBuilder 类对象的 append 方法 (入参为 Integer 类型，返回 StringBuilder 类对象的引用)；这里调用了常量池中编号 #14 引用的方法
- 编号74字节码：调用 StringBuilder 类对象的 toString 方法 (返回 String 类的对象引用)；这里调用了常量池中编号 #15 引用的方法
- 编号77字节码：调用 PrintStream 类对象的 println 方法 (没有返回值)；这里调用了常量池中编号 #9 引用的方法
- ...... // 重复内容省略
- 编号164字节码：将局部变量表槽位为 11 的 int 类型值递增，递增值为1（即 i++）；iinc 是递增指令，后面跟着“局部变量表槽位, 递增值”，表示将指定局部变量表槽位的值，增加指定值
- 编号167字节码：跳到编号 46 字节码处（跳转指令）
- 编号170字节码：执行完毕后返回（没有返回值）

以上就是全部信息，我发现直接分析字节码信息可以看出很多隐式的信息，比如在 Java 中使用加号 "+" 进行字符串拼接，其底层是构造了一个新的 StringBuilder 类对象，通过调用对象的append方法将字符串拼接。

字节码分析相关参考资源：[Java字节码与字节码分析](https://juejin.cn/post/6921193897523838983)，[Java字节码总结](https://cyw3.github.io/YalesonChan/2016/Java-Byte-Code.html)

## 参考信息汇总

- [JVM字节码文件概述](https://segmentfault.com/a/1190000037594434)
- [JVM虚拟机字节码指令集](https://segmentfault.com/a/1190000008722128)
- [JVM字节码指令集概述](https://segmentfault.com/a/1190000037628881)
- [条件判断语句案例分析](https://www.cnblogs.com/jzhlin/p/6008568.html)
- [Java字节码与字节码分析](https://juejin.cn/post/6921193897523838983)
- [Java字节码总结](https://cyw3.github.io/YalesonChan/2016/Java-Byte-Code.html)
- [字节码增强技术探索](https://tech.meituan.com/2019/09/05/java-bytecode-enhancement.html)