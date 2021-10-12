package outbound.netty4;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.ssl.SslContext;

/**
 * Netty Http 客户端初始化程序
 * @author junyangwei
 * @date 2021-10-10
 */
public class NettyHttpInitializer extends ChannelInitializer<SocketChannel> {

    private final ChannelHandlerContext serverCtx;

    private final SslContext sslCtx;

    public NettyHttpInitializer(SslContext sslCtx, ChannelHandlerContext serverCtx) {
        this.sslCtx = sslCtx;
        this.serverCtx = serverCtx;
    }


    /**
     * 重写父类的 initChannel 方法
     * @param ch
     */
    @Override
    public void initChannel(SocketChannel ch) {
        // 通过 SocketChannel 获取它的管道
        ChannelPipeline p = ch.pipeline();

        // 必要情况下允许 HTTPS
        if (sslCtx != null) {
            p.addLast(sslCtx.newHandler(ch.alloc()));
        }

        // 客户端发送的是httpRequest，所以要使用HttpRequestEncoder进行编码
        p.addLast(new HttpRequestEncoder());

        // 客户端接收到的是httpResponse响应，所以要使用HttpResponseDecoder进行解码
        p.addLast(new HttpResponseDecoder());

        // 如果不需要压缩内容，可以直接删除下一行
        p.addLast(new HttpContentDecompressor());

        // 如果不想处理 Http 的内容，就注释掉下一行（限制内容的最常的长度为 1024 * 1024 个字节）
        p.addLast(new HttpObjectAggregator(1024 * 1024));

        // 添加自定义 Netty Http 客户端处理器
        p.addLast(new NettyHttpClientOutboundHandler(this.serverCtx));
    }
}
