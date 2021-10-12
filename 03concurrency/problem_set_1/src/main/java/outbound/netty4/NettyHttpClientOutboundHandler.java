package outbound.netty4;

import filter.HeaderHttpResponseFilter;
import filter.HttpRequestFilter;
import filter.HttpResponseFilter;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.DecoderResult;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.cookie.ServerCookieDecoder;
import io.netty.handler.codec.http.cookie.ServerCookieEncoder;
import io.netty.util.CharsetUtil;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import outbound.httpclient4.NamedThreadFactory;
import router.HttpEndpointRouter;
import router.RandomHttpEndpointRouter;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import static io.netty.handler.codec.http.HttpHeaderNames.*;
import static io.netty.handler.codec.http.HttpHeaderValues.KEEP_ALIVE;
import static io.netty.handler.codec.http.HttpResponseStatus.*;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * @author junyangwei
 * @date 2021-10-09
 */
public class NettyHttpClientOutboundHandler extends ChannelInboundHandlerAdapter {

    /**
     * 执行线程服务
     */
    private ExecutorService proxyService;

    /**
     * 用户发送请求给 Netty 服务端的通道处理器上下文
     * 用于最终通过 Netty 服务端响应用户的请求
     */
    private final ChannelHandlerContext serverCtx;

    /**
     * HTTP 外部处理器构造函数
     */
    public NettyHttpClientOutboundHandler(ChannelHandlerContext serverCtx) {
        this.serverCtx = serverCtx;

        System.out.println("#### 构造 Netty HTTP 客户端线程池...");
        // 获取当前 JVM 虚拟机可用核心数
        int cores = Runtime.getRuntime().availableProcessors();
        // 定义保持连接时间
        long keepAliveTime = 1000;
        // 定义队列长度
        int queueSize = 2048;

        // 定义被拒绝任务的处理器
        RejectedExecutionHandler handler = new ThreadPoolExecutor.CallerRunsPolicy();
        // 初始化线程池，设置线程池数量为 JVM 可用核心数
        proxyService = new ThreadPoolExecutor(cores, cores,
                keepAliveTime, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(queueSize),
                new NamedThreadFactory("proxyService"), handler);
    }

    /**
     * 请求处理方法
     * @param fullHttpRequest 完整的 HTTP 请求
     * @param ctx 通道处理器上下文
     */
    public void handle(final FullHttpRequest fullHttpRequest,
                       final ChannelHandlerContext ctx,
                       String serverHost) {
        // 获取代理的后端路径
        String url = serverHost + fullHttpRequest.uri();
        // 发送请求到分配的后端服务（做了一个代理）TODO: 区分 GET 和 POST 请求
        proxyService.submit(() -> fetchGet(ctx, url));
    }

    /**
     * 处理 GET 请求
     * @param ctx 通道处理器上下文
     * @param url 请求 URL
     */
    private void fetchGet(final ChannelHandlerContext ctx, final String url) {
        try {
            new NettyHttpClient().connect(url, ctx);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
    }

    /**
     * Netty 的 HTTP 客户端接收服务器的响应
     * @param ctx 通道处理器上下文
     * @param msg 响应信息
     * @throws Exception
     */
    public void channelRead1(ChannelHandlerContext ctx, Object msg)  {
        System.out.println(ctx);
        System.out.println(this.serverCtx);
        // 简单一点，不做多余的处理，直接响应请求
        if (msg instanceof HttpContent) {
            HttpContent content = (HttpContent) msg;

            System.err.print(content.content().toString(CharsetUtil.UTF_8));
            System.err.flush();

            if (content instanceof LastHttpContent) {
                System.err.println("} END OF CONTENT");
                ctx.close();
            }
        }
        if (msg instanceof FullHttpResponse) {
            FullHttpResponse response = (FullHttpResponse) msg;

            ByteBuf buf = response.content();

            FullHttpResponse result = new DefaultFullHttpResponse(
                    HTTP_1_1, response.decoderResult().isSuccess() ? OK : BAD_REQUEST,
                    Unpooled.copiedBuffer(buf.toString(), CharsetUtil.UTF_8));
            result.headers().set(CONTENT_TYPE, response.headers().get("content-type"));
            result.headers().set(CONTENT_LENGTH, buf.readableBytes());

            System.out.println("内容打印" + result.content());
            System.out.println(this.serverCtx.channel().isRegistered());
            System.out.println(this.serverCtx.channel().isOpen());
            System.out.println(this.serverCtx.channel().isWritable());
            System.out.println(this.serverCtx.channel().isActive());
            this.serverCtx.write(result);
            ctx.close();

            buf.release();
            return;
        }
        if (msg instanceof FullHttpResponse) {
//            ByteBuf buf = response.content();
//            String result = buf.toString(CharsetUtil.UTF_8);
//            System.out.println("response -> " + result);

            // 定义完整的 Http 响应 FullHttpResponse 对象
            FullHttpResponse response = null;
            try {
                response = (FullHttpResponse) msg;
                System.out.println(response);
            } catch (Exception e) {
                // 若失败，同样模拟一个 Http 请求响应结果："HTTP/1.1"，"状态码为204"
                response = new DefaultFullHttpResponse(HTTP_1_1, NO_CONTENT);
            } finally {
                // 最后的处理逻辑，当 fullHttpRequest 存在时进行
                if (response != null) {
                    // 校验当前 Http 请求连接状态
                    if (!HttpUtil.isKeepAlive(response)) {
                        // 若不再连接，则直接关闭通道的监听状态
                        ctx.write(response).addListener(ChannelFutureListener.CLOSE);
                    } else {
                        // 否则，设置响应头：连接仍然存在
                        response.headers().set(CONNECTION, KEEP_ALIVE);
                        // 对通道处理器上下文，写入响应内容
                        ctx.write(response);
                    }
                }
            }
        } else {
            this.channelActive(ctx);
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        // 对请求信息做解析
        HttpResponse request = null;
        StringBuilder buf = new StringBuilder();
        if (msg instanceof HttpResponse) {
            // 将 msg 强制转换为 HttpRequest 类对象，获取本次请求
            request = (HttpResponse) msg;

            // 重置 buf 长度，并且拼接版本，主机名和请求地址到 buf 中
            buf.setLength(0);
            buf.append("欢迎来到示例网络服务器\r\n");
            buf.append("===================\r\n");

            buf.append("版本：").append(request.protocolVersion()).append("\r\n");
            buf.append("主机名：").append(request.headers().get(HttpHeaderNames.HOST, "未知")).append("\r\n");
            buf.append("状态码：").append(request.status()).append("\r\n\r\n");

            // 提取请求头的信息，若不为空则拼接到 buf 中
            HttpHeaders headers = request.headers();
            if (!headers.isEmpty()) {
                for (Map.Entry<String, String> h : headers) {
                    String key = h.getKey();
                    String value = h.getValue();
                    buf.append("请求头：").append(key).append(" = ").append(value).append("\r\n");
                }
                buf.append("\r\n");
            }

            // 拼接解码结果
            appendDecoderResult(buf, request);
        }

        // 对请求体做解析
        if (msg instanceof HttpContent) {
            HttpContent httpContent = (HttpContent) msg;

            ByteBuf content = httpContent.content();
            if (content.isReadable()) {
                buf.append("内容：");
                buf.append(content.toString(CharsetUtil.UTF_8));
                buf.append("\r\n");
                appendDecoderResult(buf, request);
            }

            if (msg instanceof LastHttpContent) {
                buf.append("内容的结尾\r\n");

                LastHttpContent trailer = (LastHttpContent) msg;
                if (!trailer.trailingHeaders().isEmpty()) {
                    buf.append("\r\n");
                    for (String name: trailer.trailingHeaders().names()) {
                        for (String value : trailer.trailingHeaders().getAll(name)) {
                            buf.append("结尾头：");
                            buf.append(name).append(" = ").append(value).append("\r\n");
                        }
                    }
                    buf.append("\r\n");
                }

                // 写入响应给客户端的响应体，并且获取是否需要保持连接
                boolean keepAlive = writeResponse(trailer, this.serverCtx, request, buf);

                // 若不需要保持连接，则编写完毕后直接关闭连接
                if (!keepAlive) {
                    ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
                }
            }

            // 生成ByteBuf对象后，需要调用release方法，防止出现内存泄漏
            // 详情参考：https://www.cnblogs.com/zhaoshizi/p/13122467.html
            content.release();
        }
    }

    /**
     * 拼接当前 HTTP 请求解码的结果
     * @param buf 响应内容缓存的对象
     * @param o HTTP 请求对象
     */
    private static void appendDecoderResult(StringBuilder buf, HttpObject o) {
        DecoderResult result = o.decoderResult();
        if (result.isSuccess()) {
            return;
        }

        buf.append(".. 解码失败：");
        buf.append(result.cause());
        buf.append("\r\n");
    }

    /**
     * 写入响应给客户端的响应体，并且返回是否需要保持连接状态
     * @param currentObj Http 请求对象
     * @param ctx 通道处理器上下文
     * @return 是否需要保持连接状态
     */
    private boolean writeResponse(HttpObject currentObj, ChannelHandlerContext ctx, HttpResponse request, StringBuilder buf) {
        // 根据当前请求头信息，决定是否需要保持连接状态
        boolean keepAlive = HttpUtil.isKeepAlive(request);

        // 建立响应对象（响应内容直接使用当前解析的请求头以及请求体）
        FullHttpResponse response = new DefaultFullHttpResponse(
                HTTP_1_1, currentObj.decoderResult().isSuccess() ? OK : BAD_REQUEST,
                Unpooled.copiedBuffer(buf.toString(), CharsetUtil.UTF_8));

        // 设置请求头 —— 内容类型为文本，格式为UTF-8
        response.headers().set(CONTENT_TYPE, "text/plain; charset=UTF-8");

        if (true) {
            // 当 keep-alive 参数开启(保持连接状态），则添加 'Content-Length' 头
            response.headers().set(CONTENT_LENGTH, response.content().readableBytes());
            // 按要求添加保持连接的头
            response.headers().set(CONNECTION, HttpHeaderValues.KEEP_ALIVE);
        }

        // 提取请求头中的 cookie，若存在，那么响应头也设置相同的 cookies
        String cookieString = request.headers().get(COOKIE);
        if (cookieString != null) {
            Set<io.netty.handler.codec.http.cookie.Cookie> cookies = ServerCookieDecoder.LAX.decode(cookieString);

            if (!cookies.isEmpty()) {
                // 在需要的情况下，可以重新设置响应头的 cookie
                for (io.netty.handler.codec.http.cookie.Cookie cookie : cookies) {
                    response.headers().add(SET_COOKIE, io.netty.handler.codec.http.cookie.ServerCookieEncoder.LAX.encode(cookie));
                }
            }
        } else {
            // 若请求没有携带 cookie，则可以自定义一些响应的 cookie
            response.headers().add(SET_COOKIE, io.netty.handler.codec.http.cookie.ServerCookieEncoder.LAX.encode("key1", "value1"));
            response.headers().add(SET_COOKIE, ServerCookieEncoder.LAX.encode("key2", "value2"));
        }

        // 编写响应（最终响应客户端请求的操作）
        // 关于 write 方法可参见：https://stackoverflow.com/questions/52794066/can-anyone-explain-netty-channelhandlercontext-flush
        ctx.write(response);
        System.out.println(buf);

        return keepAlive;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
