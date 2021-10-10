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

        // 模拟后端服务部署在同机房的两台机器上，内网IP分别为 localhost 和 127.0.0.1
        List<String> proxyServer = new ArrayList<>();
        proxyServer.add("http://localhost");
        proxyServer.add("http://127.0.0.1");

        try {
            // 启动服务
            new HttpInboundServer(port, proxyServer).run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
