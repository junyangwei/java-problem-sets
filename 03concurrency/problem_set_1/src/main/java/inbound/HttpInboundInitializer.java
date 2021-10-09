package inbound;

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
        // 定义通道传输 p
        ChannelPipeline p = ch.pipeline();
        p.addLast(new HttpServerCodec());
        p.addLast(new HttpObjectAggregator(1024 * 1024));
        p.addLast(new HttpInboundHandler(this.proxyServer));
    }
}
