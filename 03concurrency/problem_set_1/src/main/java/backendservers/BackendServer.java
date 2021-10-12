package backendservers;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 实现一个最简单的 HTTP 服务器（线程池）
 *  - 访问：http://localhost:8088
 *  - 启动参数：-Xmx512m
 * @author junyangwei
 * @date 2021-10-10
 */
public class BackendServer {

    public static void main(String[] args) throws IOException {
        // 使用 JDK 方法创建一个线程池
        ExecutorService executorService = Executors.newFixedThreadPool(
                Runtime.getRuntime().availableProcessors() + 2);
        // 创建一个绑定8088端口的ServerSocket类对象
        final ServerSocket serverSocket = new ServerSocket(8088);

        // 在while循环中不断地尝试（单线程）
        while (true) {
            try {
                // 等待客户端的请求过来
                final Socket socket = serverSocket.accept();
                // 当客户端请求过来后，将待处理的请求交给我们的线程池，来异步处理，就不需要每个请求都创建一个线程了
                executorService.execute(() -> server(socket));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 与客户端通信 server 方法，响应客户端的请求
     * @param socket 服务器创建的 Socket 对象，用于与客户端进行通信
     */
    public static void server(Socket socket) {
        try {
            // 打开双方的 socket 的通道，并且打开一个输入流，模拟向请求客户端响应数据
            PrintWriter printWriter = new PrintWriter(socket.getOutputStream(), true);
            // 输出的就是 HTTP 的报文协议
            printWriter.println("HTTP/1.1 200 OK");
            printWriter.println("Content-Type:text/html;charset=utf-8");
            // 模拟返回一个固定的字符串给客户端
            String body = "Hello, http server!";
            // 再输出一个请求头，显示的告诉客户端当前报文体的长度（用字节数表示）
            // 如果没有这个参数，客户端会不知道整个报文体到哪里是结束，导致出错
            printWriter.println("Content-Length:" + body.getBytes().length);
            // 输出一个空行，隔开"报文头"和"报文体"
            printWriter.println();

            // 写入报文体，并关闭输入流，结束与客户端的通信
            printWriter.write(body);
            printWriter.close();

            // 关闭 socket
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
