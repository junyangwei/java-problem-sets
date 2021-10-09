package netty;

import inbound.HttpInboundServer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * @author junyangwei
 * @date 2021-10-09
 */
public class NettyHttpServer {
    public static void main1(String[] args) {
        // 定义绑定的端口是8808
        int port = 8808;

        EventLoopGroup bossGroup = new NioEventLoopGroup(2);
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
//                    .childOption(EpollChannelOption.SO_REUSEPORT, true)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);

            // 将 ServerBootstrap 类对象绑定前面两个 EventLoopGroup
            // 并且绑定另外一个 HttpInitializer 类对象
            b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler((LogLevel.INFO)))
                    .childHandler(new HttpInitializer());

            // 把启动器入口点绑定上8808端口，并且开启它的channel，将服务器启动
            Channel ch = b.bind(port).sync().channel();
            System.out.println("开启netty http服务器，监听地址和端口为 http://127.0.0.1:" + port + "/");
            ch.closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        // 定义绑定的端口是8808
        int port = 8808;
        List<String> proxyServer = new ArrayList<>();
        proxyServer.add("localhost");

        try {
            new HttpInboundServer(port, proxyServer).run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
