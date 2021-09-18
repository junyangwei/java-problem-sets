package work;

import java.io.*;
import java.lang.reflect.Method;
import java.util.NoSuchElementException;

/**
 * 定义一个自定义加载器
 *  - 加载 Hello.xlass 文件
 *  - 并且调用它的 hello 方法
 * 注意：
 *  - Hello.xlass 是 Hello.class 字节码文件所有字节经过（255 - 每个字节）加密处理
 *  - Hello.xlass 存放的目录为 /src/main/resources
 *
 * @author junyangwei
 * @date 2021-09-18
 */
public class HelloXlassLoader extends ClassLoader {

    public static void main(String[] args) {
        new HelloXlassLoader().loadClass();
    }

    /**
     * 加载 Hello 类并调用它的 hello 方法
     */
    private void loadClass() {
        // 待加载的类名
        String className = "Hello";

        // 待调用的方法名
        String methodName = "hello";

        // 找到资源文件 /src/main/resources 目录指定的 Hello.xlass 文件，并加载
        Class<?> clazz = new HelloXlassLoader().findClass(className);

        try {
            // 通过反射，获取 Hello 类的 hello 方法
            Method method = clazz.getDeclaredMethod(methodName);

            // 通过反射，初始化 Hello 类
            Object obj = clazz.newInstance();
            // 调用 Hello 类的 hello 方法
            method.invoke(obj);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 找到资源文件 /src/main/resources 目录指定的 .xlass 文件，并加载
     * @Param className 待加载的类名
     */
    @Override
    protected Class<?> findClass(String className) {
        // 获取待加载的完整文件名
        final String fileName = className + ".xlass";

        // 定义返回的字节流
        byte[] bytesResult = new byte[0];

        // 获取当前类的应用类加载器
        ClassLoader appClassLoader = this.getClass().getClassLoader();

        /*
            - 使用了带资源的 try 语句，结束时自动调用 close 关闭资源
            - 加载工程目录下 /src/main/resources/Hello.xlass 文件为输入流
            - 以字节流形式读取文件，结果存储在 bytesStream中
            - 获取解码后的 Byte 数组
        */
        try (InputStream inputStream = appClassLoader.getResourceAsStream(fileName)) {
            if (inputStream == null) {
                throw new NoSuchElementException();
            }

            byte[] bytesStream = new byte[inputStream.available()];
            inputStream.read(bytesStream);

            bytesResult = decode(bytesStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 调用ClassLoader自带的defineClass（相当于默认通过JVM内部的ClassLoader机制加载Hello.class文件）
        return defineClass(className, bytesResult, 0, bytesResult.length);
    }

    /**
     * 解码字节流（单纯位置偏移类的加密）
     *  - 加密方式：每个位置加密后字节码 = 255 - 每个位置的字节码
     *  - 解密方式：每个位置字节码 = 255 - 每个位置加密字节码
     * @param bytesStream 加密后的字节流
     * @return decodeBytes 解密后的字节流
     */
    private byte[] decode(byte[] bytesStream) {
        byte[] decodeBytes = new byte[bytesStream.length];
        for (int i = 0; i < bytesStream.length; i++) {
            decodeBytes[i] = (byte) (255 - bytesStream[i]);
        }
        return decodeBytes;
    }
}
