package inbound;

import filter.ProxyBizFilter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;

import java.util.List;

/**
 * Http 内部初始化程序
 * @author junyangwei
 * @date 2021-10-09
 */
public class HttpInboundInitializer extends ChannelInitializer<SocketChannel> {
    /**
     * 代理服务列表
     */
    private List<String> proxyServer;

    public HttpInboundInitializer(List<String> proxyServer) {
        this.proxyServer = proxyServer;
    }

    /**
     * 重写初始化通道方法
     * @param ch socket 通道
     */
    @Override
    public void initChannel(SocketChannel ch) {
        // 通过 SocketChannel 获取它的管道
        ChannelPipeline p = ch.pipeline();
        // 添加 HttpServer 的编码器
        p.addLast(new HttpServerCodec());
        // 再添加一个报文聚合器 HttpObjectAggregator
        p.addLast(new HttpObjectAggregator(1024 * 1024));
        // 最后添加一个 HttpHandler 自定义的处理器
        p.addLast(new HttpInboundHandler(this.proxyServer));

        System.out.println("#### Netty HTTP 服务端通信通道初始化成功.");
    }
}
