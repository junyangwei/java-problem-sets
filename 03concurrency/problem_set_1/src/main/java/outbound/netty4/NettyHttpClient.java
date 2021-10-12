package outbound.netty4;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;

import java.net.URI;

import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * Netty HTTP 客户端
 * 参考：https://netty.io/4.0/xref/io/netty/example/http/snoop/package-summary.html
 * @author junyangwei
 * @date 2021-10-10
 */
public class NettyHttpClient {

    /**
     * 连接指定服务，并发送 GET 请求
     * @param url 指定服务完整的 url，如 http://localhost:8801/
     * @throws Exception 异常信息
     */
    public void connect(String url, ChannelHandlerContext serverCtx) throws Exception {
        // 解析 url，获取 host 以及 端口，并且兼容 https
        URI uri = new URI(url);
        String scheme = uri.getScheme() == null ? "http" : uri.getScheme();
        String host = uri.getHost() == null ? "127.0.0.1" : uri.getHost();
        int port = uri.getPort();
        if (!"http".equalsIgnoreCase(scheme) && !"https".equalsIgnoreCase(scheme)) {
            System.err.println("只有 HTTP(S) 被允许。");
            return;
        }

        // 必要情况下，配置 SSL 内容
        final boolean ssl = "https".equalsIgnoreCase(scheme);
        final SslContext sslCtx;
        if (ssl) {
            sslCtx = SslContextBuilder.forClient()
                    .trustManager(InsecureTrustManagerFactory.INSTANCE).build();
        } else {
            sslCtx = null;
        }

        // 配置客户端
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
//                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .handler(new NettyHttpInitializer(sslCtx, serverCtx));

            // 建立连接
            Channel ch = b.connect(host, port).sync().channel();

            // 准备 HTTP 请求 TODO: 请求参数，POST 请求，复用通道（单例）
            FullHttpRequest request = new DefaultFullHttpRequest(
                    HTTP_1_1, HttpMethod.GET, uri.getRawPath());
            request.headers()
                    .add(HttpHeaderNames.HOST, host)
                    .add(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE)
                    .add(HttpHeaderNames.CONTENT_LENGTH, request.content().readableBytes());

            // 发送 HTTP 请求
            ch.writeAndFlush(request);

            // 关闭连接
            ch.closeFuture().sync();
        } finally {
            // 结束线程
            group.shutdownGracefully();
        }
    }

    /**
     * 单元测试
     */
    public static void main(String[] args) throws Exception {
        String test01Url = System.getProperty("url", "http://127.0.0.1:8801/test01api/get");
//        new NettyHttpClient().connect(test01Url);
    }
}
