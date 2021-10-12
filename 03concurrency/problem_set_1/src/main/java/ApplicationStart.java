import inbound.HttpInboundServer;

import java.util.ArrayList;
import java.util.List;

/**
 * netty 网关服务启动类
 * @author junyangwei
 * @date 2021-10-10
 */
public class ApplicationStart {
    public static void main(String[] args) {
        // 定义 netty 网关绑定的端口是8800
        int port = 8800;

        // 模拟服务器上部署的两个不同的后端服务 host
        List<String> proxyServer = new ArrayList<>();
        proxyServer.add("http://127.0.0.1:8801/");
        proxyServer.add("http://127.0.0.1:8802/");

        System.out.println("#### 当前代理的后端服务 HOST 为：" + proxyServer.toString());

        try {
            // 启动服务
            System.out.println("#### Netty HTTP 服务端准备启动...");
            new HttpInboundServer(port, proxyServer).run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
