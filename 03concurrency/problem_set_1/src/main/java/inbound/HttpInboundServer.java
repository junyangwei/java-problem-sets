package inbound;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.Data;

import java.util.List;

/**
 * Http 内部服务
 * @author junyangwei
 * @date 2021-10-09
 */
@Data
public class HttpInboundServer {
    /**
     * 定义端口
     */
    private int port;

    /**
     * 定义代理的服务列表
     */
    private List<String> proxyServers;

    /**
     * Http 内部服务构造器
     * @param port 监听端口
     * @param proxyServers 监听的代理服务列表
     */
    public HttpInboundServer(int port, List<String> proxyServers) {
        this.port = port;
        this.proxyServers = proxyServers;
    }

    /**
     * 启动器
     * @throws Exception
     */
    public void run() throws Exception {
        // 定义接收请求的事件处理器（2个线程）
        EventLoopGroup bossGroup = new NioEventLoopGroup(2);
        // 定义处理业务逻辑的事件处理器（16个线程）
        EventLoopGroup workerGroup = new NioEventLoopGroup(16);

        try {
            // 创建一个 ServerBootstrap 类对象，作为启动的入口点
            ServerBootstrap b = new ServerBootstrap();
            // 绑定 Channel 参数
            b.option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childOption(ChannelOption.SO_REUSEADDR, true)
                    .childOption(ChannelOption.SO_RCVBUF, 32 * 1024)
                    .childOption(ChannelOption.SO_SNDBUF, 32 * 1024)
                    .childOption(EpollChannelOption.SO_REUSEPORT, true)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);

            // 将 ServerBootstrap 类对象绑定前面两个 EventLoopGroup
            // 并且绑定另外一个 HttpInitializer 类对象
            b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.DEBUG))
                    .childHandler(new HttpInboundInitializer(this.proxyServers));

            // 把启动器入口点绑定上指定端口，并且开启它的channel，将服务器启动
            Channel ch = b.bind(port).sync().channel();
            System.out.println("开启 netty http 服务器，监听地址和端口 http://127.0.0.1:" + port + "/");
            ch.closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
