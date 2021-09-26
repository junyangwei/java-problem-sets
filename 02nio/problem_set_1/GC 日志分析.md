## 并行GC日志分析 (java8默认)

考虑到 [GC 日志](https://github.com/junyangwei/java-problem-sets/blob/main/02nio/problem_set_1/heap1g_default_gc_log)较大，因此分几个模块来说明

### GC 日志头

```tex
Java HotSpot(TM) 64-Bit Server VM (25.201-b09) for bsd-amd64 JRE (1.8.0_201-b09), built on Dec 15 2018 18:35:23 by "java_re" with gcc 4.2.1 (Based on Apple Inc. build 5658) (LLVM build 2336.11.00)
Memory: 4k page, physical 16777216k(449768k free)
```

- 包含了一些JVM的基本信息，以及本机的内存信息
  - Java虚拟机类型：HotSpot虚拟机
  - 位数：64位
  - JRE版本：1.8
  - 物理内存：16G (约0.43G空闲)

### 命令行参数

```
CommandLine flags: -XX:InitialHeapSize=1073741824 -XX:MaxHeapSize=1073741824 -XX:+PrintGC -XX:+PrintGCDateStamps -XX:+PrintGCDetails -XX:+PrintGCTimeStamps -XX:+UseCompressedClassPointers -XX:+UseCompressedOops -XX:+UseParallelGC 
```

- InitialHeapSize：等价于Xms，这里表示分配 1G 内存 (1073741824/1024/1024)
- MaxHeapSize：等价于Xmx，这里表示分配了 1G 内存
- PrintGC：该参数是开启打印日志开关
  - 根据多个 GC 日志记录，发现只要有 `+PrintGC...` 相关的命令时，都会默认开启
- PrintGCDateStamps：开启打印 GC 日志日期标记的开关
- PrintGCDetails：开启打印 GC 详情日志的开关
- PrintGCTimeStamps：开启打印 GC 日志时间标记的开关
  - 根据多个 GC 日志记录，发现这个是默认开启的，即使启动参数没有
- UseCompressedOops：开启普通对象指针压缩的开关
  - oops指的是普通对象指针：ordinary object pointers
  - 在  64位 JVM中，在Xmx小于32G时，默认开启（[参考](https://javarevisited.blogspot.com/2012/06/what-is-xxusecompressedoops-in-64-bit.html)）
- UseCompressedClassPointers：开启类指针压缩的开关
  - 在 64位 JVM中，当Xmx小于32G时，默认开启，情况与 `UseCompressedOops` 类似，它们的目的都是为了节省内存
- UseParallelGC：使用并行GC的开关

实际启动命令为

```
java -Xmx1g -Xms1g -XX:+UseParallelGC -XX:+PrintGCDetails -XX:+PrintGCDateStamps -Xloggc:parallel_gc_log GCLogAnalysis
```

通过对比可知，其中的 `-Xmx` 和 `-Xms` 转换了形式，而`-XX:+PrintGC`，`-XX:+PrintGCTimeStamps`，`-XX:+UseCompressedClassPointers` ，`-XX:+UseCompressedOops` 这几项是默认添加的

### 详细的GC日志

先拿第一条普通的GC记录来分析

```tex
2021-09-24T14:32:34.871-0800: 0.216: [GC (Allocation Failure) [PSYoungGen: 262144K->43516K(305664K)] 262144K->87329K(1005056K), 0.0318466 secs] [Times: user=0.04 sys=0.15, real=0.03 secs] 
```

- `2021-09-24T14:32:34.871-0800` 是具体的GC时间点 ( `-XX:+PrintGCDateStamps` 触发)
- `[GC (Allocation Failure) [PSYoungGen: 262144K->43516K(305664K)] 262144K->87329K(1005056K), 0.0318466 secs]` 表示：分配内存失败，原因是发生了 Minor GC（Young GC），年轻代的内存空间不够了，因此做了一次 GC 垃圾回收
  - `PSYoungGen` 表示年轻代的垃圾收集器，`PS` 含义是：Parallel Scavenge，表示对年轻代使用并行的方式进行垃圾回收（复制算法）
  - `[PSYoungGen 262114K->43516K(305664K)]` 表示：[GC前年轻代已使用的内存->GC后年轻代使用中的内存(年轻代的总内存)]
  - `262144K->87329K(1005056K)` 表示：GC前Java堆占已使用的内存->GC后Java堆使用中的内存(Java堆的总内存)
  - `0.0318466 secs` 表示 Java 堆进行 GC 操作总耗时，单位秒
- `[Times: user=0.04 sys=0.15, real=0.03 secs]` 表示：
  - user代表进程在用户态消耗的CPU时间
  - sys代表进程在内核太消耗的CPU时间
  - real代表程序从开始到结束所用的时间（包括其他进程使用的时间片和阻塞时间）

参考：[GC日志查看分析](https://blog.csdn.net/TimHeath/article/details/53053106)，[user,sys,real](https://cloud.tencent.com/developer/article/1491229)

再拿第一条Full GC日志来分析

```tex
2021-09-24T14:32:35.558-0800: 0.903: [Full GC (Ergonomics) [PSYoungGen: 43032K->0K(232960K)] [ParOldGen: 617095K->336212K(699392K)] 660127K->336212K(932352K), [Metaspace: 2717K->2717K(1056768K)], 0.0466376 secs] [Times: user=0.27 sys=0.01, real=0.05 secs] 
```

可以看出，Full GC 的原因是 Ergonomics，它的底层实现是通过判断：当即将晋升到老年代的**平均大小**“大于”老年代的剩余大小时，就会返回 true，告诉 JVM 需要触发一次 Full GC了

- `ParOldGen` 表示老年代的垃圾收集器，`ParOld` 含义是：Parallel Old，表示对老年代使用并行的方式进行垃圾回收（标记-整理算法）
- `Metaspace` 表示的是元数据区

Tips：从这里可以看出java8默认的 GC 算法为：Parallel Scavenge(年轻代) + Parallel Old(老年代) [[参考](https://blog.csdn.net/weixin_43753797/article/details/106450040)]

参考：[读懂一行Full GC日志](https://cloud.tencent.com/developer/article/1082687)，[记一次有惊无险的的JVM优化经历](https://zhuanlan.zhihu.com/p/70010064)

详细的日志直接使用启动命令就可以查看了（设置堆内存 -Xmx512m 才更容易看见Full GC）：
`java -Xmx512m -Xms512m -XX:+UseParallelGC -XX:+PrintGCDetails -XX:+PrintGCDateStamps GCLogAnalysis`


### 堆&元数据区

```
Heap
 PSYoungGen      total 232960K, used 45353K [0x00000007aab00000, 0x00000007c0000000, 0x00000007c0000000)
  eden space 116736K, 4% used [0x00000007aab00000,0x00000007ab04a908,0x00000007b1d00000)
  from space 116224K, 34% used [0x00000007b1d00000,0x00000007b43ffc20,0x00000007b8e80000)
  to   space 116224K, 0% used [0x00000007b8e80000,0x00000007b8e80000,0x00000007c0000000)
 ParOldGen       total 699392K, used 475780K [0x0000000780000000, 0x00000007aab00000, 0x00000007aab00000)
  object space 699392K, 68% used [0x0000000780000000,0x000000079d0a10d8,0x00000007aab00000)
 Metaspace       used 2724K, capacity 4486K, committed 4864K, reserved 1056768K
  class space    used 297K, capacity 386K, committed 512K, reserved 1048576K
```

- 堆
  - 分配给年轻代的总内存为：232960K (227.5M)，使用了 45353K (约44.3M)
  - 分配给老年代的总内存为：699392K (683M)，使用了 475780K (464.6M)
- 元数据区
  - (used)已使用2724K，(capacity)当前分配块内存为4486K，(committed)已提交的块内存为4864K，(reserved)分配的总内存为 1056768K
  - class space指的是：压缩的类的指针(元数据)对应的值的占比

参考：[理解JVM堆中Metaspace](https://www.cnblogs.com/benwu/articles/8312699.html)，[JVM used/capacity/committed/reserved](https://blog.csdn.net/reliveIT/article/details/115377713)

## 串行GC日志分析

与并行GC相同的 GC 日志头、命令行参数、堆&元数据区就不再重复分析了，直接分析详细日志中

先拿第一条普通的GC记录来分析

```tex
2021-09-24T18:38:52.491-0800: 0.118: [GC (Allocation Failure) [DefNew: 69952K->8704K(78656K), 0.0137334 secs] 69952K->23377K(253440K), 0.0138492 secs] [Times: user=0.00 sys=0.01, real=0.01 secs] 
```

- `[GC (Allocation Failure) [DefNew: 69952K->8704K(78656K), 0.0137334 secs] 69952K->23377K(253440K), 0.0138492 secs]` 表示：分配内存失败，原因是发生了 Young GC，年轻代的内存空间不够了，因此做了一次 GC 垃圾回收
  - `DefNew` 表示年轻代使用串行(Serial)的垃圾收集器，全称“Default New Generation” 
  - `[DefNew: 69952K->8704K(78656K), 0.0137334 secs]` 每个位置分别表示：[GC前年轻代已使用的内存->GC后年轻代使用中的内存(年轻代的总内存)，GC花费的时间]
  - `69952K->23377K(253440K)` 每个位置分别表示：GC前Java堆占已使用的内存->GC后Java堆使用中的内存(Java堆的总内存)
  - `0.0138492 secs` 表示 Java 堆进行 GC 操作总耗时，单位秒

再拿第一条Full GC日志来分析

```tex
2021-09-24T18:38:52.734-0800: 0.361: [Full GC (Allocation Failure) 2021-09-24T18:38:52.734-0800: 0.361: [Tenured: 174733K->174781K(174784K), 0.0262162 secs] 253373K->196338K(253440K), [Metaspace: 2717K->2717K(1056768K)], 0.0263211 secs] [Times: user=0.02 sys=0.00, real=0.03 secs] 
```

可以看出，Full GC 的原因是 Allocation Failure，老年代分配的内存不够了，Tenured 区设置的总内存为 174784K，当前已使用内存为174733K，剩余的内存为51K，不够为分配到下一次即将晋升到老年代的大小

- `Tenured` 可以称为 `Tenured Generation (Old Generation)`，表示老年代

详细的日志直接使用启动命令就可以查看了（设置堆内存 -Xmx256m 才更容易看见Full GC）：
`java -Xmx256m -Xms256m -XX:+UseSerialGC -XX:+PrintGCDetails -XX:+PrintGCDateStamps GCLogAnalysis`

## CMS GC日志分析

与并行GC相同的 GC 日志头、命令行参数、堆&元数据区就不再重复分析了，直接分析详细日志中

先拿第一条普通的GC记录来分析

```tex
2021-09-25T11:43:20.098-0800: [GC (Allocation Failure) [ParNew: 69882K->8703K(78656K), 0.0161607 secs] 69882K->25023K(253440K), 0.0163046 secs] [Times: user=0.03 sys=0.06, real=0.02 secs]
```

- `[GC (Allocation Failure) [ParNew: 69882K->8703K(78656K), 0.0161607 secs] 69882K->25023K(253440K), 0.0163046 secs]` 表示：分配内存失败，原因是发生了 Young GC，年轻代的内存空间不够了，因此做了一次 GC 垃圾回收
  - `ParNew` 表示本次 GC 使用的垃圾收集器时 ParNew，它是专门针对新生代的垃圾收集器
  - `[ParNew: 69882K->8703K(78656K), 0.0161607 secs]` 每个位置分别表示：[GC前年轻代已使用的内存->GC后年轻代使用中的内存(年轻代的总内存)，GC花费的时间]
  - `69882K->25023K(253440K)` 每个位置分别表示：GC前Java堆占已使用的内存->GC后Java堆使用中的内存(Java堆的总内存)
  - `0.0163046 secs` 表示 Java 堆进行 GC 操作总耗时，单位秒

再拿第一条Full GC日志来分析

```tex
2021-09-24T18:38:52.734-0800: 0.361: [Full GC (Allocation Failure) 2021-09-24T18:38:52.734-0800: 0.361: [Tenured: 174733K->174781K(174784K), 0.0262162 secs] 253373K->196338K(253440K), [Metaspace: 2717K->2717K(1056768K)], 0.0263211 secs] [Times: user=0.02 sys=0.00, real=0.03 secs] 
```

可以看出，Full GC 的原因是 Allocation Failure，老年代分配的内存不够了，Tenured 区设置的总内存为 174784K，当前已使用内存为174733K，剩余的内存为51K，不够为分配到下一次即将晋升到老年代的大小

- `Tenured` 可以称为 `Tenured Generation (Old Generation)`，表示老年代

详细的日志直接使用启动命令就可以查看了（设置堆内存 -Xmx256m 才更容易看见Full GC）：
`java -Xmx256m -Xms256m -XX:+UseConcMarkSweepGC -XX:+PrintGCDetails -XX:+PrintGCDateStamps GCLogAnalysis`
