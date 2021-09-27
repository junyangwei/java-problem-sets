## 作业内容
使用 GCLogAnalysis.java 自己演练一遍串行/并行/CMS/G1 的案例

## 操作步骤
打印日志执行类：[GCLogAnalysis](https://github.com/junyangwei/java-problem-sets/blob/main/02nio/problem_set_1/GCLogAnalysis.java)

使用的命令：
 1. 改变 GC 算法：
   - 使用串行GC `java -Xmx1g -Xms1g -XX:+UseSerialGC -XX:+PrintGCDetails -XX:+PrintGCDateStamps -Xloggc:serial_gc_log GCLogAnalysis`
   - 使用并行GC `java -Xmx1g -Xms1g -XX:+UseParallelGC -XX:+PrintGCDetails -XX:+PrintGCDateStamps -Xloggc:parallel_gc_log GCLogAnalysis`
   - 使用CMS GC `java -Xmx1g -Xms1g -XX:+UseConcMarkSweepGC -XX:+PrintGCDetails -XX:+PrintGCDateStamps -Xloggc:cmc_gc_log GCLogAnalysis`
   - 使用G1 GC `java -Xmx1g -Xms1g -XX:+UseG1GC -XX:+PrintGCDetails -XX:+PrintGCDateStamps -Xloggc:g1_gc_log GCLogAnalysis`
 2. 改变其他参数：
   - 修改堆大小：
     - `java -Xmx1g -Xms1g -XX:+PrintGCDetails -XX:+PrintGCDateStamps -Xloggc:heap1g_default_gc_log GCLogAnalysis`
     - `java -Xmx512m -Xms512m -XX:+PrintGCDetails -XX:+PrintGCDateStamps -Xloggc:heap512m_default_gc_log GCLogAnalysis`
     - `java -Xmx256m -Xms256m -XX:+PrintGCDetails -XX:+PrintGCDateStamps -XX:+UseSerialGC -Xloggc:heap256m_serial_gc_log GCLogAnalysis`
   - 修改打印日志参数 `java -Xmx1g -Xms1g -XX:+PrintGC -XX:+PrintGCDateStamps -Xloggc:heap1g_default_gc_simple_log GCLogAnalysis`
   - 关闭日志日期标记 `java -Xmx1g -Xms1g -XX:+PrintGCDetails -Xloggc:heap1g_default_gc_nodate_log GCLogAnalysis`
   - 打印简版信息(不含日期标记) `java -Xmx1g -Xms1g -XX:+PrintGC -Xloggc:heap1g_default_gc_simple_nodate_log GCLogAnalysis`

所有打印的日志，都在保存在这个文件夹下 log 结尾的文件

## 日志分析
[日志分析](https://github.com/junyangwei/java-problem-sets/blob/main/02nio/problem_set_1/GC%20%E6%97%A5%E5%BF%97%E5%88%86%E6%9E%90.md)
