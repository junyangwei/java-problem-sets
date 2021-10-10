package outbound.netty4;

import filter.HeaderHttpResponseFilter;
import filter.HttpRequestFilter;
import filter.HttpResponseFilter;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import outbound.httpclient4.NamedThreadFactory;
import router.HttpEndpointRouter;
import router.RandomHttpEndpointRouter;

import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import static io.netty.handler.codec.http.HttpHeaderNames.CONNECTION;
import static io.netty.handler.codec.http.HttpHeaderValues.KEEP_ALIVE;
import static io.netty.handler.codec.http.HttpResponseStatus.NO_CONTENT;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * @author junyangwei
 * @date 2021-10-09
 */
public class NettyHttpClientOutboundHandler extends ChannelInboundHandlerAdapter {

    /**
     * 定义指定的代理服务
     */
    private ExecutorService proxyService;

    /**
     * 定义后端服务部署的内网地址列表
     */
    private List<String> backendUrls;

    /**
     * 定义 Http 响应头过滤器
     */
    HttpResponseFilter filter = new HeaderHttpResponseFilter();

    /**
     * 定义 路由
     */
    HttpEndpointRouter router = new RandomHttpEndpointRouter();

    NettyHttpClientOutboundHandler() {}

    /**
     * HTTP 外部处理器
     * @param backendUrls 后端服务 urls
     */
    public NettyHttpClientOutboundHandler(List<String> backendUrls) {
        this.backendUrls = backendUrls.stream()
                .map(this::formatUrl)
                .collect(Collectors.toList());

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

//        for (String url : this.backendUrls) {
//            String test01Url = System.getProperty("url", url + ":" +ApiTagEnum.TEST01.getPort());
//            String test02Url = System.getProperty("url", url + ":" +ApiTagEnum.TEST02.getPort());
//            try {
//                new NettyHttpClient().connect(test01Url);
//                new NettyHttpClient().connect(test02Url);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
    }

    /**
     * 处理 URL（去除结尾的 / 符号）
     * @param backend 后端 url
     * @return 去除结尾的/符号的 url
     */
    private String formatUrl(String backend) {
        return backend.endsWith("/") ? backend.substring(0, backend.length() - 1) : backend;
    }

    /**
     * 请求处理方法
     * @param fullHttpRequest 完整的 HTTP 请求
     * @param ctx 通道处理器上下文
     * @param requestFilter 请求过滤器
     * @param serverPort 代理的后端服务的端口
     */
    public void handle(final FullHttpRequest fullHttpRequest,
                       final ChannelHandlerContext ctx,
                       HttpRequestFilter requestFilter, int serverPort) {
        // 获取分配到的
        String backendUrl = router.route(this.backendUrls);
        // 拼接实际请求地址
        String url = backendUrl + ":" + serverPort + fullHttpRequest.uri();
        // 通过过滤器处理请求
        requestFilter.filter(fullHttpRequest, ctx);
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
            new NettyHttpClient().connect(url);
        } catch (Exception e) {
            e.printStackTrace();
        }
//        FullHttpRequest request = new DefaultFullHttpRequest(
//                HTTP_1_1, HttpMethod.GET, url);
//        request.headers()
//                .add(CONNECTION, KEEP_ALIVE)
//                .add(HttpHeaderNames.CONTENT_LENGTH, request.content().readableBytes());
//
//        ctx.writeAndFlush(request);
    }

    static final String URL = System.getProperty("url", "http://127.0.0.1:8801/test01api/get");

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg)  throws Exception {
       System.out.println("msg -> " + msg);
        if (msg instanceof HttpResponse) {
            HttpResponse response = (HttpResponse) msg;

            System.err.println("STATUS: " + response.getStatus());
            System.err.println("VERSION: " + response.getProtocolVersion());
            System.err.println();

            if (!response.headers().isEmpty()) {
                for (String name : response.headers().names()) {
                    for (String value : response.headers().getAll(name)) {
                        System.err.println("HEADER: " + name + " = " + value);
                    }
                }
                System.err.println();
            }

            if (HttpHeaders.isTransferEncodingChunked(response)) {
                System.err.println("CHUNKED CONTENT {");
            } else {
                System.err.println("CONTENT {");
            }
        }

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
}
