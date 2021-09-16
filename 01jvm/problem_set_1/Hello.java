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
